LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_HEADER_LIBRARIES := generated_kernel_headers

LOCAL_SRC_FILES := \
     gui/SensorManager.cpp \
     ui/GraphicBuffer.cpp \
     ui/GraphicBufferAllocator.cpp \
     ui/GraphicBufferMapper.cpp

LOCAL_SHARED_LIBRARIES := \
    libbinder libcutils libgui libhardware liblog libsensor libsync libui libutils

LOCAL_MODULE := libcamera_shim
LOCAL_MODULE_TAGS := optional

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_HEADER_LIBRARIES := generated_kernel_headers

LOCAL_SRC_FILES := \
     libcameraservice/CameraFlashlight.cpp

LOCAL_C_INCLUDES += \
    $(LOCAL_PATH)/camera/include \
    system/media/camera/include \
    frameworks/av/services/camera/libcameraservice \
    system/libfmq/include \
    system/media/private/camera/include

LOCAL_SHARED_LIBRARIES := \
    libhardware \
    liblog \
    libgui \
    libutils \
    libcamera_client_htc \
    libcameraservice \
    camera.device@1.0-impl-htc_msm8960 \
    android.hardware.camera.device@1.0 \
    android.hardware.camera.provider@2.5 \
    android.hardware.camera.device@3.4 \
    android.hardware.camera.device@3.5

LOCAL_MODULE := libflashlight_shim
LOCAL_MODULE_TAGS := optional

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += \
    $(LOCAL_PATH)/camera/include \
    system/media/camera/include

LOCAL_SRC_FILES := \
    CameraWrapper.cpp

LOCAL_STATIC_LIBRARIES := \
    libarect \
    libbase

LOCAL_SHARED_LIBRARIES := \
    libhardware \
    liblog \
    libcamera_client_htc \
    libutils \
    libcutils \
    libdl \
    android.hidl.token@1.0-utils \
    android.hardware.graphics.bufferqueue@1.0 \
    android.hardware.graphics.bufferqueue@2.0

LOCAL_HEADER_LIBRARIES := libnativebase_headers

LOCAL_MODULE_RELATIVE_PATH := hw
LOCAL_MODULE := camera.$(TARGET_BOARD_PLATFORM)
LOCAL_MODULE_TAGS := optional
LOCAL_PROPRIETARY_MODULE := true

include $(BUILD_SHARED_LIBRARY)
