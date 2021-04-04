package com.han.rtmpandroid.utils

class YuvConvertor {

    init {
        System.loadLibrary("rtmpJni")
    }

    fun yv12ToI420(pYv12: ByteArray?, pI420: ByteArray?, width: Int, height: Int) {
        Yv12ToI420(pYv12, pI420, width, height)
    }

    fun i420ToYv12(pI420: ByteArray?, pYv12: ByteArray?, width: Int, height: Int) {
        I420ToYv12(pI420, pYv12, width, height)
    }

    fun nv21ToI420(pNv21: ByteArray?, pI420: ByteArray?, width: Int, height: Int) {
        Nv21ToI420(pNv21, pI420, width, height)
    }

    fun i420ToNv21(pI420: ByteArray?, pNv21: ByteArray?, width: Int, height: Int) {
        I420ToNv21(pI420, pNv21, width, height)
    }

    fun nv21ToYv12(pNv21: ByteArray?, pYv12: ByteArray?, width: Int, height: Int) {
        Nv21ToYv12(pNv21, pYv12, width, height)
    }

    fun yV12ToNv21(pYv12: ByteArray?, pNv21: ByteArray?, width: Int, height: Int) {
        YV12ToNv21(pYv12, pNv21, width, height)
    }

    fun nv21ToNv12(pNv21: ByteArray?, pNv12: ByteArray?, width: Int, height: Int) {
        Nv21ToNv12(pNv21, pNv12, width, height)
    }

    fun nv12ToNv21(pNv12: ByteArray?, pNv21: ByteArray?, width: Int, height: Int) {
        Nv12ToNv21(pNv12, pNv21, width, height)
    }

    fun cutCommonYuv(yuvType: Int, startX: Int, startY: Int, srcYuv: ByteArray?, srcW: Int, srcH: Int, tarYuv: ByteArray?, cutW: Int, cutH: Int) {
        CutCommonYuv(yuvType, startX, startY, srcYuv, srcW, srcH, tarYuv, cutW, cutH)
    }

    fun getSpecYuvBuffer(yuvType: Int, dstBuf: ByteArray?, srcYuv: ByteArray?, srcW: Int, srcH: Int, dirty_Y: Int, dirty_UV: Int) {
        GetSpecYuvBuffer(yuvType, dstBuf, srcYuv, srcW, srcH, dirty_Y, dirty_UV)
    }

    fun yuvAddWaterMark(yuvType: Int, startX: Int, startY: Int, waterMarkData: ByteArray?,
                        waterMarkW: Int, waterMarkH: Int, yuvData: ByteArray?, yuvW: Int, yuvH: Int) {
        YuvAddWaterMark(yuvType, startX, startY, waterMarkData, waterMarkW, waterMarkH, yuvData, yuvW, yuvH)
    }

    fun nv21ClockWiseRotate90(pNv21: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?) {
        Nv21ClockWiseRotate90(pNv21, srcWidth, srcHeight, outData, outWidth, outHeight)
    }

    fun nv12ClockWiseRotate90(pNv12: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?) {
        Nv12ClockWiseRotate90(pNv12, srcWidth, srcHeight, outData, outWidth, outHeight)
    }
    fun nv21ClockWiseRotate180(pNv21: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?) {
        Nv21ClockWiseRotate180(pNv21, srcWidth, srcHeight, outData, outWidth, outHeight)
    }
    fun nv21ClockWiseRotate270(pNv21: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?) {
        Nv21ClockWiseRotate270(pNv21, srcWidth, srcHeight, outData, outWidth, outHeight)
    }

    //I420(YUV420P)图像顺时针旋转90度
    fun i420ClockWiseRotate90(pI420: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?) {
        I420ClockWiseRotate90(pI420, srcWidth, srcHeight, outData, outWidth, outHeight)
    }

    //YV12图像顺时针旋转90度
    fun yv12ClockWiseRotate90(pYv12: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?) {
        Yv12ClockWiseRotate90(pYv12, srcWidth, srcHeight, outData, outWidth, outHeight)
    }


    private external fun Yv12ToI420(pYv12: ByteArray?, pI420: ByteArray?, width: Int, height: Int)
    private external fun I420ToYv12(pI420: ByteArray?, pYv12: ByteArray?, width: Int, height: Int)
    private external fun Nv21ToI420(pNv21: ByteArray?, pI420: ByteArray?, width: Int, height: Int)
    private external fun I420ToNv21(pI420: ByteArray?, pNv21: ByteArray?, width: Int, height: Int)
    private external fun Nv21ToYv12(pNv21: ByteArray?, pYv12: ByteArray?, width: Int, height: Int)
    private external fun YV12ToNv21(pYv12: ByteArray?, pNv21: ByteArray?, width: Int, height: Int)
    private external fun Nv21ToNv12(pNv21: ByteArray?, pNv12: ByteArray?, width: Int, height: Int)
    private external fun Nv12ToNv21(pNv12: ByteArray?, pNv21: ByteArray?, width: Int, height: Int)
    private external fun CutCommonYuv(yuvType: Int, startX: Int, startY: Int, srcYuv: ByteArray?, srcW: Int, srcH: Int, tarYuv: ByteArray?, cutW: Int, cutH: Int)
    private external fun GetSpecYuvBuffer(yuvType: Int, dstBuf: ByteArray?, srcYuv: ByteArray?, srcW: Int, srcH: Int, dirty_Y: Int, dirty_UV: Int)
    private external fun YuvAddWaterMark(yuvType: Int, startX: Int, startY: Int, waterMarkData: ByteArray?,
                                         waterMarkW: Int, waterMarkH: Int, yuvData: ByteArray?, yuvW: Int, yuvH: Int)

    private external fun Nv21ClockWiseRotate90(pNv21: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?)
    private external fun Nv12ClockWiseRotate90(pNv12: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?)
    private external fun Nv21ClockWiseRotate180(pNv21: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?)
    private external fun Nv21ClockWiseRotate270(pNv21: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?)

    //I420(YUV420P)图像顺时针旋转90度
    private external fun I420ClockWiseRotate90(pI420: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?)

    //YV12图像顺时针旋转90度
    private external fun Yv12ClockWiseRotate90(pYv12: ByteArray?, srcWidth: Int, srcHeight: Int, outData: ByteArray?, outWidth: IntArray?, outHeight: IntArray?)
}