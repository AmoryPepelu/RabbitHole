#!/bin/sh
NDK=/Users/chenliuyong/program/android-ndk-r16b
API=17
PLATFORM=arm-linux-androideabi
SYSROOT=$NDK/platforms/android-$API/arch-arm/
ISYSROOT=$NDK/sysroot
ASM=$ISYSROOT/usr/include/$PLATFORM
TOOLCHAIN=$NDK/toolchains/$PLATFORM-4.9/prebuilt/darwin-x86_64
CPU=arm
PREFIX=$(pwd)/android/$CPU
ADDI_CFLAGS="-marm"
export TMPDIR=$(pwd)/temp
function build_one
{
    ./configure \
    --prefix=$PREFIX \
    --disable-shared \
    --enable-static \
    --disable-doc \
    --disable-ffmpeg \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-avdevice \
    --disable-doc \
    --disable-symver \
    --disable-ffserver \
    --cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
    --target-os=android \
    --arch=arm \
    --enable-cross-compile \
    --sysroot=$SYSROOT \
    --extra-cflags="-I$ASM -isysroot $ISYSROOT -Os -fpic $ADDI_CFLAGS -D__ANDROID_API__=$API" \
    --extra-ldflags="$ADDI_LDFLAGS" \
    $ADDITIONAL_CONFIGURE_FLAG
    make clean
    make -j8
    make install
}
build_one