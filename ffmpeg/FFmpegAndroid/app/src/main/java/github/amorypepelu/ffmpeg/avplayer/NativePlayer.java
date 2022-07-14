package github.amorypepelu.ffmpeg.avplayer;

/**
 * Created by sly on 2019/2/1.
 */
public class NativePlayer {

    static {
        System.loadLibrary("avcodec");
        System.loadLibrary("avfilter");
        System.loadLibrary("avformat");
        System.loadLibrary("avutil");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
        System.loadLibrary("x264v157");
        System.loadLibrary("pepeluffmpeg");
    }

    public static native String avRegisterAll();

    public static native int playVideo(String url, Object surface);

    //with OpenSLES
    public static native int playAudio(String url);

    public static native int stopAudio();

    //with AudioTrack
    public static native int playAudioByAudioTrack(String url);

    // play both audio and video
//    public static native int playMedia(String url, Object surface);

    // play medial
    public static native void createMediaPlayer();

    public static native void setEventCallback(Object eventCallback);

    public static native void setDataSource(String url);

    public static native void prepareAsync();

    public static native void setSurface(Object object);

    public static native void start();

    public static native void pause();

    public static native int getVideoHeight();

    public static native int getVideoWidth();
}
