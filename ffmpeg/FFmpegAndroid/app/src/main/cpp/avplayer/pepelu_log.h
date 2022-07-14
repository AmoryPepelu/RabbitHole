#ifndef FFMPEG_DEMO_PEPELU_LOG_H
#define FFMPEG_DEMO_PEPELU_LOG_H

#include <android/log.h> //打印日志
#include "libavutil/log.h"

#define LOGV(FORMAT, ...) __android_log_print(ANDROID_LOG_VERBOSE,"pepelu_tag",FORMAT,##__VA_ARGS__);
#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"pepelu_tag",FORMAT,##__VA_ARGS__);
#define LOGD(FORMAT, ...) __android_log_print(ANDROID_LOG_DEBUG,"pepelu_tag",FORMAT,##__VA_ARGS__);
#define LOGW(FORMAT, ...) __android_log_print(ANDROID_LOG_WARN,"pepelu_tag",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"pepelu_tag",FORMAT,##__VA_ARGS__);
#define LOGQ(FORMAT, ...) __android_log_print(ANDROID_LOG_SILENT,"pepelu_tag",FORMAT,##__VA_ARGS__);

#endif //FFMPEG_DEMO_PEPELU_LOG_H
