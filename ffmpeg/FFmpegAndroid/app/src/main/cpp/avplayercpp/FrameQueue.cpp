#include "FrameQueue.h"

void FrameQueue::Put(AVFrame *frame) {
    std::lock_guard<std::mutex> lock(mutex_);
    AVFrame *tmp = av_frame_alloc();
    av_frame_move_ref(tmp, frame);
    queue_.push(tmp);
    cond_.notify_all();
}

AVFrame *FrameQueue::Get() {
    std::unique_lock<std::mutex> lock(mutex_);
    cond_.wait(lock, [this] {
        return !queue_.empty();
    });
    AVFrame *frame = queue_.front();
    queue_.pop();
    return frame;
}