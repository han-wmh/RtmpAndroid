package com.han.rtmpandroid.encoder

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.util.Log
import com.han.rtmpandroid.interfaces.IMediaCodecCallback
import com.han.rtmpandroid.utils.AndroidLogger
import com.han.rtmpandroid.utils.YuvConvertor
import java.nio.ByteBuffer
import java.util.concurrent.LinkedBlockingQueue
import kotlin.experimental.and

class MediaEncoder() {
    private val TAG: String = "RtmpAndroid_MediaEncoder"
    private val TIMEOUT_USEC = 10000L
    private val videoMimeType: String = MediaFormat.MIMETYPE_VIDEO_AVC
    /**
     * yuv格式转换器
     */
    private var yuvConvertor: YuvConvertor? = null
    /**
     * 视频编码
     */
    private var videoCodec: MediaCodec? = null
    /**
     * 视频格式
     */
    private var videoFormat: MediaFormat? = null
    /**
     * 视频缓存
     */
    private var videoBufferInfo: MediaCodec.BufferInfo? = null
    /**
     * colorformat
     */
    private var colorFormat: Int = 0
    /**
     * 保存sps帧
     */
    private var spsNalu: ByteArray? = null
    /**
     * 保存pps帧
     */
    private var ppsNalu: ByteArray? = null
    /**
     * 视频编码线程是否开启
     */
    @Volatile
    private var isVideoEncoderStart: Boolean = false
    /**
     * 视频帧数据队列
     */
    private var videoQueu: LinkedBlockingQueue<ByteArray>? = null
    /**
     * yuv
     */
    private var yuvBuffer: ByteArray? = null
    /**
     * 旋转之后的yuv
     */
    private var rotateYuv: ByteArray? = null

    /**
     * 视频编码线程
     */
    private var videoEncodeThread: Thread? = null

    /**
     * 音频编码器
     */
    private var audioCodec: MediaCodec? = null
    /**
     * 音频编码缓存
     */
    private var audioBufferInfo: MediaCodec.BufferInfo? = null
    /**
     * audio codec info
     */
    private var audioCodecInfo: MediaCodecInfo? = null
    /**
     * 音频编码线程
     */
    private var audioEncodeThread: Thread? = null
    /**
     * 音频编码是否开启
     */
    @Volatile
    private var isAudioEncoderStart = false
    /**
     * 音频帧数据队列
     */
    private var audioQueue: LinkedBlockingQueue<ByteArray>? = null
    /**
     * 音频格式
     */
    private var audioFormat: MediaFormat? = null
    /**
     * audioMimeType
     */
    private val audioMimeType: String = MediaFormat.MIMETYPE_AUDIO_AAC
    /**
     * 时间戳
     */
    private var presentationTimeStamp: Long = 0
    /**
     * 编码回调
     */
    private var iMediaCodecCallback: IMediaCodecCallback? = null
    /**
     * 预览宽
     */
    private var width: Int = 0
    /**
     * 预览高
     */
    private var height: Int = 0

    /**
     * 初始化音频编码器
     */
    fun initAudioEncoder(sampleRate: Int, pcmFormat: Int, channelCount: Int) {
        audioBufferInfo = MediaCodec.BufferInfo()
        audioQueue = LinkedBlockingQueue()
        audioCodecInfo = selectCodec(audioMimeType)
        if (audioCodecInfo == null) {
            AndroidLogger.printStringD("无法找到 audio/mp4a-latm 类型")
            return
        }
        audioFormat = MediaFormat.createAudioFormat(audioMimeType, sampleRate, channelCount)
        audioFormat?.let {
            it.setInteger(MediaFormat.KEY_BIT_RATE, 96000)
            it.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
            it.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096 * 10)

            audioCodec = MediaCodec.createEncoderByType(audioMimeType)
            audioCodec?.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        }
    }

    /**
     * 初始化视频编码器
     */
    fun initVideoEncoder(width: Int, height: Int, fps: Int) {
        this.width = width
        this.height = height
        videoQueu = LinkedBlockingQueue()
        yuvBuffer = ByteArray(width * height * 3 / 2)
        rotateYuv = ByteArray(width * height * 3 / 2)
        videoBufferInfo = MediaCodec.BufferInfo()
        val vCodecInfo: MediaCodecInfo? = selectCodec(videoMimeType)
        if (vCodecInfo == null) {
            return
        }
        // 选择颜色格式
        val supportColorFormats: MutableList<Int> = selectColorFormat(videoMimeType, vCodecInfo)
        for (i in supportColorFormats.indices) {
            if (isRecognizedFormat(supportColorFormats.get(i))) {
                colorFormat = supportColorFormats.get(i)
                break
            }
        }
        if (colorFormat == 0) {
            AndroidLogger.printStringD(TAG, "颜色格式不正确")
            return
        }
        // 根据mime创建mediaformat
        videoFormat = MediaFormat.createVideoFormat(videoMimeType, height, width)
        // 一帧数据 width * height * 3 / 2个字节，换算比特 * 8 ，一秒出fps * 一帧bit大小
        val bitRate: Int = width * height * 3 / 2 * 8 * fps

        videoFormat?.let {
            //设置比特率,将编码比特率值设为bitrate
            it.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 4)
            //设置帧率,将编码帧率设为Camera实际帧率mFps
            it.setInteger(MediaFormat.KEY_FRAME_RATE, fps)
            //设置颜色格式
            it.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat)
            //设置关键帧的时间:30帧出一个I帧
            it.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        }
        if (videoCodec != null) {
            return
        }
        videoCodec = MediaCodec.createByCodecName(vCodecInfo.name)
    }

    /**
     * 设置类型
     */
    private fun selectCodec(mimeType: String): MediaCodecInfo? {
        val numCodecs = MediaCodecList.getCodecCount()
        for (i in 0 until numCodecs) {
            val codecInfo = MediaCodecList.getCodecInfoAt(i)
            if (!codecInfo.isEncoder) {
                continue
            }
            val types = codecInfo.supportedTypes
            for (j in types.indices) {
                if (types[j].equals(mimeType, ignoreCase = true)) {
                    return codecInfo
                }
            }
        }
        return null
    }

    /**
     * 选择颜色格式
     */
    private fun selectColorFormat(mimeType: String, codecInfo: MediaCodecInfo): MutableList<Int> {
        val supportColorFormat: MutableList<Int> = mutableListOf()
        val capabilities: MediaCodecInfo.CodecCapabilities = codecInfo.getCapabilitiesForType(mimeType)
        for (i in capabilities.colorFormats.indices) {
            val colorFormat = capabilities.colorFormats[i]
            supportColorFormat.add(colorFormat)
        }
        return supportColorFormat
    }

    /**
     * 是否识别格式
     */
    private fun isRecognizedFormat(colorFormat: Int): Boolean {
        return when (colorFormat) {
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar -> {
                //对应Camera预览格式I420(YV21/YUV420P)
                true
            }
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar -> {
                ////对应Camera预览格式YV12
                true
            }
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar -> {
                //对应Camera预览格式NV12
                true
            }
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar -> {
                //对应Camera预览格式NV21
                true
            }
            else -> false
        }
    }

    /**
     * 开始编码
     */
    fun start(yuvConvertor: YuvConvertor) {
        this.yuvConvertor = yuvConvertor
        AudioRecorder.getInstance().setOnRecordLisener(object : AudioRecorder.OnRecordLisener {
            override fun recordByte(audioData: ByteArray?, readSize: Int) {
                if (audioData != null) {
                    putAudioData(audioData)
                }
            }

        })
        presentationTimeStamp = 0L
        AudioRecorder.getInstance().startRecord()
        startVideoEncodec()
        startAudioEncodec()
    }

    /**
     * 停止编码
     */
    fun stop() {
        AudioRecorder.getInstance().stopRecord()
        AudioRecorder.getInstance().setOnRecordLisener(null)
        stopAudioEncodec()
        stopVideoEncodec()
    }

    /**
     * 开始音频编码
     */
    private fun startAudioEncodec() {
        if (audioCodec == null) {
            throw RuntimeException("音频编码器未初始化")
        }
        if (isAudioEncoderStart) {
            throw RuntimeException("音频编码线程正在工作，先停止")
        }
        audioEncodeThread = object : Thread() {
            override fun run() {
                isAudioEncoderStart = true
                presentationTimeStamp = System.currentTimeMillis() * 1000
                audioCodec?.start()
                AndroidLogger.printStringD("test_", "开始音频编码")
                while (isAudioEncoderStart && !interrupted()) {
                    try {
                        val data = audioQueue!!.take()
                        encodeAudioData(data)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        break
                    }
                }

                audioCodec?.let {
                    it.stop()
                    //释放音频编码器
                    it.release()
                }
                audioCodec = null
                audioQueue!!.clear()
            }
        }
        audioEncodeThread?.start()
    }

    /**
     * 停止音频编码
     */
    private fun stopAudioEncodec() {
        isAudioEncoderStart = false
    }

    /**
     * 开始视频编码
     */
    private fun startVideoEncodec() {
        if (videoCodec == null) {
            throw RuntimeException("视频编码器未初始化")
        }
        if (isVideoEncoderStart) {
            throw RuntimeException("视频编码线程正在工作，先停止")
        }
        videoEncodeThread = object : Thread() {
            override fun run() {
                isVideoEncoderStart = true
                presentationTimeStamp = System.currentTimeMillis() * 1000
                videoCodec?.let {
                    it.configure(videoFormat, null, null,
                            MediaCodec.CONFIGURE_FLAG_ENCODE)
                    it.start()
                    AndroidLogger.printStringD("test_", "开始视频编码")
                }
                while (isVideoEncoderStart && !interrupted()) {
                    try {
                        //待编码的数据
                        val data: ByteArray = videoQueu!!.take()
                        encodeVideoData(data)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        break
                    }
                }
                videoCodec?.let {
                    it.stop()
                    it.release()
                }
                videoCodec = null
                videoQueu!!.clear()
            }
        }
        videoEncodeThread?.start()
    }

    private fun stopVideoEncodec() {
        isVideoEncoderStart = false
    }

    /**
     * 预览帧数据入队
     */
    fun putVideoData(cameraFrame: ByteArray) {
        videoQueu?.put(cameraFrame)
    }

    /**
     * pcm帧数据入队
     */
    fun putAudioData(audioFrame: ByteArray) {
        audioQueue?.put(audioFrame)
    }

    /**
     * 编码pcm为aac
     */
    private fun encodeAudioData(input: ByteArray) {
        audioCodec?.let {
            //拿到输入缓冲区,用于传送数据进行编码
            val inputBuffers: Array<ByteBuffer> = it.getInputBuffers()
            val inputBufferIndex: Int = it.dequeueInputBuffer(TIMEOUT_USEC)
            if (inputBufferIndex >= 0) {
                val inputBuffer: ByteBuffer = inputBuffers[inputBufferIndex]
                inputBuffer.clear()
                // 数据写入缓冲区
                inputBuffer.put(input)

                val pts = System.currentTimeMillis() * 1000 - presentationTimeStamp
                if (!isAudioEncoderStart) {
                    // 发送结束标志，结束编码
                    it.queueInputBuffer(inputBufferIndex, 0, input.size, pts, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                } else {
                    // 入队
                    it.queueInputBuffer(inputBufferIndex, 0, input.size, pts, 0)
                }
            }
            val outputBuffers: Array<ByteBuffer> = it.outputBuffers
            // pcm->aac
            var outputBufferIndex: Int = it.dequeueOutputBuffer(audioBufferInfo!!, TIMEOUT_USEC)
            while (outputBufferIndex >= 0) {
                val outputBuffer: ByteBuffer = outputBuffers[outputBufferIndex]
                if (outputBuffer == null) {
                    throw java.lang.RuntimeException("音频编码输出缓存为空，无法编码成aac")
                }
                if (audioBufferInfo!!.size != 0) {
                    onEncodeAudioFrame(outputBuffer, audioBufferInfo!!)
                }
                it.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = it.dequeueOutputBuffer(audioBufferInfo!!, TIMEOUT_USEC)
                if (audioBufferInfo!!.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    isAudioEncoderStart = false
                    audioEncodeThread?.interrupt()
                    return
                }
            }
        }
    }

    /**
     * 编码nv21为h264
     */
    private fun encodeVideoData(input: ByteArray) {
        // input为camera nv21格式
        var outWidth: IntArray = IntArray(1)
        var outHeight: IntArray = IntArray(1)
        if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar) {
            // nv21->nv12
            yuvConvertor?.nv21ToNv12(input, yuvBuffer, width, height)
            yuvConvertor?.nv12ClockWiseRotate90(yuvBuffer, width, height, rotateYuv, outWidth, outHeight)
        } else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
            // nv21->i420(yuv420p)
            yuvConvertor?.nv21ToI420(input, yuvBuffer, width, height)
            yuvConvertor?.i420ClockWiseRotate90(yuvBuffer, width, height, rotateYuv, outWidth, outHeight)
        } else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar) {
            System.arraycopy(input, 0, yuvBuffer, 0, width * height * 3 / 2)
            yuvConvertor?.nv21ClockWiseRotate90(yuvBuffer, width, height, rotateYuv, outWidth, outHeight)
        } else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar) {
            // nv21->yv12
            yuvConvertor?.nv21ToYv12(input, yuvBuffer, width, height)
            yuvConvertor?.yv12ClockWiseRotate90(yuvBuffer, width, height, rotateYuv, outWidth, outHeight)
        }

        videoCodec?.let {
            val inputBuffers: Array<ByteBuffer> = it.inputBuffers
            val inputBufferIndex: Int = it.dequeueInputBuffer(TIMEOUT_USEC)
            if (inputBufferIndex >= 0) {
                val inputBuffer: ByteBuffer = inputBuffers[inputBufferIndex]
                inputBuffer.clear()
                // 写入缓冲区
                inputBuffer.put(rotateYuv)
                val pts: Long = System.currentTimeMillis() * 1000 - presentationTimeStamp
                if (!isVideoEncoderStart) {
                    it.queueInputBuffer(inputBufferIndex, 0, rotateYuv!!.size, pts, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                } else {
                    it.queueInputBuffer(inputBufferIndex, 0, rotateYuv!!.size, pts, 0)
                }
            }
            val outputBuffers: Array<ByteBuffer> = it.outputBuffers
            var outputBufferIndex: Int = it.dequeueOutputBuffer(videoBufferInfo!!, TIMEOUT_USEC)
            var keyframe = false
//            if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) { // sps pps 更新则会执行下方处理
//                Log.d("test_", "INFO_OUTPUT_FORMAT_CHANGED")
//                println("test_" + "INFO_OUTPUT_FORMAT_CHANGED")
//                // sps
//                val spsb: ByteBuffer = it.getOutputFormat().getByteBuffer("csd-0")
//                spsNalu = ByteArray(spsb.remaining())
//                spsb[spsNalu, 0, spsNalu!!.size]
//                // pps
//                val ppsb: ByteBuffer = it.getOutputFormat().getByteBuffer("csd-1")
//                ppsNalu = ByteArray(ppsb.remaining())
//                ppsb[ppsNalu, 0, ppsNalu!!.size]
//                AndroidLogger.printStringD(TAG, "sps:" + byteToHex(spsNalu!!))
//                AndroidLogger.printStringD(TAG, "pps:" + byteToHex(ppsNalu!!))
//            } else {
//                while (outputBufferIndex >= 0) {
//                    val outputBuffer: ByteBuffer = it.getOutputBuffers().get(outputBufferIndex)
//                    outputBuffer.position(videoBufferInfo!!.offset)
//                    outputBuffer.limit(videoBufferInfo!!.offset + videoBufferInfo!!.size)
//                    val data = ByteArray(outputBuffer.remaining())
//                    outputBuffer[data, 0, data.size]
//                    // I帧0x0000000165 P帧0x0000000141 B帧
//                    // I帧
//                    if (videoBufferInfo!!.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
//                        keyframe = true
//                        if (iMediaCodecCallback != null) {
//                            iMediaCodecCallback!!.onSpsPPS(spsNalu!!, ppsNalu!!, presentationTimeStamp)
//                        }
//                    }
//                    if (iMediaCodecCallback != null) {
//                        AndroidLogger.printStringD(TAG, "h264:" + byteToHex(data))
//                        iMediaCodecCallback!!.onVideoFrame(data, keyframe, presentationTimeStamp)
//                    }
//                    it.releaseOutputBuffer(outputBufferIndex, false)
//                    outputBufferIndex = it.dequeueOutputBuffer(videoBufferInfo, 0)
//                }
//            }

            while (outputBufferIndex >= 0) {
                AndroidLogger.printStringD(TAG, "=========Video===outputBufferIndex: $outputBufferIndex")
                //数据已经编码成H264格式
                //outputBuffer保存的就是H264数据
                val outputBuffer = outputBuffers[outputBufferIndex]
                        ?: throw java.lang.RuntimeException("encoderOutputBuffer " + outputBufferIndex +
                                " was null")
                if (videoBufferInfo!!.size != 0) {
                    val outData = ByteArray(videoBufferInfo!!.size)
                    outputBuffer[outData]
                    AndroidLogger.printStringD(TAG + "video", byteToHex(outData))
                    // 0 0 0 1 103 66 -128 41 -38 15 10 104 6 -48 -95 53  0 0 0 1 104 -50 6 -30
                    //sps序列参数集，即0x67 pps图像参数集，即0x68，MediaCodec编码输出的头两个NALU即为sps和pps
                    //并且在h264码流的开始两帧即为sps和pps，在这里MediaCodec将sps和pps作为一个buffer输出。
                    if (spsNalu != null && ppsNalu != null) {
                        val naluType: Byte = outData[4] and 0x1f
                        AndroidLogger.printStringD(TAG, "======AVC Frame===data: " + outData[0] + " ," + outData[1] + " ," + outData[2] + " ," + outData[3]
                                + "  ," + outData[4] + "  len: " + outData.size + "    AVC帧类型: " + naluType + "   时间戳: " + videoBufferInfo!!.presentationTimeUs / 1000)
                        keyframe = outData[4] == 0x65.toByte()
                        if (null != iMediaCodecCallback && isVideoEncoderStart) {
                            iMediaCodecCallback?.onVideoFrame(outData, keyframe, videoBufferInfo!!.presentationTimeUs / 1000)
                        }
                    } else { //保存pps sps 即h264码流开始两帧，保存起来后面用
                        val spsPpsBuffer = ByteBuffer.wrap(outData)
                        //通过上面的打印看到sps帧长度为输出buffer的前面12字节
                        if (spsPpsBuffer.int == 0x00000001 && spsPpsBuffer[4].toInt() == 0x67) {
                            //8为两个startCode的长度，一个startCode为0x00000001
                            spsNalu = ByteArray(outData.size - 4 - 8)
                            //通过上面的打印看到pps帧长度为输出buffer的最后4字节
                            ppsNalu = ByteArray(4)
                            //保存sps帧
                            spsPpsBuffer[spsNalu, 0, spsNalu!!.size]
                            //跳过startCode 0x00000001
                            spsPpsBuffer.int
                            //保存pps帧
                            spsPpsBuffer[ppsNalu, 0, ppsNalu!!.size]
                            if (null != iMediaCodecCallback && isVideoEncoderStart) {
                                AndroidLogger.printStringD(TAG, "==========SPS PPS帧=====时间戳: " + videoBufferInfo!!.presentationTimeUs / 1000)
                                iMediaCodecCallback?.onSpsPPS(spsNalu!!, ppsNalu!!, videoBufferInfo!!.presentationTimeUs / 1000)
                            }
                        }
                    }
                }
                //释放资源
                videoCodec!!.releaseOutputBuffer(outputBufferIndex, false)
                //拿到输出缓冲区的索引
                outputBufferIndex = videoCodec!!.dequeueOutputBuffer(videoBufferInfo!!, TIMEOUT_USEC)
                //编码结束的标志
                if (videoBufferInfo!!.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    AndroidLogger.printStringD(TAG, "========4==Recv Video Encoder===BUFFER_FLAG_END_OF_STREAM=====")
                    isVideoEncoderStart = false
                    videoEncodeThread?.interrupt()
                    return
                }
            }
        }

    }

    /**
     * 发送音频数据
     */
    private fun onEncodeAudioFrame(buffer: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
        val byteArray: ByteArray = ByteArray(bufferInfo.size)
        buffer.get(byteArray)
        if (bufferInfo.size == 2) {
            iMediaCodecCallback?.onAudioSpec(byteArray)
        } else {
            iMediaCodecCallback?.onAudioFrame(byteArray, bufferInfo.presentationTimeUs / 1000)
        }
    }

    /**
     * 设置回调
     */
    fun setIMediaCodecCallback(iMediaCodecCallback: IMediaCodecCallback) {
        this.iMediaCodecCallback = iMediaCodecCallback
    }

    /**
     * byte转16进制字符串
     */
    fun byteToHex(bytes: ByteArray): String? {
        val stringBuffer = StringBuffer()
        for (i in bytes.indices) {
            val hex = Integer.toHexString(bytes[i].toInt())
            if (hex.length == 1) {
                stringBuffer.append("0$hex")
            } else {
                stringBuffer.append(hex)
            }
            if (i > 20) {
                break
            }
        }
        return stringBuffer.toString()
    }
}