LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := UartFrame
LOCAL_SRC_FILES := UartFrame.cpp Uart.cpp

include $(BUILD_SHARED_LIBRARY)
