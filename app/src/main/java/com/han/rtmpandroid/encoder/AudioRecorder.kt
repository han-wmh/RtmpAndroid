package com.han.rtmpandroid.encoder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class AudioRecorder(var sampleRateInHz: Int = 44100, var channelConfig: Int = AudioFormat.CHANNEL_IN_STEREO, var audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT) {
    private var audioRecord: AudioRecord? = null
    private var bufferSizeInBytes = 0
    private var isStart = false
    private var readSize = 0
    private var onRecordLisener: OnRecordLisener? = null

    init {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRateInHz,
                channelConfig,
                audioFormat,
                bufferSizeInBytes)
    }

    /**
     * 开始录音
     */
    fun startRecord() {
        Thread(Runnable {
            isStart = true
            audioRecord!!.startRecording()
            val audioData = ByteArray(bufferSizeInBytes)
            while (isStart) {
                readSize = audioRecord!!.read(audioData, 0, bufferSizeInBytes)
                if (onRecordLisener != null) {
                    onRecordLisener!!.recordByte(audioData, readSize)
                }
            }
            if (audioRecord != null) {
                audioRecord!!.stop()
            }
        }).start()
    }

    /**
     * 设置监听器
     */
    fun setOnRecordLisener(onRecordLisener: OnRecordLisener?) {
        this.onRecordLisener = onRecordLisener
    }

    /**
     * 停止录音
     */
    fun stopRecord() {
        isStart = false
    }

    companion object {
        /**
         * 获取实例对象
         */
        fun getInstance(): AudioRecorder {
            return Holder.INSTANCE
        }
    }

    /**
     * 修改audioRecorder参数
     */
    fun setAudioRecorderParameters(sampleRateInHz: Int, channelConfig: Int, audioFormat: Int) {
        if (isStart) {
            stopRecord()
        }
        this.sampleRateInHz = sampleRateInHz
        this.channelConfig = channelConfig
        this.audioFormat = audioFormat
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRateInHz,
                channelConfig,
                audioFormat,
                bufferSizeInBytes)
    }

    /**
     * 监听接口
     */
    interface OnRecordLisener {
        fun recordByte(audioData: ByteArray?, readSize: Int)
    }

    /**
     * 是否开始录音
     */
    fun isStart(): Boolean {
        return isStart
    }

    /**
     * 获取数组大小
     */
    fun getBufferSize(): Int {
        return bufferSizeInBytes
    }

    fun getSampleRate(): Int {
        return sampleRateInHz
    }

    fun getChannelCount(): Int {
        return channelConfig
    }

    class Holder private constructor() {
        companion object {
            val INSTANCE: AudioRecorder = AudioRecorder()
        }
    }
}