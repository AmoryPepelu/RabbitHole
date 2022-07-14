#include "OpenSL_ES_Core.h"
#include "FFmpegCore.h"
#include <assert.h>
#include <jni.h>
#include <string.h>

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

// for native asset manager
#include <sys/types.h>
#include <android/asset_manager.h>
#include "pepelu_log.h"

#include <android/asset_manager_jni.h>

// engine interfaces
SLObjectItf engineObject = NULL;
SLEngineItf engineEngine;

// output mix interfaces
SLObjectItf outputMixObject = NULL;
SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;

//buffer queue player interfaces
SLObjectItf bqPlayerObject = NULL;
SLPlayItf bqPlayerPlay;
SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;
SLEffectSendItf bqPlayerEffectSend;
//SLMuteSoloItf bqPlayerMuteSolo;
SLVolumeItf bqPlayerVolume;

// aux effect on the output mix, used by the buffer queue player
// 混合输出时的辅助效果，用于播放器缓冲队列
const SLEnvironmentalReverbSettings reverbSettings = SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;

void *buffer;
size_t bufferSize;

//int frame_count = 0;

// this callback handler is called every time a buffer finishes playing
void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context) {
    //LOGI(">> buffer queue callback.\n");
    bufferSize = 0;

    getPCM(&buffer, &bufferSize);

    // for streaming playback, replace this test by logic to find and fill the next buffer
    if (NULL != buffer && 0 != bufferSize) {
        SLresult result;
        // enqueue another buffer 向播放队列里面写入数据
        result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, buffer, bufferSize);
        // the most likely other result is SL_RESULT_BUFFER_INSUFFICIENT,
        // which for this code example would indicate a programming error

//        if (result < 0) {
//            LOGE("Enqueue error...");
//        } else {
//            LOGI("decode frame count=%d", frame_count++);
//        }

        //防止编译器编译时报警告的用法
//        (void) sLresult;
    } else {
        LOGI("get pcm buffer null...play end .\n");
        stop();//释放资源
    }
}

void initOpenSLES() {
    LOGI(">> initOpenSLES...\n");
    SLresult ret;

    // 1、create engine 创建引擎
    ret = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    LOGI(">> initOpenSLES... step 1, result = %d", ret);

    // 2、realize the engine 实现引擎
    ret = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    LOGD(">> initOpenSLES...step 2, result = %d", ret);

    // 3、get the engine interface, which is needed in order to create other objects
    // 获取引擎接口，用于创建其他对象
    ret = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
    LOGD(">> initOpenSLES...step 3, result = %d", ret);

    // 4、create output mix, with environmental reverb specified as a non-required interface
    // 创建输出混音器，将环境混响指定为非必需接口
    ret = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 0, 0, 0);
    LOGD(">> initOpenSLES...step 4, result = %d", ret);

    // 5、realize the output mix
    // 关联输出混音器
    ret = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    LOGD(">> initOpenSLES...step 5, result = %d", ret);

    // 6、get the environmental reverb interface
    // this could fail if the environmental reverb effect is not available,
    // either because the feature is not present, excessive CPU load, or
    // the required MODIFY_AUDIO_SETTINGS permission was not requested and granted
    //获取reverb接口
    ret = (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB, &outputMixEnvironmentalReverb);
    LOGD(">> initOpenSLES...step 6, result = %d", ret);
    if (SL_RESULT_SUCCESS == ret) {
        ret = (*outputMixEnvironmentalReverb)->SetEnvironmentalReverbProperties(outputMixEnvironmentalReverb,
                                                                                &reverbSettings);
        LOGD(">> initOpenSLES...step 7, result = %d", ret);
    }
}

// init buffer queue
// 创建带有缓冲队列的音频播放器
void initBufferQueue(int rate, int channel, int bitsPerSimple) {
    LOGD(">> initBufferQueue");
    SLresult ret = 0;

    // configure audio source
    // 配置音频源
    SLDataLocator_AndroidSimpleBufferQueue loc_bufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
    SLDataFormat_PCM format_pcm;
    format_pcm.formatType = SL_DATAFORMAT_PCM;
    format_pcm.numChannels = (SLuint32) channel;
    format_pcm.samplesPerSec = (SLuint32) rate * 1000;
    format_pcm.bitsPerSample = (SLuint32) bitsPerSimple;
    format_pcm.containerSize = 16;
    if (channel == 2) {
        format_pcm.channelMask = SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT;
    } else {
        format_pcm.channelMask = SL_SPEAKER_FRONT_CENTER;
    }
    format_pcm.endianness = SL_BYTEORDER_LITTLEENDIAN;
    SLDataSource audioSrc = {&loc_bufq, &format_pcm};

    // configure audio sink
    // 配置音频池
    SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSink = {&loc_outmix, NULL};

    // create audio player
    // 创建音频播放器
    const SLInterfaceID ids[3] = {SL_IID_BUFFERQUEUE, SL_IID_EFFECTSEND,
            /*SL_IID_MUTESOLO,*/SL_IID_VOLUME};
    const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE,/*SL_BOOLEAN_TRUE*/SL_BOOLEAN_TRUE};
    ret = (*engineEngine)->CreateAudioPlayer(engineEngine, &bqPlayerObject, &audioSrc, &audioSink, 3, ids, req);
    LOGI("CreateAudioPlayer=%d", ret);

    // realize the player
    // 关联播放器
    ret = (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);
    LOGI("bqPlayerObject Realize=%d", ret);

    // get the play interface 获取播放接口
    ret = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerPlay);
    LOGI("GetInterface bqPlayerPlay=%d", ret);

    //get the buffer queue interface 获取缓冲队列接口
    ret = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE, &bqPlayerBufferQueue);
    LOGI("GetInterface bqPlayerBufferQueue=%d", ret);

    // register callback on the buffer queue  注册缓冲队列回调
    ret = (*bqPlayerBufferQueue)->RegisterCallback(bqPlayerBufferQueue, bqPlayerCallback, NULL);
    LOGI("RegisterCallback=%d", ret);

    // get the effect send interface 获取音效接口
    ret = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_EFFECTSEND, &bqPlayerEffectSend);
    LOGI("GetInterface effect=%d", ret);

    // get the volume interface 获取音量接口
    ret = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_VOLUME, &bqPlayerVolume);
    LOGI("GetInterface volume=%d", ret);

    // set the player's state to playing 开始播放音乐
    ret = (*bqPlayerPlay)->SetPlayState(bqPlayerPlay, SL_PLAYSTATE_PLAYING);
    LOGI("SetPlayState=%d", ret);
}

// stop the native audio system
void stop() {
    // destroy buffer queue audio player object, and invalidate all associated interfaces
    if (bqPlayerObject != NULL) {
        (*bqPlayerObject)->Destroy(bqPlayerObject);
        bqPlayerObject = NULL;
        bqPlayerPlay = NULL;
        bqPlayerBufferQueue = NULL;
        bqPlayerEffectSend = NULL;
//        bqPlayerMuteSolo = NULL;
        bqPlayerVolume = NULL;
    }

    // destroy output mix object, and invalidate all associated interfaces
    if (outputMixObject != NULL) {
        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject = NULL;
        outputMixEnvironmentalReverb = NULL;
    }

    // destroy engine object, and invalidate all associated interfaces
    if (engineObject != NULL) {
        (*engineObject)->Destroy(engineObject);
        engineObject = NULL;
        engineEngine = NULL;
    }

    // 释放FFmpeg解码器
    releaseAudioPlayer();
}

void play(const char *url) {
    int rate, channel;
    LOGD("...get url=%s", url);

    // 1、初始化FFmpeg解码器
    createAudioPlayer(&rate, &channel, url);

    // 2、初始化OpenSLES
    initOpenSLES();

    // 3、初始化BufferQueue ,创建带有缓冲队列的音频播放器
    initBufferQueue(rate, channel, SL_PCMSAMPLEFORMAT_FIXED_16);

    // 4、启动音频播放
    bqPlayerCallback(bqPlayerBufferQueue, NULL);
}