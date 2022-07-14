#!/bin/sh
NDK=/Users/chenliuyong/program/android-ndk-r16b
API=21
PLATFORM=arm-linux-androideabi
SYSROOT=$NDK/platforms/android-$API/arch-arm/
ISYSROOT=$NDK/sysroot
ASM=$ISYSROOT/usr/include/$PLATFORM
TOOLCHAIN=$NDK/toolchains/$PLATFORM-4.9/prebuilt/darwin-x86_64
CPU=arm
PREFIX=$(pwd)/android/$CPU
export TMPDIR=$(pwd)/temp
ADDI_CFLAGS="/Users/chenliuyong/wrokspace/Hunter/video/x264a/android/arm/include/"
ADDI_LDFLAGS="/Users/chenliuyong/wrokspace/Hunter/video/x264a/android/arm/lib/"
function build_one
{
    ./configure \
    --prefix=$PREFIX \
    --enable-shared \
    --disable-static \
    --enable-nonfree \
    --disable-doc \
    --disable-ffmpeg \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-avdevice \
    --disable-pthreads \
    --disable-doc \
    --disable-symver \
    --disable-ffserver \
    --enable-libx264 \
    --enable-encoder=libx264 \
    --enable-decoder=h264 \
    --enable-zlib \
    --enable-protocol=rtp \
    --enable-gpl \
    --cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
    --target-os=android \
    --arch=arm \
    --enable-cross-compile \
    --sysroot=$SYSROOT \
    --extra-cflags="-I$ASM -I$ADDI_CFLAGS -isysroot $ISYSROOT -Os -fpic -D__ANDROID_API__=$API" \
    --extra-ldflags="-L$ADDI_LDFLAGS" \
    $ADDITIONAL_CONFIGURE_FLAG
    make clean
    make -j8
    make install
}
build_one