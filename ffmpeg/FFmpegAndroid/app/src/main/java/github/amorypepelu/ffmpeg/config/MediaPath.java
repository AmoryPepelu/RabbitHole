package github.amorypepelu.ffmpeg.config;

import android.os.Environment;

import java.io.File;

/**
 * Created by sly on 2019/2/24.
 */
public interface MediaPath {
    String BaseVideoPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "video" + File.separator;

    //audio-local
    String StarButterflyMp3 = BaseVideoPath + "StarButterfly.mp3";
    String UntilTheWorldEndMp3 = BaseVideoPath + "UntilTheWorldEnd.mp3";
    String UntilTheWorldEnd5sMp3 = BaseVideoPath + "UntilTheWorldEnd5s.mp3";

    //audio-net
    String AudioNet1 = "http://ra01.sycdn.kuwo.cn/resource/n3/32/56/3260586875.mp3";
    String AudioNetLoser = "http://m10.music.126.net/20190225231508/581f607a7633d1ae7f636f4b4a3c8f7d/ymusic/769c/c23d/dffa/d4cc39047bdb207084344d6ae842ce17.mp3";

    //video
    String TheSimpsonsMp4 = BaseVideoPath + "TheSimpsons.mp4";
    String SouthParkMp4 = BaseVideoPath + "SouthPark.mp4";
    String AdventureTime5sMp4 = BaseVideoPath + "AdventureTime5s.mp4";

    //video-net
    String VideoNetLoser = "http://vodkgeyttp8.vod.126.net/cloudmusic/IDIkIjQwJTA4MmIwMCAwIQ==/mv/5359554/24e8532d9e47e6e0cc1c406800a37ff4.mp4";
    String VideoNet2 = "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";
}
