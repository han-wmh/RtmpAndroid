#include "YuvOperateJni.h"

#include <string.h>

#include "./log.h"
#include "yuvengine/yuvengine.h"

YuvEngine yuvEngine;

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_Yv12ToI420
        (JNIEnv *env, jobject jobject1, jbyteArray jyv12Data, jbyteArray jI420Data, jint jwidth,
         jint jheight) {
    jbyte *jYv12 = env->GetByteArrayElements(jyv12Data, NULL);
    jbyte *jI420 = env->GetByteArrayElements(jI420Data, NULL);
    unsigned char *pYv12 = (unsigned char *) jYv12;
    unsigned char *pI420 = (unsigned char *) jI420;

    yuvEngine.Yv12ToI420(pYv12, pI420, (int) jwidth, (int) jheight);
    env->ReleaseByteArrayElements(jyv12Data, jYv12, 0);
    env->ReleaseByteArrayElements(jI420Data, jI420, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_I420ToYv12
        (JNIEnv *env, jobject jobject1, jbyteArray jI420Data, jbyteArray jyv12Data,
         jint jwidth, jint jheight) {
    jbyte *jI420 = env->GetByteArrayElements(jI420Data, NULL);
    jbyte *jYv12 = env->GetByteArrayElements(jyv12Data, NULL);

    unsigned char *pI420 = (unsigned char *) jI420;
    unsigned char *pYv12 = (unsigned char *) jYv12;

    yuvEngine.I420ToYv12(pI420, pYv12, (int) jwidth, (int) jheight);
    env->ReleaseByteArrayElements(jI420Data, jI420, 0);
    env->ReleaseByteArrayElements(jyv12Data, jYv12, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_Nv21ToI420
        (JNIEnv *env, jobject jobject1, jbyteArray jNv21Data, jbyteArray jI420Data,
         jint jwidth, jint jheight) {
    jbyte *jNv21 = env->GetByteArrayElements(jNv21Data, NULL);
    jbyte *jI420 = env->GetByteArrayElements(jI420Data, NULL);

    unsigned char *pNv21 = (unsigned char *) jNv21;
    unsigned char *pI420 = (unsigned char *) jI420;

    yuvEngine.Nv21ToI420(pNv21, pI420, (int) jwidth, (int) jheight);
    env->ReleaseByteArrayElements(jNv21Data, jNv21, 0);
    env->ReleaseByteArrayElements(jI420Data, jI420, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_I420ToNv21
        (JNIEnv *env, jobject jobject1, jbyteArray jI420Data, jbyteArray jNv21Data,
         jint jwidth, jint jheight) {
    jbyte *jI420 = env->GetByteArrayElements(jI420Data, NULL);
    jbyte *jNv21 = env->GetByteArrayElements(jNv21Data, NULL);

    unsigned char *pI420 = (unsigned char *) jI420;
    unsigned char *pNv21 = (unsigned char *) jNv21;

    yuvEngine.I420ToNv21(pI420, pNv21, (int) jwidth, (int) jheight);
    env->ReleaseByteArrayElements(jI420Data, jI420, 0);
    env->ReleaseByteArrayElements(jNv21Data, jNv21, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_Nv21ToYv12
        (JNIEnv *env, jobject jobject1, jbyteArray jNv21Data, jbyteArray jYv12Data,
         jint jwidth, jint jheight) {
    jbyte *jNv21 = env->GetByteArrayElements(jNv21Data, NULL);
    jbyte *jYv12 = env->GetByteArrayElements(jYv12Data, NULL);

    unsigned char *pNv21 = (unsigned char *) jNv21;
    unsigned char *pYv12 = (unsigned char *) jYv12;

    yuvEngine.Nv21ToYv12(pNv21, pYv12, (int) jwidth, (int) jheight);
    env->ReleaseByteArrayElements(jNv21Data, jNv21, 0);
    env->ReleaseByteArrayElements(jYv12Data, jYv12, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_YV12ToNv21
        (JNIEnv *env, jobject jobject1, jbyteArray jYv12Data, jbyteArray jNv21Data,
         jint jwidth, jint jheight) {
    jbyte *jYv12 = env->GetByteArrayElements(jYv12Data, NULL);
    jbyte *jNv21 = env->GetByteArrayElements(jNv21Data, NULL);

    unsigned char *pYv12 = (unsigned char *) jYv12;
    unsigned char *pNv21 = (unsigned char *) jNv21;

    yuvEngine.Yv12ToNv21(pYv12, pNv21, (int) jwidth, (int) jheight);
    env->ReleaseByteArrayElements(jYv12Data, jYv12, 0);
    env->ReleaseByteArrayElements(jNv21Data, jNv21, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_Nv21ToNv12
        (JNIEnv *env, jobject jobject1, jbyteArray jNv21Data, jbyteArray jNv12Data,
         jint jwidth, jint jheight) {
    jbyte *jNv21 = env->GetByteArrayElements(jNv21Data, NULL);
    jbyte *jNv12 = env->GetByteArrayElements(jNv12Data, NULL);

    unsigned char *pNv21 = (unsigned char *) jNv21;
    unsigned char *pNv12 = (unsigned char *) jNv12;

    yuvEngine.Nv21ToNv12(pNv21, pNv12, (int) jwidth, (int) jheight);
    env->ReleaseByteArrayElements(jNv21Data, jNv21, 0);
    env->ReleaseByteArrayElements(jNv12Data, jNv12, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_Nv12ToNv21
        (JNIEnv *env, jobject jobject1, jbyteArray jNv12Data, jbyteArray jNv21Data,
         jint jwidth, jint jheight) {
    jbyte *jNv12 = env->GetByteArrayElements(jNv12Data, NULL);
    jbyte *jNv21 = env->GetByteArrayElements(jNv21Data, NULL);

    unsigned char *pNv12 = (unsigned char *) jNv12;
    unsigned char *pNv21 = (unsigned char *) jNv21;

    yuvEngine.Nv12ToNv21(pNv12, pNv21, (int) jwidth, (int) jheight);
    env->ReleaseByteArrayElements(jNv12Data, jNv12, 0);
    env->ReleaseByteArrayElements(jNv21Data, jNv21, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_CutCommonYuv
        (JNIEnv *env, jobject jobject1 __unused, jint jyuvType, jint jstartX, jint jstartY,
         jbyteArray jsrcYuvbyteArray, jint jsrcW, jint jsrcH, jbyteArray jtarYuvbyteArray,
         jint jcutW, jint jcutH) {
    jbyte *jsrcYuv = env->GetByteArrayElements(jsrcYuvbyteArray, NULL);
    jbyte *jtarYuv = env->GetByteArrayElements(jtarYuvbyteArray, NULL);

    unsigned char *pSrcYuv = (unsigned char *) jsrcYuv;
    unsigned char *pTarYuv = (unsigned char *) jtarYuv;

    yuvEngine.cutCommonYuv((int) jyuvType, (int) jstartX, (int) jstartY, pSrcYuv, (int) jsrcW,
                           (int) jsrcH, pTarYuv, (int) jcutW, (int) jcutH);
    env->ReleaseByteArrayElements(jsrcYuvbyteArray, jsrcYuv, 0);
    env->ReleaseByteArrayElements(jtarYuvbyteArray, jtarYuv, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_GetSpecYuvBuffer
        (JNIEnv *env, jobject jobject1, jint jyuvType, jbyteArray jdstBufbyteArray,
         jbyteArray jsrcYuvbyteArray, jint jsrcW, jint jsrcH, jint jdirty_Y, jint jdirty_UV) {
    jbyte *jdstBuf = env->GetByteArrayElements(jdstBufbyteArray, NULL);
    jbyte *jsrcYuv = env->GetByteArrayElements(jsrcYuvbyteArray, NULL);

    unsigned char *pDstBuf = (unsigned char *) jdstBuf;
    unsigned char *pSrcYuv = (unsigned char *) jsrcYuv;

    yuvEngine.getSpecYuvBuffer((int) jyuvType, pDstBuf, pSrcYuv, (int) jsrcW, (int) jsrcH,
                               (int) jdirty_Y, (int) jdirty_UV);
    env->ReleaseByteArrayElements(jdstBufbyteArray, jdstBuf, 0);
    env->ReleaseByteArrayElements(jsrcYuvbyteArray, jsrcYuv, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_YuvAddWaterMark
        (JNIEnv *env, jobject jobject1, jint jyuvType, jint jstartX, jint jstartY,
         jbyteArray jwaterMarkData, jint jwaterMarkW, jint jwaterMarkH, jbyteArray jyuvData,
         jint jyuvW, jint jyuvH) {
    jbyte *jwaterMark = env->GetByteArrayElements(jwaterMarkData, NULL);
    jbyte *jyuv = env->GetByteArrayElements(jyuvData, NULL);

    unsigned char *pWaterMark = (unsigned char *) jwaterMark;
    unsigned char *pYuv = (unsigned char *) jyuv;

    yuvEngine.yuvAddWaterMark((int) jyuvType, (int) jstartX, (int) jstartY, pWaterMark,
                              (int) jwaterMarkW, (int) jwaterMarkH, pYuv, (int) jyuvW, (int) jyuvH);
    env->ReleaseByteArrayElements(jwaterMarkData, jwaterMark, 0);
    env->ReleaseByteArrayElements(jyuvData, jyuv, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_Nv21ClockWiseRotate90
        (JNIEnv *env, jobject jobject1, jbyteArray jsrcNv21, jint jsrcWidth,
         jint jsrcHeight, jbyteArray joutData, jintArray joutWidth, jintArray joutHeight) {
    jbyte *jsrcNv21Byte = env->GetByteArrayElements(jsrcNv21, NULL);
    jbyte *joutDataByte = env->GetByteArrayElements(joutData, NULL);

    jint *joutWidthInt = env->GetIntArrayElements(joutWidth, NULL);
    jint *joutHeightInt = env->GetIntArrayElements(joutHeight, NULL);

    int *poutWidth = (int *) joutWidthInt;
    int *poutHeight = (int *) joutHeightInt;

    unsigned char *pSrcNv21 = (unsigned char *) jsrcNv21Byte;
    unsigned char *pOutData = (unsigned char *) joutDataByte;

    yuvEngine.Nv21ClockWiseRotate90(pSrcNv21, (int) jsrcWidth, (int) jsrcHeight, pOutData,
                                    poutWidth, poutHeight);

//    LOGD("%s: outWidth: %d,outHeight:%d", __FUNCTION__, *poutWidth, *poutHeight);
    env->ReleaseIntArrayElements(joutWidth, joutWidthInt, 0);
    env->ReleaseIntArrayElements(joutHeight, joutHeightInt, 0);
    env->ReleaseByteArrayElements(jsrcNv21, jsrcNv21Byte, 0);
    env->ReleaseByteArrayElements(joutData, joutDataByte, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_Nv21ClockWiseRotate180
        (JNIEnv *env, jobject jobject1, jbyteArray jsrcNv21, jint jsrcWidth,
         jint jsrcHeight, jbyteArray joutData, jintArray joutWidth, jintArray joutHeight) {
    jbyte *jsrcNv21Byte = env->GetByteArrayElements(jsrcNv21, NULL);
    jbyte *joutDataByte = env->GetByteArrayElements(joutData, NULL);

    jint *joutWidthInt = env->GetIntArrayElements(joutWidth, NULL);
    jint *joutHeightInt = env->GetIntArrayElements(joutHeight, NULL);

    int *poutWidth = (int *) joutWidthInt;
    int *poutHeight = (int *) joutHeightInt;

    unsigned char *pSrcNv21 = (unsigned char *) jsrcNv21Byte;
    unsigned char *pOutData = (unsigned char *) joutDataByte;

    yuvEngine.Nv21ClockWiseRotate180(pSrcNv21, (int) jsrcWidth, (int) jsrcHeight, pOutData,
                                     poutWidth, poutHeight);

    env->ReleaseIntArrayElements(joutWidth, joutWidthInt, 0);
    env->ReleaseIntArrayElements(joutHeight, joutHeightInt, 0);
    env->ReleaseByteArrayElements(jsrcNv21, jsrcNv21Byte, 0);
    env->ReleaseByteArrayElements(joutData, joutDataByte, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_Nv21ClockWiseRotate270
        (JNIEnv *env, jobject jobject1, jbyteArray jsrcNv21, jint jsrcWidth,
         jint jsrcHeight, jbyteArray joutData, jintArray joutWidth, jintArray joutHeight) {
    jbyte *jsrcNv21Byte = env->GetByteArrayElements(jsrcNv21, NULL);
    jbyte *joutDataByte = env->GetByteArrayElements(joutData, NULL);

    jint *joutWidthInt = env->GetIntArrayElements(joutWidth, NULL);
    jint *joutHeightInt = env->GetIntArrayElements(joutHeight, NULL);

    int *poutWidth = (int *) joutWidthInt;
    int *poutHeight = (int *) joutHeightInt;

    unsigned char *pSrcNv21 = (unsigned char *) jsrcNv21Byte;
    unsigned char *pOutData = (unsigned char *) joutDataByte;

    yuvEngine.Nv21ClockWiseRotate270(pSrcNv21, (int) jsrcWidth, (int) jsrcHeight, pOutData,
                                     poutWidth, poutHeight);

    env->ReleaseIntArrayElements(joutWidth, joutWidthInt, 0);
    env->ReleaseIntArrayElements(joutHeight, joutHeightInt, 0);
    env->ReleaseByteArrayElements(jsrcNv21, jsrcNv21Byte, 0);
    env->ReleaseByteArrayElements(joutData, joutDataByte, 0);
}

//I420(YUV420P)图像顺时针旋转90度
JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_I420ClockWiseRotate90
        (JNIEnv *env, jobject jobject1, jbyteArray jsrcI420, jint jsrcWidth,
         jint jsrcHeight, jbyteArray joutData, jintArray joutWidth, jintArray joutHeight) {
    jbyte *jsrcI420Byte = env->GetByteArrayElements(jsrcI420, NULL);
    jbyte *joutDataByte = env->GetByteArrayElements(joutData, NULL);

    jint *joutWidthInt = env->GetIntArrayElements(joutWidth, NULL);
    jint *joutHeightInt = env->GetIntArrayElements(joutHeight, NULL);

    int *poutWidth = (int *) joutWidthInt;
    int *poutHeight = (int *) joutHeightInt;

    unsigned char *pSrcI420 = (unsigned char *) jsrcI420Byte;
    unsigned char *pOutData = (unsigned char *) joutDataByte;

    yuvEngine.I420ClockWiseRotate90(pSrcI420, (int) jsrcWidth, (int) jsrcHeight, pOutData,
                                    poutWidth, poutHeight);

//    LOGD("%s: outWidth: %d,outHeight:%d", __FUNCTION__, *poutWidth, *poutHeight);
    env->ReleaseIntArrayElements(joutWidth, joutWidthInt, 0);
    env->ReleaseIntArrayElements(joutHeight, joutHeightInt, 0);
    env->ReleaseByteArrayElements(jsrcI420, jsrcI420Byte, 0);
    env->ReleaseByteArrayElements(joutData, joutDataByte, 0);
}

JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_Nv12ClockWiseRotate90
        (JNIEnv *env, jobject jobject1, jbyteArray jsrcNv12, jint jsrcWidth,
         jint jsrcHeight, jbyteArray joutData, jintArray joutWidth, jintArray joutHeight) {
    jbyte *jsrcNv12Byte = env->GetByteArrayElements(jsrcNv12, NULL);
    jbyte *joutDataByte = env->GetByteArrayElements(joutData, NULL);

    jint *joutWidthInt = env->GetIntArrayElements(joutWidth, NULL);
    jint *joutHeightInt = env->GetIntArrayElements(joutHeight, NULL);

    int *poutWidth = (int *) joutWidthInt;
    int *poutHeight = (int *) joutHeightInt;

    unsigned char *pSrcNv12 = (unsigned char *) jsrcNv12Byte;
    unsigned char *pOutData = (unsigned char *) joutDataByte;

    yuvEngine.Nv12ClockWiseRotate90(pSrcNv12, (int) jsrcWidth, (int) jsrcHeight, pOutData,
                                    poutWidth, poutHeight);

//    LOGD("%s: outWidth: %d,outHeight:%d", __FUNCTION__, *poutWidth, *poutHeight);
    env->ReleaseIntArrayElements(joutWidth, joutWidthInt, 0);
    env->ReleaseIntArrayElements(joutHeight, joutHeightInt, 0);
    env->ReleaseByteArrayElements(jsrcNv12, jsrcNv12Byte, 0);
    env->ReleaseByteArrayElements(joutData, joutDataByte, 0);
}

//Yv12图像顺时针旋转90度
JNIEXPORT void JNICALL Java_com_han_rtmpandroid_utils_YuvConvertor_Yv12ClockWiseRotate90
        (JNIEnv *env, jobject jobject1, jbyteArray jsrcYv12, jint jsrcWidth,
         jint jsrcHeight, jbyteArray joutData, jintArray joutWidth, jintArray joutHeight) {
    jbyte *jsrcYv12Byte = env->GetByteArrayElements(jsrcYv12, NULL);
    jbyte *joutDataByte = env->GetByteArrayElements(joutData, NULL);

    jint *joutWidthInt = env->GetIntArrayElements(joutWidth, NULL);
    jint *joutHeightInt = env->GetIntArrayElements(joutHeight, NULL);

    int *poutWidth = (int *) joutWidthInt;
    int *poutHeight = (int *) joutHeightInt;

    unsigned char *pSrcYv12 = (unsigned char *) jsrcYv12Byte;
    unsigned char *pOutData = (unsigned char *) joutDataByte;

    yuvEngine.Yv12ClockWiseRotate90(pSrcYv12, (int) jsrcWidth, (int) jsrcHeight, pOutData,
                                    poutWidth, poutHeight);

//    LOGD("%s: outWidth: %d,outHeight:%d", __FUNCTION__, *poutWidth, *poutHeight);
    env->ReleaseIntArrayElements(joutWidth, joutWidthInt, 0);
    env->ReleaseIntArrayElements(joutHeight, joutHeightInt, 0);
    env->ReleaseByteArrayElements(jsrcYv12, jsrcYv12Byte, 0);
    env->ReleaseByteArrayElements(joutData, joutDataByte, 0);
}


