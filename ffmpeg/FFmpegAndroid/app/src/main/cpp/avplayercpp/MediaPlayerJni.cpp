#include <jni.h>
#include <thread>
#include <string>
#include <memory>
#include <condition_variable>
#include <mutex>
#include <queue>
#include "android/native_window.h"
#include <android/native_window_jni.h>

#include "media_log.h"
#include "MediaPlayer.h"
#include "EventCallback.h"
#include "VideoDataProvider.h"


MediaPlayer *player;

MPEventCallbackImpl *eventCallback;

//JNIEXPORT void JNICALL
extern "C"
void
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_createMediaPlayer(JNIEnv *env, jclass type) {
    player = new MediaPlayer();
}

extern "C"
void
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_setEventCallback(JNIEnv *env, jclass type, jobject jcallback) {
    if (eventCallback) {
        delete eventCallback;
    }
    eventCallback = new MPEventCallbackImpl(env, jcallback);
    player->SetEventCallback(eventCallback);
}

extern "C"
void
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_setDataSource(JNIEnv *env, jclass type, jstring url_) {
    const char *url = env->GetStringUTFChars(url_, nullptr);
    player->SetDataSource(url);
    env->ReleaseStringUTFChars(url_, url);
}

extern "C"
void
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_prepareAsync(JNIEnv *env, jclass type) {
    player->PrepareAsync();
}

extern "C"
void
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_setSurface(JNIEnv *env, jclass type, jobject object) {
    initVideoRender(env, player, object);
}

extern "C"
void
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_start(JNIEnv *env, jclass type) {
    player->Start();
}

extern "C"
void
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_pause(JNIEnv *env, jclass type) {
    player->Pause();
}

extern "C"
jint
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_getVideoHeight(JNIEnv *env, jclass type) {
    return player->GetVideoHeight();
}

extern "C"
jint
Java_github_amorypepelu_ffmpeg_avplayer_NativePlayer_getVideoWidth(JNIEnv *env, jclass type) {
    return player->GetVideoWidth();
}