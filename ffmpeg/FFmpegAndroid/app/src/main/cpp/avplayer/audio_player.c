#include <jni.h>
#include <string.h>
#include <android/log.h> //打印日志
#include <stdio.h>
#include "libavformat/avformat.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include "libavutil/imgutils.h"
#include "libavcodec/avcodec.h"
#include "libswscale/swscale.h"
#include "pepelu_log.h"
#include "OpenSL_ES_Core.h"
#include "audio_player_audio_track.h"

JNIEXPORT jint JNICALL
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_playAudio(JNIEnv *env, jclass type, jstring url_) {
    const char *url = (*env)->GetStringUTFChars(env, url_, 0);
    LOGI("start play audio,url=%s\n", url);
    play(url);
    (*env)->ReleaseStringUTFChars(env, url_, url);
    return 0;
}

JNIEXPORT jint JNICALL
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_stopAudio(JNIEnv *env, jclass type) {
    LOGI("stop audio.\n");
    stop();
    return 0;
}

JNIEXPORT jint JNICALL
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_playAudioByAudioTrack(JNIEnv *env, jclass type, jstring url_) {
    const char *url = (*env)->GetStringUTFChars(env, url_, 0);
    playAudioByAudioTrack(env, url);
    (*env)->ReleaseStringUTFChars(env, url_, url);
    return 0;
}

