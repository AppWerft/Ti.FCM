# Android.mk for ti.fcm
LOCAL_PATH := $(call my-dir)
THIS_DIR := $(LOCAL_PATH)

include $(CLEAR_VARS)

THIS_DIR = $(LOCAL_PATH)
LOCAL_MODULE := ti.fcm
LOCAL_CFLAGS := -g "-I$(TI_MOBILE_SDK)/android/native/include"

# https://jira.appcelerator.org/browse/TIMOB-15263
LOCAL_DISABLE_FORMAT_STRING_CHECKS=true

LOCAL_CFLAGS += -Wno-conversion-null -Wno-format-security -Wno-format -Wno-tautological-compare -Wno-unused-result -Wno-deprecated-register
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -ldl -llog -L$(TARGET_OUT) "-L$(TI_MOBILE_SDK)/android/native/libs/$(TARGET_ARCH_ABI)" -lkroll-v8

GEN_DIR := $(realpath .)
GEN_JNI_DIR := $(GEN_DIR)/jni

ABS_SRC_FILES := $(wildcard $(LOCAL_PATH)/*.cpp)
BOOTSTRAP_CPP := $(wildcard $(LOCAL_PATH)/../*Bootstrap.cpp)

GPERF := gperf
# ifeq ($(OS), Windows_NT)
# GPERF := $(TI_MOBILE_SDK)\build\win32\gperf
# endif

LOCAL_SRC_FILES := $(patsubst $(LOCAL_PATH)/%,%,$(ABS_SRC_FILES)) \
	$(patsubst $(LOCAL_PATH)/%,%,$(BOOTSTRAP_CPP))

$(BOOTSTRAP_CPP): $(GEN_DIR)/KrollGeneratedBindings.cpp $(GEN_DIR)/BootstrapJS.cpp

$(GEN_DIR)/KrollGeneratedBindings.cpp:
	$(GPERF) -L C++ -E -t "$(GEN_DIR)/KrollGeneratedBindings.gperf" > "$(GEN_DIR)/KrollGeneratedBindings.cpp"

include $(BUILD_SHARED_LIBRARY)
