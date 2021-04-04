package com.han.rtmpandroid

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.han.rtmpandroid.encoder.MediaEncoder
import com.han.rtmpandroid.interfaces.ICameraOpCallback
import com.han.rtmpandroid.utils.AndroidLogger
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val PERMISSION_REQUEST_CODE: Int = 10010
    /**
     * 相机预览
     */
    private var surfaceView: SurfaceView? = null
    /**
     * 开始推流
     */
    private var btnStartPush: Button? = null
    /**
     * 相机
     */
    private var cameraController: CameraController? = null
    /**
     * holder
     */
    private var surfaceHolder: SurfaceHolder? = null
    /**
     * 是否有权限
     */
    private var isHasPermission: Boolean? = null
    private var mediaRtmpPush: MediaRtmpPush? = null
    private var width: Int = 0
    private var height: Int = 0
    private var fps: Int = 0
    private val perms: Array<String> = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA)

    /**
     * surfaceHolder.callback
     */
    private val surfaceHolderCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            AndroidLogger.printStringD("RtmpAndroid_MainActivity", "surfaceHolder create 是否主线程：${Looper.myLooper() == Looper.getMainLooper()}")
            if (cameraController == null) {
                cameraController = surfaceHolder?.let { CameraController(this@MainActivity, it, "CameraThread") }
                cameraController?.setICameraOpCallback(iCameraOpCallback)
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            AndroidLogger.printStringD("RtmpAndroid_MainActivity", "surfaceHolder change 是否主线程：${Looper.myLooper() == Looper.getMainLooper()}")
            cameraController?.openCamera()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            AndroidLogger.printStringD("RtmpAndroid_MainActivity", "surfaceHolder destroy 是否主线程：${Looper.myLooper() == Looper.getMainLooper()}")
            cameraController?.closeCamera()
        }
    }

    /**
     * 相机操作接口回调
     */
    private val iCameraOpCallback: ICameraOpCallback = object : ICameraOpCallback {
        override fun onCameraOpen() {
            cameraController?.startPreview()
        }

        override fun onCameraPreview(width: Int, height: Int, fps: Int) {
            if (mediaRtmpPush == null) {
                mediaRtmpPush = MediaRtmpPush("rtmp://192.168.0.105/myapp/mystream", width, height, fps)
            }
        }

        override fun onFrameData(frameData: ByteArray) {
            mediaRtmpPush?.addFrameData(frameData)
        }

        override fun onCameraClose() {
        }
    }

    /**
     * 点击事件
     */
    private val onclickListener: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.btn_start -> {
                    if (mediaRtmpPush != null) {
                        if (mediaRtmpPush!!.isPush()) {
                            mediaRtmpPush!!.stopRtmp()
                            btnStartPush?.setText("开始推流")
                            AndroidLogger.printStringD("RtmpAndroid", "stop")
                            return
                        }
                    }
                    mediaRtmpPush?.startRtmpPush()
                    btnStartPush?.setText("停止推流")
                    AndroidLogger.printStringD("RtmpAndroid", "start")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidLogger.init(true)
        btnStartPush = findViewById(R.id.btn_start)
        surfaceView = findViewById(R.id.camera_preview)
        surfaceHolder = surfaceView!!.holder
        surfaceHolder?.addCallback(surfaceHolderCallback)
        btnStartPush!!.setOnClickListener(onclickListener)

        checkPermission();
    }

    /**
     * 检查权限
     */
    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(this, *perms)) {
                afterPermissionGranted();
            } else {
                EasyPermissions.requestPermissions(this, "App需要以下权限，否则不能正常使用", PERMISSION_REQUEST_CODE, *perms);
            }
        }
    }

    /**
     * 获取到权限
     */
    fun afterPermissionGranted() {
        isHasPermission = true
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //如果用户点击永远禁止，这个时候就需要跳到系统设置页面去手动打开了
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        afterPermissionGranted()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, "App需要以下权限，否则不能正常使用", PERMISSION_REQUEST_CODE, *perms);
        } else {
            afterPermissionGranted()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraController?.onRelease()
    }
}