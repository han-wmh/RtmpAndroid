package com.han.rtmpandroid.utils

class RtmpJni {
    fun initRtmp(url: String?): Long {
        return initRtmpJni(url)
    }

    fun sendSpsAndPps(sps: ByteArray?, spsLen: Int, pps: ByteArray?, ppsLen: Int): Int {
        return sendSpsAndPpsJni(sps, spsLen, pps, ppsLen)
    }

    fun sendVideoFrame(frame: ByteArray?, len: Int, keyFrame: Boolean, timestamp: Int): Int {
        return sendVideoFrameJni(frame, len, keyFrame, timestamp)
    }

    fun sendAacSpec(data: ByteArray?, len: Int): Int {
        return sendAacSpecJni(data, len)
    }

    fun sendAacData(data: ByteArray?, len: Int, timestamp: Int): Int {
        return sendAacDataJni(data, len, timestamp)
    }

    fun stopRtmp(): Int {
        return stopRtmpJni()
    }

    private external fun initRtmpJni(url: String?): Long

    private external fun sendSpsAndPpsJni(sps: ByteArray?, spsLen: Int, pps: ByteArray?, ppsLen: Int): Int

    private external fun sendVideoFrameJni(frame: ByteArray?, len: Int, keyFrame: Boolean, timestamp: Int): Int

    private external fun sendAacSpecJni(data: ByteArray?, len: Int): Int

    private external fun sendAacDataJni(data: ByteArray?, len: Int, timestamp: Int): Int

    private external fun stopRtmpJni(): Int
}