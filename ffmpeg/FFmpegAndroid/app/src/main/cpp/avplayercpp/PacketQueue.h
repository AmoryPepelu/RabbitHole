#ifndef FFMPEG_DEMO_PACKETQUEUE_H
#define FFMPEG_DEMO_PACKETQUEUE_H

#include <thread>
#include <string>
#include <condition_variable>
#include <mutex>
#include <queue>

extern "C" {
#include "libavcodec/avcodec.h"
};

class PacketQueue {
public:
    void Put(AVPacket *pkt);

    void Get(AVPacket *pkt);

    void Clear();

private:
    std::queue<AVPacket> queue_;
    int64_t duration_;
    std::mutex mutex_;
    std::condition_variable ready_;
    std::condition_variable full_;
    const size_t kMaxSize = 16;
};

#endif //FFMPEG_DEMO_PACKETQUEUE_H
