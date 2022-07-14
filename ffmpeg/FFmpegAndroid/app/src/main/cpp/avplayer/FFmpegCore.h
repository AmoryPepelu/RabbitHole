#ifndef FFMPEG_DEMO_FFMPEGCORE_H
#define FFMPEG_DEMO_FFMPEGCORE_H

#include <stdio.h>

int createAudioPlayer(int *rate, int *channel, const char *url);

int getPCM(void **pcm, size_t *pcmSize);

int releaseAudioPlayer();

#endif //FFMPEG_DEMO_FFMPEGCORE_H
