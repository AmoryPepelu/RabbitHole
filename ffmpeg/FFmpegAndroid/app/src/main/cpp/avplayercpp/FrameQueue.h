#ifndef FFMPEG_DEMO_FRAMEQUEUE_H
#define FFMPEG_DEMO_FRAMEQUEUE_H

#include <thread>
#include <string>
#include <condition_variable>
#include <mutex>
#include <queue>

extern "C" {
#include "libavcodec/avcodec.h"
//#include "libavformat/avformat.h"
};


class FrameQueue {

public:
    void Put(AVFrame *frame);

    AVFrame *Get();

    inline size_t Size() const {
        return queue_.size();
    }

private:
    std::queue<AVFrame *> queue_;
    std::mutex mutex_;
    std::condition_variable cond_;
    const size_t kMaxSize = 16;
    const size_t kReadySize = 8;
};

#endif //FFMPEG_DEMO_FRAMEQUEUE_H
