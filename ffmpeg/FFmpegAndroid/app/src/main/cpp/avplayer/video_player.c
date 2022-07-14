#include <jni.h>
#include <string.h>
#include <stdio.h>
#include "libavformat/avformat.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include "libavutil/imgutils.h"
#include "libavcodec/avcodec.h"
#include "libswscale/swscale.h"
#include "pepelu_log.h"

//extern "C"
JNIEXPORT jstring JNICALL
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_avRegisterAll(JNIEnv *env, jclass type) {
    av_register_all();
    return (*env)->NewStringUTF(env, "hello ffmpeg !");
}

JNIEXPORT jint JNICALL
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_playVideo(JNIEnv *env, jclass type, jstring url_,
                                                               jobject surface) {
    int ret = 0;
    const char *fileName = (*env)->GetStringUTFChars(env, url_, JNI_FALSE);
    LOGI("play video url:%s\n", fileName);
    av_register_all();

    AVFormatContext *pFormatCtx = avformat_alloc_context();
    //打开视频文件
    if (avformat_open_input(&pFormatCtx, fileName, NULL, NULL) != 0) {
        LOGE("couldn't open file : %s\n", fileName);
        return -1;
    }

    //检索流信息
    if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {
        LOGE("couldn't find stream information.");
        return -1;
    }

    //找到第一个视频流
    int videoStream = -1;
    for (int i = 0; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO && videoStream < 0) {
            videoStream = i;
        }
    }
    if (videoStream == -1) {
        LOGE("didn't find a video stream");
        return -1;//找不到视频流
    }

    //得到视频流的编码器上下文环境的指针
    AVCodecParameters *avCodecParameters = pFormatCtx->streams[videoStream]->codecpar;

    //找到视频流对应的解码器
    AVCodec *pCodec = avcodec_find_decoder(avCodecParameters->codec_id);
    if (pCodec == NULL) {
        LOGE("codec not found .");
        return -1;//找不到视频解码器
    }

    AVCodecContext *pCodecCtx = avcodec_alloc_context3(NULL);
    if (!pCodecCtx) {
        LOGE("alloc avcodec fail.");
        return -1;
    }
    ret = avcodec_parameters_to_context(pCodecCtx, avCodecParameters);
    if (ret < 0) {
        LOGE("avcodec_parameters_to_context fail.");
        return -1;
    }
    if (avcodec_open2(pCodecCtx, pCodec, NULL) < 0) {
        LOGE("could not open codec.");
        return -1;//打开解码器失败
    }

    //获取NativeWindow 用于渲染视频
    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);

    //获取视频宽高
    int videoWidth = pCodecCtx->width;
    int videoHeight = pCodecCtx->height;
    LOGI("video width=%d,height=%d", videoWidth, videoHeight);

    //设置 NativeWindow 的 Buffer 大小，可自动拉伸
    ANativeWindow_setBuffersGeometry(nativeWindow, pCodecCtx->width, pCodecCtx->height, WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_Buffer windowBuffer;

    //分配视频帧空间内存
    AVFrame *pFrame = av_frame_alloc();
    //用于渲染
    AVFrame *pFrameRGBA = av_frame_alloc();
    if (pFrame == NULL || pFrameRGBA == NULL) {
        LOGE("could not allocate video frame.");
        return -1;//内存分配失败
    }

    //确定所需缓冲区大小并分配缓冲区内存空间
    //Buffer 中的数据用于渲染 ， 格式为 RGBA
    int numBytes = av_image_get_buffer_size(AV_PIX_FMT_RGBA, pCodecCtx->width, pCodecCtx->height, 1);
    uint8_t *buffer = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));
    av_image_fill_arrays(pFrameRGBA->data, pFrameRGBA->linesize, buffer, AV_PIX_FMT_RGBA,
                         pCodecCtx->width, pCodecCtx->height, 1);

    //由于解码出来的帧格式不是RGBA的，在渲染之前需要进行格式转换
    struct SwsContext *swsContext = sws_getContext(pCodecCtx->width,
                                                   pCodecCtx->height,
                                                   pCodecCtx->pix_fmt,
                                                   pCodecCtx->width,
                                                   pCodecCtx->height,
                                                   AV_PIX_FMT_RGBA,
                                                   SWS_BILINEAR,
                                                   NULL,
                                                   NULL,
                                                   NULL);

    AVPacket *packet = av_packet_alloc();
    while (av_read_frame(pFormatCtx, packet) >= 0) {
        //判断 Packet （音视频压缩数据） 是否是视频流
        if (packet->stream_index == videoStream) {
            //解码视频帧 : 老版本使用
            //avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);

            ret = avcodec_send_packet(pCodecCtx, packet);
            if (ret < 0) {
                LOGE("video Error sending a packet for decoding.");
                break;
            }
            while (ret >= 0) {
                ret = avcodec_receive_frame(pCodecCtx, pFrame);
                if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF) {
                    break;
                } else if (ret < 0) {
                    LOGE("Error during decoding\n");
                    break;
                }
                //锁住 NativeWindow 缓冲区
                ANativeWindow_lock(nativeWindow, &windowBuffer, 0);

                //格式转换
                sws_scale(swsContext, (uint8_t const *const *) pFrame->data,
                          pFrame->linesize, 0, pCodecCtx->height,
                          pFrameRGBA->data, pFrameRGBA->linesize);

                //获取 stride
                uint8_t *dst = windowBuffer.bits;
                int dstStride = windowBuffer.stride * 4;
                uint8_t *src = (uint8_t *) (pFrameRGBA->data[0]);
                int srcStride = pFrameRGBA->linesize[0];
                //由于窗口的stride和帧的stride不同，因此需要逐行复制
                for (int h = 0; h < videoHeight; h++) {
                    memcpy(dst + h * dstStride, src + h * srcStride, (size_t) srcStride);
                }
                ANativeWindow_unlockAndPost(nativeWindow);
            }
        }
        av_packet_unref(packet);
    }
    LOGI("video play end...");
    av_free(buffer);
    av_free(pFrameRGBA);
    //释放YUV图像帧
    av_free(pFrame);
    //关闭解码器
    avcodec_close(pCodecCtx);
    //关闭视频文件
    avformat_close_input(&pFormatCtx);
    (*env)->ReleaseStringUTFChars(env, url_, fileName);
    return 0;
}