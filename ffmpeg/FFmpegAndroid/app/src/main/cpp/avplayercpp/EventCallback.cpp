#include "media_log.h"
#include "EventCallback.h"

MPEventCallbackImpl::MPEventCallbackImpl(JNIEnv *env, jobject jcallback) {
    env->GetJavaVM(&g_VM);
    this->cb_ = env->NewGlobalRef(jcallback);
}

void MPEventCallbackImpl::onPrepared() {
    JNIEnv *env;
    bool need_detach = false;
    int env_state = g_VM->GetEnv((void **) &env, JNI_VERSION_1_6);
    if (env_state == JNI_EDETACHED) {
        if (g_VM->AttachCurrentThread(&env, nullptr) != 0) {
            return;
        }
        need_detach = true;
    }
    jclass jc_callback_clz = env->GetObjectClass(cb_);
    if (jc_callback_clz == nullptr) {
        LOGE("unable to find class.\n");
        g_VM->DetachCurrentThread();
        return;
    }
    jmethodID on_prepare_jmid = env->GetMethodID(jc_callback_clz, "onPrepared", "()V");
    if (!on_prepare_jmid) {
        LOGE("unable to find method onPrepare.\n");
        return;
    }
    env->CallVoidMethod(cb_, on_prepare_jmid);
    if (need_detach) {
        g_VM->DetachCurrentThread();
    }
    env = nullptr;
}