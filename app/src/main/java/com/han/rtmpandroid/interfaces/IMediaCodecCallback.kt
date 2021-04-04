package com.han.rtmpandroid.interfaces

interface IMediaCodecCallback {
    /**
     * sps pps信息
     */
    fun onSpsPPS(sps: ByteArray, pps: ByteArray, timeStamp: Long)

    /**
     * 视频帧
     */
    fun onVideoFrame(data: ByteArray, keyFrame:Boolean, timeStamp: Long)

    /**
     * 音频关键帧
     */
    fun onAudioSpec(data: ByteArray)

    /**
     * 音频帧
     */
    fun onAudioFrame(data: ByteArray, timeStamp: Long)
}