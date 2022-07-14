package github.amorypepelu.ffmpeg.avplayer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by sly on 2019/2/27.
 */
public class AudioTrackHelper {

    /**
     * 创建一个AudioTrack对象
     *
     * @param sampleRate 采样率
     * @param channels   声道布局
     * @return AudioTrack
     */
    public AudioTrack createAudioTrack(int sampleRate, int channels) {
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int channelConfig;
        if (channels == 1) {
            channelConfig = android.media.AudioFormat.CHANNEL_OUT_MONO;
        } else if (channels == 2) {
            channelConfig = android.media.AudioFormat.CHANNEL_OUT_STEREO;
        } else {
            channelConfig = android.media.AudioFormat.CHANNEL_OUT_STEREO;
        }

        int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        return new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfig, audioFormat,
                bufferSizeInBytes, AudioTrack.MODE_STREAM);
    }
}
