package com.han.rtmpandroid

import android.animation.Keyframe
import android.os.Handler
import android.os.HandlerThread
import com.han.rtmpandroid.encoder.AudioRecorder
import com.han.rtmpandroid.encoder.MediaEncoder
import com.han.rtmpandroid.interfaces.IMediaCodecCallback
import com.han.rtmpandroid.utils.RtmpJni
import com.han.rtmpandroid.utils.YuvConvertor

class MediaRtmpPush(private val rtmpUrl: String, private val width: Int, private val height: Int, private val fps: Int) {
    /**
     * yuvConvertor
     */
    private var yuvConvertor: YuvConvertor? = null
    /**
     * handlerThread
     */
    private var handlerThread: HandlerThread? = null
    /**
     * handler
     */
    private var handler: Handler? = null
    /**
     * mediaEncoder
     */
    private var mediaEncoder: MediaEncoder? = null
    @Volatile
    private var isPush: Boolean = false
    private var rtmpJni: RtmpJni? = null
    /**
     * h264 aac推流数据回调
     */
    private var iMediaCodecCallback: IMediaCodecCallback = object : IMediaCodecCallback {
        override fun onSpsPPS(sps: ByteArray, pps: ByteArray, timeStamp: Long) {
            handler?.post {
                rtmpJni?.sendSpsAndPps(sps, sps.size, pps, pps.size)
            }
        }

        override fun onVideoFrame(data: ByteArray, keyframe: Boolean, timeStamp: Long) {
            handler?.post {
                rtmpJni?.sendVideoFrame(data, data.size, keyframe, timeStamp.toInt())
            }
        }

        override fun onAudioSpec(data: ByteArray) {
            handler?.post {
                rtmpJni?.sendAacSpec(data, data.size)
            }
        }

        override fun onAudioFrame(data: ByteArray, timeStamp: Long) {
            handler?.post {
                rtmpJni?.sendAacData(data, data.size, timeStamp.toInt())
            }
        }
    }

    init {
        yuvConvertor = YuvConvertor()
        handlerThread = HandlerThread("MediaRtmpPush")
        handlerThread!!.start()
        handler = Handler(handlerThread!!.looper)
        rtmpJni = RtmpJni()
        initEncoder()
    }

    private fun initEncoder() {
        mediaEncoder = MediaEncoder()
        mediaEncoder?.setIMediaCodecCallback(iMediaCodecCallback)
    }

    fun isPush(): Boolean {
        return isPush
    }

    /**
     * 开始推流
     */
    fun startRtmpPush() {
        if (isPush) {
            return
        }
        handler?.post {
            val ret: Long? = rtmpJni?.initRtmp(rtmpUrl)
            ret?.let {
                mediaEncoder?.let {
                    it.initVideoEncoder(width, height, fps)
                    it.initAudioEncoder(
                            AudioRecorder.getInstance().sampleRateInHz,
                            AudioRecorder.getInstance().audioFormat,
                            2)
                    it.start(yuvConvertor!!)
                    isPush = true
                }
            }
        }
    }

    /**
     * 添加相机预览帧数据
     */
    fun addFrameData(byteArray: ByteArray) {
        mediaEncoder?.putVideoData(byteArray)
    }

    /**
     * 停止推流
     */
    fun stopRtmp() {
        isPush =false
        handler?.post {
            mediaEncoder?.stop()
            rtmpJni?.stopRtmp()
        }
    }
}