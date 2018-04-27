/*
 * Created by zhaocaiguang on 2017/8/8.
 */
#include <string.h>
#include <jni.h>
#include "Test.h"
#include<fcntl.h>
#include<stdlib.h>
#include <unistd.h>
#include<sys/types.h>  /*提供类型pid_t,size_t的定义*/
#include<sys/stat.h>
#include<fcntl.h>
typedef unsigned char       u8;
typedef signed   char       s8;
typedef unsigned short      u16;
typedef          short      s16;
typedef unsigned int        u32;
typedef          int        s32;
typedef unsigned long long  u64;
typedef          long long  s64;
typedef unsigned int        ticks;
int fd=0;
char NODE_PATH[]="/proc/gmnode";
using namespace android;

int _open_node(char *node_path){
    fd= open(node_path,O_CREAT|O_RDWR);
    if(fd<0){
        LOGE("Open Node %s Failed %d",node_path,fd);
        return -1;
    }
    LOGE("Open Node Succeed fd %d",fd);
    return 0;
}

int _close_node(char *node_path){
    if(fd>0){
        LOGE("Close Node[%s] fd is %d",node_path,fd);
        close(fd);
        return 0;
    }
    LOGE("Node not Opened,can not close,fd is %d",fd);
    return -1;
}

int _allocate_resource(JNIEnv * env, jobject obj){
    if(_open_node(NODE_PATH)<0)
        return -1;
    return 0;
}

int _check_quec(JNIEnv * env, jobject obj){
    LOGI("Client Check Node Start");
    int retry = 0;
    int ret = -1;
    sp < IBinder > binder = NULL;
    sp < IServiceManager > sm = defaultServiceManager();
    binder = sm->getService(String16("service.quecservice"));
    while(retry<10){
        if (binder == NULL) {
            LOGE("getService failed retry-%d",retry);
            retry++;
            sleep(1);
            binder = sm->getService(String16("service.quecservice"));
            continue;
        }
        LOGI("getService success");
        break;
    }

    if(retry == 10){
        LOGE("getService failed");
        return -1;
    }

    sp<IQuecService> cs = interface_cast < IQuecService > (binder);
    ret = cs->_is_quectel();
    LOGI("Client Check Quec is %d",ret);
    if(ret <= 0)
        LOGE("Client Check Quec FAILED");
    else
        LOGE("BINGO:Client Check Quec Success");
    return ret;
}

int _string_From_JNI(JNIEnv * env, jobject obj,jbyteArray t) {
    LOGI("read String start");
    jbyte *arr=env-> GetByteArrayElements(t, 0);
    int ret = 0;
    char res[256];
    if(_open_node(NODE_PATH)<0){
        LOGI("read open node failed");
        return -1;
    }

    LOGI("fd is %d",fd);
    ret = read(fd,res,256);
    LOGI("Read node ret is %d [%zd]%s",ret,strlen(res),res);
    res[strlen(res)+1]='\0';
    memcpy((char*)arr,res,strlen(res));
    env->ReleaseByteArrayElements(t,arr,0);
    close(fd);
    return ret;
}

jstring _test_JNI(JNIEnv * env, jobject obj,jbyteArray t) {
    LOGI("test JNI only");
    jbyte *arr=env-> GetByteArrayElements(t, 0);
    int ret = 0;
    char res[256];
    ret = read(fd,res,256);
    LOGI("Read node ret is %d [%zd]%s",ret,strlen(res),res);
    res[strlen(res)+1]='\0';
    memcpy((char*)arr,res,strlen(res));
    env->ReleaseByteArrayElements(t,arr,0);
    return env->NewStringUTF("testJNI");
}

static JNINativeMethod gmethods[] = {
                                        { "readJNI", "([B)I", (void*) _string_From_JNI },
                                        { "openNode", "()I", (void*) _allocate_resource },
                                        { "testJNI", "([B)Ljava/lang/String;", (void*) _test_JNI },
                                        { "checkQuec", "()I", (void*) _check_quec },
};

static int registerNativeMethods(JNIEnv * env, const char* className,
        JNINativeMethod* gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL)
        return JNI_FALSE;
    if ((env->RegisterNatives(clazz, gMethods, numMethods) < 0)) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static int registerNatives(JNIEnv* env) {
    if (!registerNativeMethods(env, "com/quectel/jni/QuecJNI", gmethods,
            sizeof(gmethods) / sizeof(gmethods[0]))) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    jint result = -1;
    JNIEnv* env = NULL;
    LOGI("JNI_OnLoad");
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4)) {
        LOGE("JNI_OnLoad Error to GetEnv");
        goto fail;
    }
    if (registerNatives(env) != JNI_TRUE) {
        LOGE("JNI_OnLoad Error to registerNatives");
        goto fail;
    }
    result = JNI_VERSION_1_4;
    fail: return result;
}
