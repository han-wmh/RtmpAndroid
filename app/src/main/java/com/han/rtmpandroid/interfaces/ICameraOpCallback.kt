package com.han.rtmpandroid.interfaces

/**
 * 相机操作回调
 */
interface ICameraOpCallback {
    /**
     * 相机打开
     */
    fun onCameraOpen()

    /**
     * 开始预览
     */
    fun onCameraPreview(width: Int, height: Int, fps: Int)

    /**
     * 帧数据回调
     */
    fun onFrameData(frameData:ByteArray)

    /**
     * 关闭相机
     */
    fun onCameraClose()
}