# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        pepeluffmpeg

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/avplayer/audio_player.c
        src/main/cpp/avplayer/video_player.c
        src/main/cpp/avplayer/FFmpegCore.c
        src/main/cpp/avplayer/OpenSL_ES_Core.c
        src/main/cpp/avplayer/audio_player_audio_track.c
        src/main/cpp/avplayer/AVpacket_queue.c

        # 音视频同步播放器
        src/main/cpp/avplayercpp/MediaPlayerJni.cpp
        src/main/cpp/avplayercpp/MediaPlayer.cpp
        src/main/cpp/avplayercpp/EventCallback.cpp
        src/main/cpp/avplayercpp/AudioDataProvider.cpp
        src/main/cpp/avplayercpp/VideoDataProvider.cpp
        src/main/cpp/avplayercpp/PacketQueue.cpp
        src/main/cpp/avplayercpp/FrameQueue.cpp
        src/main/cpp/avplayercpp/media_log.cpp
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

#添加libavcodec.so
add_library(avcodec
        SHARED
        IMPORTED)
set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libavcodec.so)

add_library(avfilter
        SHARED
        IMPORTED)
set_target_properties(avfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libavfilter.so)

add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libavformat.so)

add_library(avutil
        SHARED
        IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libavutil.so)

add_library(swresample
        SHARED
        IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libswresample.so)

add_library(swscale
        SHARED
        IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libswscale.so)

add_library(postproc
        SHARED
        IMPORTED)
set_target_properties(postproc
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libpostproc.so)

add_library(x264v157
        SHARED
        IMPORTED)
set_target_properties(x264v157
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libx264v157.so)

include_directories(src/main/cpp)
include_directories(src/main/cpp/include/ffmpeg)
include_directories(src/main/cpp/include/x264)
include_directories(src/main/cpp/avplayer)
include_directories(src/main/cpp/avplayercpp)

target_link_libraries( # Specifies the target library.
        pepeluffmpeg
        avcodec
        avfilter
        avformat
        avutil
        swresample
        swscale
        postproc
        x264v157

        # Links the target library to the log library
        # included in the NDK.
        -landroid #native_window
        -lOpenSLES #openSLES
        -ljnigraphics #bitmap
        ${log-lib})