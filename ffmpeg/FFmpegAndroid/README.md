# ffmpeg

## 播放音频

* OpenSLES : PlayAudioActivity
* AudioTrack : PlayAudioActivity

## 播放视频

* 视频播放 : PlayVideoActivity
* 音视频同步播放
    * 音频 : OpenSLES
    * 视频 : SurfaceView

## version

- ffmpeg : 3.4.5
- ndk : android-ndk-r16b

## 编译脚本

在目录 `build_so_script` 中

替换 `configure`

先执行 `build_arm.sh` 再执行 `build_arm_static_lib.sh`

编译 `x264` 时不要修改 `configure` 文件，否则会编译错误



