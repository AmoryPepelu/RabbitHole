#ifndef FFMPEG_DEMO_MPEVENTCALLBACK_H
#define FFMPEG_DEMO_MPEVENTCALLBACK_H

#include <jni.h>

class EventCallback {
public:
    virtual void onPrepared() = 0;

    virtual ~EventCallback() = default;
};

class MPEventCallbackImpl : public EventCallback {
public:
    MPEventCallbackImpl(JNIEnv *env, jobject jcallback);

    void onPrepared() override;

private:
    JavaVM *g_VM;
    jobject cb_;
};

#endif //FFMPEG_DEMO_MPEVENTCALLBACK_H
