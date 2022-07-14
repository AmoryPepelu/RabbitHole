#ifndef FFMPEG_DEMO_MEDIAPLAYER_H
#define FFMPEG_DEMO_MEDIAPLAYER_H

#include <thread>
#include <string>
#include <memory>
#include <condition_variable>
#include <mutex>
#include <queue>
#include "AudioDataProvider.h"
#include "EventCallback.h"
#include "VideoDataProvider.h"
#include "FrameQueue.h"
#include "PacketQueue.h"

extern "C" {
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include "libswresample/swresample.h"
#include "libavutil/opt.h"
#include "libavutil/imgutils.h"
#include "libavutil/time.h"
#include "libavcodec/avfft.h"
};

#define MP_AUDIO_BUFFER_SIZE 8196
#define MP_AUDIO_READY_SIZE 8

enum class MPState {
    Idle,
    Initialized,
    Preparing,
    Prepared,
    Started,
    Paused,
    Stoped,
    Completed,
    End,
    Error,
};

class MediaPlayer : public AudioDataProvider, public VideoDataProvider {
public:
    MediaPlayer();

    MediaPlayer(const MediaPlayer &player) = delete;

    ~MediaPlayer() final;

    int SetDataSource(const std::string &path);

    int PrepareAsync();

    void SetEventCallback(EventCallback *cb);

    int Start();

    int Pause();

    void GetData(uint8_t **buffer, int &buffer_size) override;

    void GetData(uint8_t **buffer, AVFrame **frame, int &width, int &height) override;

    int GetVideoWidth() override;

    int GetVideoHeight() override;


private:
    void read();

    void decodeAudio();

    void decodeVideo();

    void openStream(int index);

private:
    EventCallback *event_cb_;
    MPState state_;
    std::unique_ptr<std::thread> read_thread_;
    std::unique_ptr<std::thread> audio_decode_thread_;
    std::unique_ptr<std::thread> video_decode_thread_;
    std::unique_ptr<std::thread> audio_render_thread_;
    std::unique_ptr<std::thread> video_render_thread_;
    AVFormatContext *ic_;
    std::string path_;
    PacketQueue audio_packets_;
    PacketQueue video_packets_;
    FrameQueue audio_frames_;
    FrameQueue video_frames_;
    int video_stream_;
    int audio_stream_;
    AVStream *audio_st_;
    AVStream *video_st_;
    struct SwrContext *swr_ctx_;
    struct SwsContext *img_convert_ctx_;
    AVCodecContext *audio_codec_ctx_;
    AVCodecContext *video_codec_ctx_;
    int eof;
    uint8_t *audio_buffer_;
    AVFrame *frame_rgba_;
    uint8_t *rgba_buffer_;
    double audio_clock;
};

#endif //FFMPEG_DEMO_MEDIAPLAYER_H
