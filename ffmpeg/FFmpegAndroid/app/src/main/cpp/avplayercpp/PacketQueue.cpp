#include "PacketQueue.h"

//packet queue
void PacketQueue::Get(AVPacket *pkt) {
    if (pkt == nullptr) {
        return;
    }
    std::unique_lock<std::mutex> lck(mutex_);
    ready_.wait(lck, [this] {
        return !queue_.empty();
    });
    *pkt = queue_.front();
    queue_.pop();
    full_.notify_all();
}

void PacketQueue::Put(AVPacket *pkt) {
    std::unique_lock<std::mutex> lck(mutex_);
    full_.wait(lck, [this] {
        return queue_.size() <= kMaxSize;
    });
    queue_.push(*pkt);
    ready_.notify_all();
}

void PacketQueue::Clear() {

}