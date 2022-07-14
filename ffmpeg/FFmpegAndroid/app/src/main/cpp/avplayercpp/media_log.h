#ifndef FFMPEG_DEMO_MEDIA_LOG_H
#define FFMPEG_DEMO_MEDIA_LOG_H

#include <android/log.h>

#define FF_LOG_TAG "ffmpeg"
#define MY_LOG_LEVEL kVerbose

#define LOGV(FORMAT, ...) __android_log_print(ANDROID_LOG_VERBOSE,"pepelu_tag",FORMAT,##__VA_ARGS__);
#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"pepelu_tag",FORMAT,##__VA_ARGS__);
#define LOGD(FORMAT, ...) __android_log_print(ANDROID_LOG_DEBUG,"pepelu_tag",FORMAT,##__VA_ARGS__);
#define LOGW(FORMAT, ...) __android_log_print(ANDROID_LOG_WARN,"pepelu_tag",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"pepelu_tag",FORMAT,##__VA_ARGS__);
#define LOGQ(FORMAT, ...) __android_log_print(ANDROID_LOG_SILENT,"pepelu_tag",FORMAT,##__VA_ARGS__);

#define WLOG(level, tag, ...) WriteLog(level, tag, __FILE__, __FUNCTION__, __LINE__, __VA_ARGS__);

enum LogLevel {
    kVerbose = 2,
    kDebug = 3,
    kInfo = 4,
    kWarn = 5,
    kError = 6,
    kOff = 8,
};

void ff_log_callback(void *ptr, int level, const char *fmt, va_list vl);


void WriteLog(LogLevel level, const char *tag, const char *file_name,
              const char *func_name, int line, const char *fmt, ...);

#endif //FFMPEG_DEMO_PEPELU_LOG_H
