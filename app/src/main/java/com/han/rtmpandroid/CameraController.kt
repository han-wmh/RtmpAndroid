package com.han.rtmpandroid

import android.app.Activity
import android.graphics.ImageFormat
import android.hardware.Camera
import android.hardware.Camera.AutoFocusCallback
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Surface
import android.view.SurfaceHolder
import com.han.rtmpandroid.interfaces.ICameraOpCallback
import com.han.rtmpandroid.utils.AndroidLogger

class CameraController(val activity: Activity, val surfaceHolder: SurfaceHolder, threadName: String?) {
    private val TAG: String = "RtmpAndroid_CameraController"
    /**
     * camera
     */
    private var camera: Camera? = null
    /**
     * 是否开始预览
     */
    private var isStartPreview: Boolean = false
    /**
     * 相机参数
     */
    private var cameraParameters: Camera.Parameters? = null
    /**
     * 预览大小
     */
    private var previewSize: Camera.Size? = null
    /**
     * 帧率
     */
    private var frameRate: Int? = null
    /**
     * 预览数据缓存
     */
    private var callbackBuffer: ByteArray? = null
    /**
     * preview callback
     */
    private var previewCallback: CameraPreviewCallback? = null
    /**
     * 相机操作回调接口
     */
    private var iCameraOpCallback: ICameraOpCallback? = null
    /**
     * handler
     */
    private var handler: Handler? = null
    /**
     * handlerThread
     */
    private var handlerThread: HandlerThread? = null

    init {
        handlerThread = HandlerThread(threadName)
        handlerThread!!.start()
        handler = Handler(handlerThread!!.looper)
    }

    /**
     * 打开相机
     */
    fun openCamera() {
        handler?.post {
            AndroidLogger.printStringD(TAG, "isMainThread:${Looper.myLooper() == Looper.getMainLooper()}")
            if (camera != null) {
                AndroidLogger.printStringD(TAG, "camera已初始化，无需重复初始化")
                iCameraOpCallback?.onCameraOpen()
                return@post
            }
            if (camera == null) {
                camera = Camera.open();
            }
            if (camera == null) {
                throw RuntimeException("无法打开相机")
            }
            iCameraOpCallback?.onCameraOpen()
        }
    }

    /**
     * 开始预览
     */
    fun startPreview() {
        handler?.post {
            AndroidLogger.printStringD(TAG, "isMainThread:${Looper.myLooper() == Looper.getMainLooper()}")
            rotateCamera(activity, Camera.CameraInfo.CAMERA_FACING_BACK)
            setCameraParameter(surfaceHolder)
            camera?.setPreviewDisplay(surfaceHolder)
            camera?.startPreview()
            isStartPreview = true
            camera?.autoFocus(AutoFocusCallback { success, camera ->
                AndroidLogger.printStringD(TAG, "auto focus:$success")
            })
            iCameraOpCallback?.onCameraPreview(previewSize!!.width, previewSize!!.height, frameRate!!)
        }
    }

    /**
     * 关闭相机
     */
    fun closeCamera() {
        handler?.post {
            AndroidLogger.printStringD(TAG, "isMainThread:${Looper.myLooper() == Looper.getMainLooper()}")
            camera?.let {
                it.setPreviewCallbackWithBuffer(null)
                previewCallback = null
                if (isStartPreview) {
                    it.stopPreview()
                    isStartPreview = false
                }
                it.release()
            }
            camera = null
            iCameraOpCallback?.onCameraClose()
        }
    }

    /**
     * 旋转摄像头
     */
    private fun rotateCamera(activity: Activity, cameraId: Int) {
        val cameraInfo: Camera.CameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, cameraInfo)
        val rotation: Int = activity.windowManager.defaultDisplay.rotation
        var degree: Int = 0;
        when (rotation) {
            Surface.ROTATION_0 -> {
                degree = 0
            }
            Surface.ROTATION_90 -> {
                degree = 90
            }
            Surface.ROTATION_180 -> {
                degree = 180
            }
            Surface.ROTATION_270 -> {
                degree = 270
            }
        }
        var rotateValue = 0
        // 前置
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotateValue = (cameraInfo.orientation + degree) % 360
            rotateValue = (360 - rotateValue) % 360
        } else {
            // 后置
            rotateValue = (cameraInfo.orientation - degree + 360) % 360
        }
        camera?.setDisplayOrientation(rotateValue)
    }

    /**
     * 相机配置信息
     */
    private fun setCameraParameter(surfaceHolder: SurfaceHolder) {
        if (isStartPreview || camera == null) {
            AndroidLogger.printStringD(TAG, "is previewing, can not set camera parameter");
            return
        }
        cameraParameters = camera?.parameters
        val supportFormat: MutableList<Int>? = cameraParameters?.supportedPreviewFormats
        if (supportFormat != null) {
            var hasNV21: Boolean = false;
            for (format in supportFormat) {
                if (ImageFormat.NV21.compareTo(format) == 0) {
                    hasNV21 = true
                }
                AndroidLogger.printStringD(format.toString())
            }
            if (hasNV21) {
                cameraParameters?.previewFormat = ImageFormat.NV21
            }
        }
        // 预览分辨率
        val supportPrevieSize: MutableList<Camera.Size>? = cameraParameters?.supportedPreviewSizes;
        if (supportPrevieSize != null) {
            supportPrevieSize.sortBy { it.width }
            var displayMetrics: DisplayMetrics = activity.resources.displayMetrics;
            for (size: Camera.Size in supportPrevieSize) {
                if (size.width >= displayMetrics.heightPixels && size.height >= displayMetrics.widthPixels) {
                    if ((size.width * 1.0f / size.height) == (displayMetrics.heightPixels * 1.0f / displayMetrics.widthPixels)) {
                        previewSize = size
                        AndroidLogger.printStringD(TAG, "预览大小：宽->${size.width}  高->${size.height}")
                        break
                    }
                }
            }
            if (cameraParameters != null && previewSize != null) {
                cameraParameters!!.setPreviewSize(previewSize!!.width, previewSize!!.height)
            } else {
                AndroidLogger.printStringD(TAG, "camearParameters是否为空： ${cameraParameters == null}，previewSize是否为空：${previewSize == null}")
            }
        }
        // fps range
        var minFps: Int = 0
        var maxFps: Int = 0
        var supportPreviewFpsRange: MutableList<IntArray>? = cameraParameters?.supportedPreviewFpsRange;
        if (supportPreviewFpsRange != null) {
            for (fpsRange: IntArray in supportPreviewFpsRange) {
                if (minFps <= fpsRange.get(Camera.Parameters.PREVIEW_FPS_MIN_INDEX) && maxFps <= fpsRange.get(Camera.Parameters.PREVIEW_FPS_MAX_INDEX)) {
                    minFps = fpsRange.get(Camera.Parameters.PREVIEW_FPS_MIN_INDEX)
                    maxFps = fpsRange.get(Camera.Parameters.PREVIEW_FPS_MAX_INDEX)
                }
            }
        }
        // 预览分辨率
        cameraParameters?.setPreviewFpsRange(minFps, maxFps)
        frameRate = maxFps / 1000
//        surfaceHolder.setFixedSize(previewSize!!.width, previewSize!!.height)
        previewSize?.let {
            callbackBuffer = ByteArray(it.width * it.height * 3 / 2)
            previewCallback = CameraPreviewCallback(callbackBuffer!!, iCameraOpCallback!!)
            camera?.addCallbackBuffer(callbackBuffer)
            camera?.setPreviewCallbackWithBuffer(previewCallback)
        }
        val focusModeList: MutableList<String> = cameraParameters?.supportedFocusModes as MutableList<String>
        for (focusMode:String in focusModeList) {
            AndroidLogger.printStringD(TAG, "focus mode:${focusMode}")
            if (focusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                cameraParameters?.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
            } else if (focusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                cameraParameters?.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
            } else if (focusMode.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                cameraParameters?.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO)
            }
        }
        camera?.parameters = cameraParameters
    }

    /**
     * 设置相机操作回调接口
     */
    fun setICameraOpCallback(callback: ICameraOpCallback) {
        iCameraOpCallback = callback
    }

    /**
     * 释放
     */
    fun onRelease() {
        closeCamera()
        handler?.removeCallbacksAndMessages(null)
        handlerThread?.quit()
        handlerThread = null
    }

    /**
     * 嵌套类（静态内部类）
     */
    class CameraPreviewCallback(val byteArray: ByteArray, val iCameraOpCallback: ICameraOpCallback) : Camera.PreviewCallback {
        override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
            // 预览数据给mediacodec h264编码
            if (data != null) {
                camera?.addCallbackBuffer(data)
                iCameraOpCallback?.onFrameData(data)
            } else {
                camera?.addCallbackBuffer(byteArray)
            }
        }
    }
}