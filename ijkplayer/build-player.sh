#!/usr/bin/env bash

if [ "$ANDROID_SDK" = "" ] ; then
    echo "Set ANDROID_SDK to be the location of your Sdk"
    exit 1
fi

if [ ! -d Ndk ] ; then
    echo "run init-sources to init the Ndk"
    exit 1
fi

export ANDROID_NDK=`pwd`/Ndk/android-ndk-r10e
#export ANDROID_SDK=/home/sls/Android/Sdk/

cd ijkplayer
cd config
ln -sf module-default.sh module.sh
cd ..
./init-android.sh
cd android/contrib/
./compile-ffmpeg.sh clean
./compile-ffmpeg.sh all
cd ..
./compile-ijk.sh all
cd ..

echo "run copy-resources to update the project's artifacts with the new player"
