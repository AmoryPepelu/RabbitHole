#include "MediaPlayer.h"
#include "media_log.h"
#include "mp_common.h"
#include "AudioDataProvider.h"
#include "VideoDataProvider.h"
#include <unistd.h>

inline char *GetAVErrorMsg(int code) {
    static char err_msg[1024];
    av_strerror(code, err_msg, sizeof(err_msg));
    return err_msg;
}

//player
MediaPlayer::MediaPlayer() : state_(MPState::Idle),
                             ic_(nullptr),
                             eof(0),
                             video_stream_(-1),
                             audio_stream_(-1),
                             event_cb_(nullptr),
                             audio_clock(0) {
    av_log_set_callback(ff_log_callback);
    av_log_set_level(AV_LOG_DEBUG);
    av_register_all();
    avformat_network_init();
}

MediaPlayer::~MediaPlayer() {
    if (ic_) {
        avformat_free_context(ic_);
        ic_ = nullptr;
    }
}

int MediaPlayer::SetDataSource(const std::string &path) {
    if (state_ != MPState::Idle) {
        LOGE("illegal state|current:%d", state_);
        return ERROR_ILLEGAL_STATE;
    }
    LOGI("source path:%s", path.c_str());
    path_ = path;
    state_ = MPState::Initialized;
    return SUCCESS;
}

int MediaPlayer::PrepareAsync() {
    if (state_ != MPState::Initialized && state_ != MPState::Stoped) {
        LOGE("illegal state|current:%d", state_);
        return ERROR_ILLEGAL_STATE;
    }
    state_ = MPState::Preparing;
    read_thread_.reset(new std::thread(&MediaPlayer::read, this));
    return SUCCESS;
}

void MediaPlayer::SetEventCallback(EventCallback *cb) {
    event_cb_ = cb;
}

/**
 * 音频
 * @param buffer
 * @param buffer_size
 */
void MediaPlayer::GetData(uint8_t **buffer, int &buffer_size) {
    if (audio_buffer_ == nullptr) return;
    auto frame = audio_frames_.Get();
    int next_size;
    if (audio_codec_ctx_->sample_fmt == AV_SAMPLE_FMT_S16P) {
        next_size = av_samples_get_buffer_size(frame->linesize, audio_codec_ctx_->channels,
                                               audio_codec_ctx_->frame_size,
                                               audio_codec_ctx_->sample_fmt, 1);
    } else {
        av_samples_get_buffer_size(&next_size, audio_codec_ctx_->channels,
                                   audio_codec_ctx_->frame_size, audio_codec_ctx_->sample_fmt, 1);
    }
    int ret = swr_convert(swr_ctx_, &audio_buffer_, frame->nb_samples,
                          (uint8_t const **) (frame->extended_data),
                          frame->nb_samples);
    audio_clock = frame->pts * av_q2d(audio_st_->time_base);
    av_frame_unref(frame);
    av_frame_free(&frame);
    *buffer = audio_buffer_;
    buffer_size = next_size;
}

/**
 * 视频
 * @param buffer
 * @param frame
 * @param width
 * @param height
 */
void MediaPlayer::GetData(uint8_t **buffer, AVFrame **frame, int &width, int &height) {
    auto m_frame = video_frames_.Get();
    sws_scale(img_convert_ctx_, (const uint8_t *const *) m_frame->data, m_frame->linesize, 0,
              video_codec_ctx_->height,
              frame_rgba_->data, frame_rgba_->linesize);
    double timestamp = av_frame_get_best_effort_timestamp(m_frame) * av_q2d(video_st_->time_base);
    if (timestamp > audio_clock) {
        usleep((unsigned long) ((timestamp - audio_clock) * 1000000));
    }
    *frame = frame_rgba_;
    *buffer = rgba_buffer_;
    width = video_codec_ctx_->width;
    height = video_codec_ctx_->height;
    av_frame_unref(m_frame);
    av_frame_free(&m_frame);
}

void MediaPlayer::read() {
    int err;
    char err_buff[1024];
    err = avformat_open_input(&ic_, path_.c_str(), nullptr, nullptr);
    if (err) {
        av_strerror(err, err_buff, sizeof(err_buff));
        LOGE("avformat_open_input failed|ret:%d|msg:%s", err, err_buff);
        return;
    }
    err = avformat_find_stream_info(ic_, nullptr);
    if (err < 0) {
        LOGE("could not find codec parameters");
        return;
    }
    for (int i = 0; i < ic_->nb_streams; i++) {
        if (ic_->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            audio_stream_ = i;
            LOGI("find audio stream index %d", i);
        }
        if (ic_->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            video_stream_ = i;
            LOGI("find video stream index %d", i);
        }
    }
    // start audio decode thread
    if (audio_stream_ >= 0) {
        openStream(audio_stream_);
    }
    // start video decode thread
    if (video_stream_ >= 0) {
        openStream(video_stream_);
    }

    auto *pkt = (AVPacket *) av_malloc(sizeof(AVPacket));
    if (pkt == nullptr) {
        LOGE("Could not allocate avPacket");
        return;
    }
    while (true) {
        err = av_read_frame(ic_, pkt);
        if (err < 0) {
            if ((err == AVERROR_EOF || avio_feof(ic_->pb))) {
                eof = 1;
            }
            if (ic_->pb && ic_->pb->error)
                break;
        }
        if (pkt->stream_index == audio_stream_) {
            audio_packets_.Put(pkt);
        } else if (pkt->stream_index == video_stream_) {
            video_packets_.Put(pkt);
        } else {
            av_packet_unref(pkt);
        }
    }
}

void MediaPlayer::decodeAudio() {
    AVPacket pkt;
    AVFrame *frame = av_frame_alloc();
    while (true) {
        audio_packets_.Get(&pkt);
        int ret;
        ret = avcodec_send_packet(audio_codec_ctx_, &pkt);
        if (ret < 0 && ret != AVERROR(EAGAIN) && ret != AVERROR_EOF) {
            LOGE("avcodec_send_packet error|code:%d|msg:%s", ret, GetAVErrorMsg(ret));
            break;
        }
        ret = avcodec_receive_frame(audio_codec_ctx_, frame);
        if (ret < 0 && ret != AVERROR_EOF) {
            LOGE("avcodec_receive_frame error|code:%d|msg:%s", ret, GetAVErrorMsg(ret));
            if (ret == -11) {
                continue;
            }
            break;
        }
        audio_frames_.Put(frame);
        if (audio_frames_.Size() >= MP_AUDIO_READY_SIZE && state_ == MPState::Preparing) {
            // TODO illegal state check
            state_ = MPState::Prepared;
            if (event_cb_) {
                event_cb_->onPrepared();
            }
        }
    }
}

/**
 * 解码视频
 */
void MediaPlayer::decodeVideo() {
    LOGI("start decode video");
    AVPacket pkt;
    AVFrame *frame = av_frame_alloc();
    while (true) {
        video_packets_.Get(&pkt);
        int ret;
        ret = avcodec_send_packet(video_codec_ctx_, &pkt);
        if (ret < 0 && ret != AVERROR(EAGAIN) && ret != AVERROR_EOF) {
            LOGE("avcodec_send_packet error|code:%d|msg:%s", ret, GetAVErrorMsg(ret));
            break;
        }
        ret = avcodec_receive_frame(video_codec_ctx_, frame);
        if (ret < 0 && ret != AVERROR_EOF) {
            LOGE("avcodec_receive_frame error|code:%d|msg:%s", ret, GetAVErrorMsg(ret));
            if (ret == -11) {
                continue;
            }
            break;
        }
        video_frames_.Put(frame);
    }
}

/**
 *
 * @param index
 */
void MediaPlayer::openStream(int index) {
    LOGI("open stream|index:%d", index);
    AVCodecContext *avctx;
    AVCodec *codec;
    int ret = 0;
    if (index < 0 || index >= ic_->nb_streams)
        return;
    avctx = avcodec_alloc_context3(nullptr);
    if (!avctx) {
        LOGE("can not alloc codec ctx");
        return;
    }
    ret = avcodec_parameters_to_context(avctx, ic_->streams[index]->codecpar);
    if (ret < 0) {
        avcodec_free_context(&avctx);
        LOGE("avcodec_parameters_to_context error %d", ret);
        return;
    }
    av_codec_set_pkt_timebase(avctx, ic_->streams[index]->time_base);
    codec = avcodec_find_decoder(avctx->codec_id);
    avctx->codec_id = codec->id;
    ic_->streams[index]->discard = AVDISCARD_DEFAULT;
    ret = avcodec_open2(avctx, codec, NULL);
    if (ret < 0) {
        LOGE("Fail to open codec on stream:%d|code:%d", index, ret);
        avcodec_free_context(&avctx);
        return;
    }
    switch (avctx->codec_type) {
        case AVMEDIA_TYPE_AUDIO:
            swr_ctx_ = swr_alloc();
            swr_ctx_ = swr_alloc_set_opts(NULL,
                                          avctx->channel_layout, AV_SAMPLE_FMT_S16,
                                          avctx->sample_rate,
                                          avctx->channel_layout, avctx->sample_fmt,
                                          avctx->sample_rate,
                                          0, NULL);
            if (!swr_ctx_ || swr_init(swr_ctx_) < 0) {
                LOGE("Cannot create sample rate converter for conversion channels!");
                swr_free(&swr_ctx_);
                return;
            }
            audio_st_ = ic_->streams[index];
            audio_codec_ctx_ = avctx;
            audio_buffer_ = (uint8_t *) malloc(sizeof(uint8_t) * MP_AUDIO_BUFFER_SIZE);
            initAudioPlayer(audio_codec_ctx_->sample_rate, audio_codec_ctx_->channels, this);
            audio_decode_thread_.reset(new std::thread(&MediaPlayer::decodeAudio, this));
            break;
        case AVMEDIA_TYPE_VIDEO:
            video_st_ = ic_->streams[index];
            img_convert_ctx_ = sws_getContext(avctx->width, avctx->height, avctx->pix_fmt,
                                              avctx->width, avctx->height, AV_PIX_FMT_RGBA,
                                              SWS_BICUBIC, nullptr, nullptr, nullptr);
            video_codec_ctx_ = avctx;
            frame_rgba_ = av_frame_alloc();
            int numBytes = av_image_get_buffer_size(AV_PIX_FMT_RGBA, avctx->width, avctx->height,
                                                    1);
            rgba_buffer_ = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));
            av_image_fill_arrays(frame_rgba_->data, frame_rgba_->linesize, rgba_buffer_,
                                 AV_PIX_FMT_RGBA, avctx->width, avctx->height, 1);
            video_decode_thread_.reset(new std::thread(&MediaPlayer::decodeVideo, this));
            break;
    }
}

int MediaPlayer::Start() {
    if (state_ != MPState::Prepared) {
        LOGE("illegal state|current:%d", state_);
        return ERROR_ILLEGAL_STATE;
    }
    audio_render_thread_.reset(new std::thread(startAudioPlay));
    video_render_thread_.reset(new std::thread(videoRender));
    state_ = MPState::Started;
    return SUCCESS;
}

int MediaPlayer::Pause() {
    if (state_ != MPState::Started) {
        LOGE("illegal state|current:%d", state_);
        return ERROR_ILLEGAL_STATE;
    }
    stopAudioPlay();
    state_ = MPState::Paused;
    return SUCCESS;
}

int MediaPlayer::GetVideoWidth() {
    if (video_codec_ctx_)
        return video_codec_ctx_->width;
    return 0;
}

int MediaPlayer::GetVideoHeight() {
    if (video_codec_ctx_)
        return video_codec_ctx_->height;
    return 0;
}