#include <jni.h>
#include <string>
#include <malloc.h>

#include "log.h"
#include "librtmp/rtmp.h"

RTMP* m_pRtmp;
int startTime;

extern "C"
JNIEXPORT jlong JNICALL
Java_com_han_rtmpandroid_utils_RtmpJni_initRtmpJni(JNIEnv *env, jobject thiz, jstring url) {
    bool ret = FALSE;
    const char *pushUrl = env->GetStringUTFChars(url, JNI_FALSE);
    LOGD("rtmp_push:url:%s", pushUrl);
    m_pRtmp = RTMP_Alloc();
    RTMP_Init(m_pRtmp);

    /*设置URL*/
    if (RTMP_SetupURL(m_pRtmp,(char*)pushUrl) == FALSE)
    {
        RTMP_Free(m_pRtmp);
        m_pRtmp = NULL;
        ret = FALSE;
        goto end;
    }

    /*设置可写,即发布流,这个函数必须在连接前使用,否则无效*/
    RTMP_EnableWrite(m_pRtmp);

    /*连接服务器*/
    if (RTMP_Connect(m_pRtmp, NULL) == FALSE)
    {
        RTMP_Free(m_pRtmp);
        m_pRtmp = NULL;
        ret = FALSE;
        goto end;
    }

    /*连接流*/
    if (RTMP_ConnectStream(m_pRtmp,0) == FALSE)
    {
        RTMP_Close(m_pRtmp);
        RTMP_Free(m_pRtmp);
        m_pRtmp = NULL;
        ret = FALSE;
        goto end;
    }
    end:
    startTime = RTMP_GetTime();
    env->ReleaseStringUTFChars(url, pushUrl);
    return ret;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_han_rtmpandroid_utils_RtmpJni_sendSpsAndPpsJni(JNIEnv *env, jobject thiz, jbyteArray sps,
                                                        jint sps_len, jbyteArray pps,
                                                        jint pps_len) {
    jbyte* spsBytes = env->GetByteArrayElements(sps, NULL);
    jbyte* ppsBytes = env->GetByteArrayElements(pps, NULL);

    char *spsData = reinterpret_cast<char *>(spsBytes);
    char *ppsData = reinterpret_cast<char *>(ppsBytes);
    int bodySize = sps_len + pps_len + 16;
    RTMPPacket *packet = new RTMPPacket;
    RTMPPacket_Alloc(packet, bodySize);
    RTMPPacket_Reset(packet);

    // 帧数据 body
    char *body = packet->m_body;
    int i = 0;
    // flv文件推流格式中查找
    // frameType（1：关键 2：非关键） 4个bit 和 codecId (7:AVC) 4个bit组合而成1个字节
    // 0x17表示关键帧 0x27表示非关键帧
    body[i++] = 0x17;

    // 开始帧
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;

    // AVCDecoderConfigurationRecord
    body[i++] = 0x01;
    body[i++] = spsData[1]; // AVCProfileIndication
    body[i++] = spsData[2]; // profile_compatibility: 兼容性
    body[i++] = spsData[3]; // AVCLevelIndication: Profile level

    body[i++] = 0xFF; // 包长数据所使用的的字节数

    // sps
    body[i++] = 0xE1; // sps个数
    body[i++] = (sps_len >> 8) & 0xFF;
    body[i++] = sps_len & 0xFF;
    memcpy(&body[i], spsData, sps_len);
    i += sps_len;

    // pps
    body[i++] = 0x01;
    body[i++] = (pps_len >> 8) & 0xFF;
    body[i++] = pps_len & 0xFF;
    memcpy(&body[i], ppsData, pps_len);

    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = bodySize;
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    // 视频
    packet->m_nChannel = 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    packet->m_nInfoField2 = m_pRtmp->m_stream_id;
    int ret = RTMP_SendPacket(m_pRtmp, packet, 1);
    RTMPPacket_Free(packet);
    delete packet;
    packet = NULL;
    env->ReleaseByteArrayElements(sps, spsBytes, 0);
    env->ReleaseByteArrayElements(pps, ppsBytes, 0);
    LOGD("=========send video h264 sps pps=====ret: %d" ,ret);
    return ret;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_han_rtmpandroid_utils_RtmpJni_sendVideoFrameJni(JNIEnv *env, jobject thiz,
                                                         jbyteArray frame, jint len, jboolean keyFrame,
                                                         jint timestamp) {
    jbyte* jframe = env->GetByteArrayElements(frame, NULL);
    char *frameData = reinterpret_cast<char *>(jframe);
    int bodySize = len + 9;
    RTMPPacket *packet = new RTMPPacket;
    RTMPPacket_Alloc(packet, bodySize);
    RTMPPacket_Reset(packet);

    char *body = packet->m_body;
    int i = 0;
    /*去掉StartCode帧界定符--导致vlc无法看到实时画面*/
//    if (frameData[2] == 0x00) {/*00 00 00 01*/
//        frameData += 4;
//        len -= 4;
//    } else if (frameData[2] == 0x01) {/*00 00 01*/
//        frameData += 3;
//        len -= 3;
//    }

    //提取NALU Header中type字段,Nalu头一个字节，type是其后5bit
    int type = frameData[0] & 0x1f;

    if (keyFrame) {
        body[i++] = 0x17;
        LOGD("关键帧");
    } else {
        body[i++] = 0x27;
    }

    body[i++] = 0x01;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;

    body[i++] = (len >> 24) & 0xFF;
    body[i++] = (len >> 16) & 0xFF;
    body[i++] = (len >> 8) & 0xFF;
    body[i++] = len & 0xFF;
    memcpy(&body[i], frameData, len);

    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = bodySize;
    packet->m_nTimeStamp = RTMP_GetTime() - startTime;
    packet->m_hasAbsTimestamp = 0;
    packet->m_nChannel = 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nInfoField2 = m_pRtmp->m_stream_id;
    int ret = RTMP_SendPacket(m_pRtmp, packet, 1);
    RTMPPacket_Free(packet);
    delete packet;
    packet = NULL;
    env->ReleaseByteArrayElements(frame, jframe, JNI_FALSE);
    LOGD("=========send video h264 data=====ret: %d" ,ret);
    return ret;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_han_rtmpandroid_utils_RtmpJni_sendAacSpecJni(JNIEnv *env, jobject thiz, jbyteArray data,
                                                      jint len) {
    jbyte *audioData = env->GetByteArrayElements(data, JNI_FALSE);
    char *audio = reinterpret_cast<char *>(audioData);
    int bodySize = len + 2;
    RTMPPacket *packet = new RTMPPacket;
    RTMPPacket_Alloc(packet, bodySize);
    RTMPPacket_Reset(packet);

    char *body = packet->m_body;
    body[0] = 0xAF;
    body[1] = 0x00;
    memcpy(&body[2], audio, len);

    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = bodySize;
    packet->m_nTimeStamp = RTMP_GetTime() - startTime;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nChannel = 0x04;
    packet->m_nInfoField2 = m_pRtmp->m_stream_id;
    int ret = RTMP_SendPacket(m_pRtmp, packet, 1);
    RTMPPacket_Free(packet);
    delete packet;
    packet = NULL;
    env->ReleaseByteArrayElements(data, audioData, JNI_FALSE);
    LOGD("=========send audio aac spec data=====ret: %d" ,ret);
    return ret;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_han_rtmpandroid_utils_RtmpJni_sendAacDataJni(JNIEnv *env, jobject thiz, jbyteArray data,
                                                      jint len, jint timestamp) {
    jbyte *audioData = env->GetByteArrayElements(data, JNI_FALSE);
    char *audio = reinterpret_cast<char *>(audioData);
    int bodySize = len + 2;
    RTMPPacket *packet = new RTMPPacket;
    RTMPPacket_Alloc(packet, bodySize);
    RTMPPacket_Reset(packet);

    char *body = packet->m_body;
    body[0] = 0xAF;
    body[1] = 0x01;
    memcpy(&body[2], audio, len);

    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = bodySize;
    packet->m_nTimeStamp = RTMP_GetTime() - startTime;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nChannel = 0x04;
    packet->m_nInfoField2 = m_pRtmp->m_stream_id;
    int ret = RTMP_SendPacket(m_pRtmp, packet, 1);
    RTMPPacket_Free(packet);
    delete packet;
    packet = NULL;
    env->ReleaseByteArrayElements(data, audioData, JNI_FALSE);
    LOGD("=========send audio aac data=====ret: %d" ,ret);
    return ret;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_han_rtmpandroid_utils_RtmpJni_stopRtmpJni(JNIEnv *env, jobject thiz) {
    if(m_pRtmp)
    {
        RTMP_Close(m_pRtmp);
        RTMP_Free(m_pRtmp);
        m_pRtmp = NULL;
    }
    return TRUE;
}