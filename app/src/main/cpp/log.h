
#ifndef _LOG_H_
#define _LOG_H_

#include <android/log.h>


#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"test_native",__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,"test_native",__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"test_native",__VA_ARGS__)

#endif
