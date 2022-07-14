#include <jni.h>
#include <stdlib.h>
#include <unistd.h>

#include "audio_player_audio_track.h"
#include "pepelu_log.h"

//封装格式
#include "libavformat/avformat.h"
//解码
#include "libavcodec/avcodec.h"
//缩放
#include "libswscale/swscale.h"
//重采样
#include "libswresample/swresample.h"

#define MAX_AUDIO_FRAME_SIZE 8196

int playAudioByAudioTrack(JNIEnv *env, const char *url) {
    LOGI("playAudioByAudioTrack input url:%s", url);
    int ret = 0;

    //注册组件
    av_register_all();

    AVFormatContext *avFormatContext = avformat_alloc_context();
    //打开音频文件
    ret = avformat_open_input(&avFormatContext, url, NULL, NULL);
    if (ret != 0) {
        LOGI("open input fail.\n");
        return -1;
    }

    //获取输入文件信息
    ret = avformat_find_stream_info(avFormatContext, NULL);
    if (ret < 0) {
        LOGI("无法获取输入文件信息.\n");
        return -1;
    }

    //获取音频流索引位置
    int audio_stream_index = -1;
    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        if (avFormatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            audio_stream_index = i;
            break;
        }
    }

    //获取音频解码器
    AVCodecParameters *avCodecParameters = avFormatContext->streams[audio_stream_index]->codecpar;
    AVCodec *avCodec = avcodec_find_decoder(avCodecParameters->codec_id);
    if (NULL == avCodec) {
        LOGI("无法获取解码器\n");
        return -1;
    }

    AVCodecContext *avCodecContext = avcodec_alloc_context3(NULL);
    avcodec_parameters_to_context(avCodecContext, avCodecParameters);

    //打开解码器
    ret = avcodec_open2(avCodecContext, avCodec, NULL);
    if (ret < 0) {
        LOGI("%s", "无法打开解码器");
        return -1;
    }

    //压缩数据
    AVPacket *avPacket = av_packet_alloc();
    //解压缩数据
    AVFrame *avFrame = av_frame_alloc();
    //frame->16bit 44100 PCM 统一音频采样格式与采样率
    SwrContext *swrContext = swr_alloc();

    //输入的采样格式
    enum AVSampleFormat in_sample_fmt = avCodecContext->sample_fmt;
    //输出采样格式16bit PCM
    enum AVSampleFormat out_sample_fmt = AV_SAMPLE_FMT_S16;
    //输入采样率
    int in_sample_rate = avCodecContext->sample_rate;
    //输出采样率
    int out_sample_rate = in_sample_rate;
    //声道布局（2个声道，默认立体声stereo）
    uint64_t in_channel_layout = avCodecContext->channel_layout;
    //输出的声道布局（立体声）
    uint64_t out_channel_layout = AV_CH_LAYOUT_STEREO;

    swr_alloc_set_opts(swrContext,
                       out_channel_layout, out_sample_fmt,
                       out_sample_rate, in_channel_layout,
                       in_sample_fmt, in_sample_rate,
                       0, NULL);
    swr_init(swrContext);

    //输出的声道个数
    int out_channel_nb = av_get_channel_layout_nb_channels(out_channel_layout);

    //创建AudioTrack对象
    //1. 创建 AudioTrackHelper 对象
    jclass audio_track_helper_clz = (*env)->FindClass(env, "github/amorypepelu/ffmpeg/avplayer/AudioTrackHelper");
    jmethodID audio_track_helper_constructor = (*env)->GetMethodID(env, audio_track_helper_clz, "<init>", "()V");
    jobject audio_track_helper_obj = (*env)->NewObject(env, audio_track_helper_clz, audio_track_helper_constructor);
    jmethodID create_at_mid = (*env)->GetMethodID(env, audio_track_helper_clz, "createAudioTrack",
                                                  "(II)Landroid/media/AudioTrack;");
    if (!create_at_mid) {
        LOGE("createAudioTrack mid not found.\n");
        return -1;
    }
    //2. 调用 AudioTrackHelper#createAudioTrack() 方法，创建 AudioTrack
    jobject audio_track_obj = (*env)->CallObjectMethod(env, audio_track_helper_obj, create_at_mid,
                                                       out_sample_rate, out_channel_nb);

    //调用 AudioTrack#play() 方法
    jclass audio_track_clz = (*env)->GetObjectClass(env, audio_track_obj);
    jmethodID audio_track_play_mid = (*env)->GetMethodID(env, audio_track_clz, "play", "()V");
    (*env)->CallVoidMethod(env, audio_track_obj, audio_track_play_mid);

    //获取write()方法
    jmethodID audio_track_write_mid = (*env)->GetMethodID(env, audio_track_clz, "write", "([BII)I");

    //16bit 44100 PCM 数据
    int outputBufferSize = MAX_AUDIO_FRAME_SIZE;
    uint8_t *out_buffer = (uint8_t *) av_malloc((size_t) outputBufferSize);
    int got_frame = 0, index = 0;
    //不断读取编码数据
    while (av_read_frame(avFormatContext, avPacket) >= 0) {
        if (avPacket->stream_index == audio_stream_index) {
            ret = avcodec_send_packet(avCodecContext, avPacket);
            if (ret < 0) {
                LOGE("audio Error sending a packet for decoding.");
                break;
            }
            while (avcodec_receive_frame(avCodecContext, avFrame) == 0) {
                //解码一帧成功

                //音频格式转换
                // data_size为音频数据所占的字节数
                int data_size = av_samples_get_buffer_size(
                        avFrame->linesize, avCodecContext->channels,
                        avFrame->nb_samples, avCodecContext->sample_fmt, 1);
                // LOGI(">> while getPcm data_size=%d\n", data_size);
                // 内存再分配
                if (data_size > outputBufferSize) {
                    outputBufferSize = (size_t) data_size;
                    out_buffer = realloc(out_buffer, sizeof(uint8_t) * outputBufferSize);
                }
                // 音频格式转换
                swr_convert(swrContext, &out_buffer, avFrame->nb_samples,
                            (uint8_t const **) (avFrame->extended_data),
                            avFrame->nb_samples);

                //调用 audio track 播放
                jbyteArray audio_sample_array = (*env)->NewByteArray(env, data_size);
                jbyte *sample_byte_array = (*env)->GetByteArrayElements(env, audio_sample_array, NULL);
                //拷贝缓冲数据
                memcpy(sample_byte_array, out_buffer, (size_t) data_size);
                //释放数组
                (*env)->ReleaseByteArrayElements(env, audio_sample_array, sample_byte_array, 0);
                //调用AudioTrack的write方法进行播放
                (*env)->CallIntMethod(env, audio_track_obj, audio_track_write_mid, audio_sample_array, 0, data_size);
                //释放局部引用
                (*env)->DeleteLocalRef(env, audio_sample_array);
            }
        }
        av_packet_unref(avPacket);
    }
    LOGI("decode audio finish");
    av_frame_free(&avFrame);
    av_free(out_buffer);
    swr_free(&swrContext);
    avcodec_close(avCodecContext);
    avformat_close_input(&avFormatContext);
    return 0;
}

int stopAudioTrack() {
    int ret = 0;

    return 0;
}