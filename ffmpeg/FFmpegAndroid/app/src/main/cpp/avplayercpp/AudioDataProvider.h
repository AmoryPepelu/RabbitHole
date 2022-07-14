#ifndef FFMPEG_DEMO_AUDIOPROVIDER_H
#define FFMPEG_DEMO_AUDIOPROVIDER_H

#include <stdint.h>

class AudioDataProvider {
public:
    virtual void GetData(uint8_t **buffer, int &buffer_size) = 0;

    virtual ~AudioDataProvider() = default;
};

void initAudioPlayer(int sampleRate, int channel, AudioDataProvider *audioProvider);

void startAudioPlay();

void stopAudioPlay();

#endif //FFMPEG_DEMO_AUDIOPROVIDER_H
