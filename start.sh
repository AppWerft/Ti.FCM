#!/bin/bash

APPID=ti.fcm
VERSION=0.0.3

export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/"

cd android && rm -rf build/classes && \
rm -f "~/Library/Application Support/Titanium/modules/android/$APPID/$VERSION/lib/*" && \
ti --platform android build --build-only --sdk 6.1.1.GA && \
unzip -uo dist/$APPID-android-$VERSION.zip  -d  ~/Library/Application\ Support/Titanium/ && \
unzip -uo dist/$APPID-android-$VERSION.zip -d ~/Documents/MLearning/mobilelearningapp &&\
cd ..
