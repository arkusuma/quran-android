APP_PLATFORM := android-21

BASE_PATH := $(call my-dir)
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm
LOCAL_MODULE := render
LOCAL_C_INCLUDES := $(LOCAL_PATH)/freetype/include $(LOCAL_PATH)/harfbuzz/src
LOCAL_SRC_FILES := render.c
LOCAL_STATIC_LIBRARIES := harfbuzz ft2
LOCAL_LDLIBS := -llog -ljnigraphics
LOCAL_CFLAGS += -W -Wall
LOCAL_CFLAGS += -fPIC -DPIC

include $(BUILD_SHARED_LIBRARY)
include $(BASE_PATH)/freetype/Android.mk
include $(BASE_PATH)/harfbuzz/Android.mk
