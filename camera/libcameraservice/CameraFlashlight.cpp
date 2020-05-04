/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "CameraFlashlight_shim"
#define ATRACE_TAG ATRACE_TAG_CAMERA
// #define LOG_NDEBUG 0

#include <utils/Log.h>
#include <utils/Trace.h>
#include <cutils/properties.h>

#include "camera/CameraMetadata.h"
#include "CameraFlashlight.h"
#include "gui/IGraphicBufferConsumer.h"
#include "gui/BufferQueue.h"
#include "camera/camera2/CaptureRequest.h"
#include "device3/Camera3Device.h"


namespace android {


static void notifyCallback(int32_t, int32_t, int32_t, void*) {
    /* Empty */
}

static void dataCallback(int32_t, const sp<IMemory>&, camera_frame_metadata_t*, void*) {
    /* Empty */
}

static void dataCallbackTimestamp(nsecs_t, int32_t, const sp<IMemory>&, void*) {
    /* Empty */
}

static void dataCallbackTimestampBatch(int32_t, const std::vector<HandleTimestampMessage>&,
        void*) {
    /* Empty */
}

status_t
CameraHardwareInterfaceFlashControl::connectCameraDevice(
        const String8& cameraId) {
    sp<CameraHardwareInterface> device =
            new CameraHardwareInterface(cameraId.string());

    status_t res = device->initialize(mProviderManager);
    if (res) {
        ALOGE("%s: initializing camera %s failed", __FUNCTION__,
                cameraId.string());
        return res;
    }

    // need to set __get_memory in set_callbacks().
    device->setCallbacks(notifyCallback, dataCallback, dataCallbackTimestamp,
            dataCallbackTimestampBatch, this);

    mParameters = device->getParameters();

    int32_t width, height;
    res = getSmallestSurfaceSize(&width, &height);
    if (res) {
        ALOGE("%s: failed to get smallest surface size for camera %s",
                __FUNCTION__, cameraId.string());
        return res;
    }

    res = initializePreviewWindow(device, width, height);
    if (res) {
        ALOGE("%s: failed to initialize preview window for camera %s",
                __FUNCTION__, cameraId.string());
        return res;
    }

    mCameraId = cameraId;
    mDevice = device;
    return OK;
}

}
