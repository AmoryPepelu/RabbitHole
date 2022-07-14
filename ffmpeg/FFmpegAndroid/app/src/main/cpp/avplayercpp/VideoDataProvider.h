
#ifndef FFMPEG_DEMO_VIDEODATAPROVIDER_H
#define FFMPEG_DEMO_VIDEODATAPROVIDER_H

#include <jni.h>

extern "C" {
#include "libavformat/avformat.h"
};

class VideoDataProvider {
public:
    virtual void GetData(uint8_t **buffer, AVFrame **frame, int &width, int &height) = 0;

    virtual int GetVideoHeight() = 0;

    virtual int GetVideoWidth() = 0;

    virtual ~VideoDataProvider() = default;
};

void initVideoRender(JNIEnv *env, VideoDataProvider *provider, jobject surface);

void videoRender();

#endif //FFMPEG_DEMO_VIDEODATAPROVIDER_H
