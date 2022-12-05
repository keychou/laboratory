LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

src_dirs := app/src/main/java/
res_dirs := app/src/main/res/
res_dirs += ./../../../prebuilts/sdk/current/support/v7/appcompat/res
LOCAL_MANIFEST_FILE := app/src/main/AndroidManifest.xml

LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dirs))

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 \
                               android-support-v13 \
                               android-support-v7-appcompat \
                               android-support-v7-palette \
                               android-support-v7-mediarouter \
                               android-support-v7-gridlayout \
                               android-support-v7-cardview \
                               android-support-v7-palette \
                               android-support-v7-recyclerview
LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.v7.appcompat

LOCAL_DEX_PREOPT := false
LOCAL_PACKAGE_NAME := MyDemo
LOCAL_MODULE_TAG := tests
LOCAL_SDK_VERSION := current
#LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE)

