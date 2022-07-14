#include "FFmpegCore.h"
#include "pepelu_log.h"
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include "libswresample/swresample.h"
#include "libavutil/samplefmt.h"
#include "libavutil/opt.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

uint8_t *outputBuffer;
size_t outputBufferSize;

AVPacket *packet;
int audioStream;
AVFrame *avFrame;
SwrContext *swrCtx;
AVFormatContext *avFormatCtx;
AVCodecContext *avCodecCtx;

int createAudioPlayer(int *rate, int *channel, const char *url) {
    int ret = 0;
    av_register_all();
    avFormatCtx = avformat_alloc_context();
    LOGI("init ffmpeg url=%s\n", url);

    //open audio file
    ret = avformat_open_input(&avFormatCtx, url, NULL, NULL);
    if (ret != 0) {
        LOGE("could not open file:%s\n", url);
        return -1;
    }

    // Retrieve stream information 检索流信息
    ret = avformat_find_stream_info(avFormatCtx, NULL);
    if (ret < 0) {
        LOGE("could not find stream information.\n");
        return -1;
    }

    //Find the first audio stream
    audioStream = -1;
    for (int i = 0; i < avFormatCtx->nb_streams; i++) {
        if (avFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            audioStream = i;
            break;
        }
    }
    LOGI("audio stream : %d", audioStream);
    if (audioStream == -1) {
        LOGE("could not find audio stream!\n")
        return -1;
    }

    // Get a pointer to the codec context for the video stream
    // 得到音频流解码器的上下文指针
    AVCodecParameters *avCodecParameters = avFormatCtx->streams[audioStream]->codecpar;
    // Find the decoder for the audio stream
    AVCodec *avCodec = avcodec_find_decoder(avCodecParameters->codec_id);
    if (!avCodec) {
        LOGE("unsupported codec. \n")
        return -1;
    }

    //trans avCodecParameters to avCodecCtx
    avCodecCtx = avcodec_alloc_context3(NULL);
    if (!avCodecCtx) {
        LOGE("alloc avCodecCtx fail.\n");
        return -1;
    }
    ret = avcodec_parameters_to_context(avCodecCtx, avCodecParameters);
    if (ret < 0) {
        LOGE("avcodec_parameters_to_context fail.\n");
        return -1;
    }

    // 打开解码器
    ret = avcodec_open2(avCodecCtx, avCodec, NULL);
    if (ret < 0) {
        LOGE("Could not open codec.\n");
        return -1; // Could not open codec 打开解码器失败
    }

    avFrame = av_frame_alloc();
    //设置格式转换
    swrCtx = swr_alloc();
    av_opt_set_int(swrCtx, "in_channel_layout", avCodecCtx->channel_layout, 0);
    av_opt_set_int(swrCtx, "out_channel_layout", avCodecCtx->channel_layout, 0);
    av_opt_set_int(swrCtx, "in_sample_rate", avCodecCtx->sample_rate, 0);
    av_opt_set_int(swrCtx, "out_sample_rate", avCodecCtx->sample_rate, 0);
    av_opt_set_sample_fmt(swrCtx, "in_sample_fmt", avCodecCtx->sample_fmt, 0);
    av_opt_set_sample_fmt(swrCtx, "out_sample_fmt", AV_SAMPLE_FMT_S16, 0);
    swr_init(swrCtx);

    // 分配PCM数据缓存
    outputBufferSize = 8196;
    outputBuffer = (uint8_t *) malloc(sizeof(uint8_t) * outputBufferSize);

    // 返回sample rate和channels
    *rate = avCodecCtx->sample_rate;
    *channel = avCodecCtx->channels;
    return 0;
}

// 获取PCM数据, 自动回调获取
int getPCM(void **pcm, size_t *pcmSize) {
    int ret = 0;
    //LOGI("getPCM.\n");
    packet = av_packet_alloc();
    while (av_read_frame(avFormatCtx, packet) >= 0) {
        if (packet->stream_index == audioStream) {
            ret = avcodec_send_packet(avCodecCtx, packet);
            if (ret < 0) {
                LOGE("audio Error sending a packet for decoding.");
                break;
            }
            while (avcodec_receive_frame(avCodecCtx, avFrame) == 0) {
                // data_size为音频数据所占的字节数
                int data_size = av_samples_get_buffer_size(
                        avFrame->linesize, avCodecCtx->channels,
                        avFrame->nb_samples, avCodecCtx->sample_fmt, 1);
                // LOGI(">> while getPcm data_size=%d\n", data_size);
                // 内存再分配
                if (data_size > outputBufferSize) {
                    outputBufferSize = (size_t) data_size;
                    outputBuffer = realloc(outputBuffer, sizeof(uint8_t) * outputBufferSize);
                }
                // 音频格式转换
                swr_convert(swrCtx, &outputBuffer, avFrame->nb_samples,
                            (uint8_t const **) (avFrame->extended_data),
                            avFrame->nb_samples);
                // 返回pcm数据
                *pcm = outputBuffer;
                *pcmSize = (size_t) data_size;
                return 0;
            }
        }
    }
    //play end
    LOGI("get pcm end...\n");
    return -1;
}

// 释放相关资源
int releaseAudioPlayer() {
    if (NULL != packet) {
        av_packet_unref(packet);
    }
    if (NULL != outputBuffer) {
        av_free(outputBuffer);
    }
    if (NULL != avFrame) {
        av_free(avFrame);
    }
    avcodec_close(avCodecCtx);
    avformat_close_input(&avFormatCtx);
    return 0;
}