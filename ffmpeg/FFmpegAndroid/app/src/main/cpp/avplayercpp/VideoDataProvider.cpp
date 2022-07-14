#include "VideoDataProvider.h"

#include <android/native_window.h>
#include <android/native_window_jni.h>
#include "media_log.h"
#include <string>

ANativeWindow *nativeWindow;
ANativeWindow_Buffer windowBuffer;
VideoDataProvider *dataProvider = nullptr;

void initVideoRender(JNIEnv *env, VideoDataProvider *provider, jobject surface) {
    dataProvider = provider;
    nativeWindow = ANativeWindow_fromSurface(env, surface);
}

void videoRender() {
    if (!dataProvider) {
        LOGE("video render not init.\n");
        return;
    }
    if (0 != ANativeWindow_setBuffersGeometry(nativeWindow,
                                              dataProvider->GetVideoWidth(),
                                              dataProvider->GetVideoHeight(),
                                              WINDOW_FORMAT_RGBA_8888)) {
        LOGE("Couldn't set buffers geometry.\n");
        ANativeWindow_release(nativeWindow);
        return;
    }

    while (true) {
        if (0 != ANativeWindow_lock(nativeWindow, &windowBuffer, nullptr)) {
            LOGE("cannot lock window\n");
        } else {
            uint8_t *buffer = nullptr;
            AVFrame *frame = nullptr;
            int width, height;
            dataProvider->GetData(&buffer, &frame, width, height);
            if (nullptr == buffer) {
                LOGI("play video finish.\n");
                break;
            }
            auto *dst = (uint8_t *) windowBuffer.bits;
            for (int h = 0; h < height; h++) {
                memcpy(dst + h * windowBuffer.stride * 4, buffer + h * frame->linesize[0], frame->linesize[0]);
            }
            ANativeWindow_unlockAndPost(nativeWindow);
        }
    }
}