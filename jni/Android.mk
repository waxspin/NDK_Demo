# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ndkdemo
LOCAL_SRC_FILES += ndkdemo.c

LOCAL_STATIC_LIBRARIES := cpufeatures

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

include $(NDK_ROOT)/sources/cpufeatures/Android.mk

APP_ABI := armeabi armeabi-v7a


##########################
#LOCAL_PATH := $(call my-dir)

#include $(CLEAR_VARS)

#LOCAL_MODULE := radtmfdecoder

#LOCAL_SRC_FILES += radtmfdecoder.c


#LOCAL_C_INCLUDES := $(NDK_ROOT)/sources/cpufeatures

#LOCAL_STATIC_LIBRARIES := cpufeatures

#LOCAL_LDLIBS := -llog

#include $(BUILD_SHARED_LIBRARY)

#include $(NDK_ROOT)/sources/cpufeatures/Android.mk


#Application.mk -- may be unnecessary
# Build both ARMv5TE and ARMv7-A machine code.
#APP_ABI := armeabi armeabi-v7a
