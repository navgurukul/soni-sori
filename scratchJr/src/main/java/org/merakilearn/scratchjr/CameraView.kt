package org.merakilearn.scratchjr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Matrix
import android.graphics.RectF
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.PictureCallback
import android.hardware.SensorManager
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.view.OrientationEventListener
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ScrollView
import java.io.ByteArrayOutputStream
import java.io.IOException


/**
 * Creates a camera view that hovers at a particular location and has a mask.
 *
 * We use a ScrollView because the camera will rescale (squish) the preview to whatever size the
 * SurfaceView is and we want to keep the aspect ratio. So the ScrollView is of the desired
 * size and then we add a SurfaceView to it with the camera preview.
 *
 * @author markroth8
 */
class CameraView
    (
    context: Context,
    val rect: RectF,
    private val _scale: Float,
    private var _currentFacingFront: Boolean
) :
    ScrollView(context) {
    private var _cameraPreview: CameraPreviewView? = null
    private var _camera: Camera?
    private var _cameraId = 0
    private val _orientationListener: CameraOrientationListener?

    init {
        _camera = safeOpenCamera(_currentFacingFront)
        if (_camera != null) {
            _cameraPreview = CameraPreviewView(context)
            val previewSize = _camera!!.parameters.previewSize

            var previewWidth = rect.width()
            var previewHeight = previewSize.height * rect.width() / previewSize.width
            var centerScrollY = (previewHeight - rect.height()) / 2
            var centerScrollX = 0.0f
            if (previewHeight < rect.height()) {
                previewHeight = rect.height()
                previewWidth = previewSize.width * rect.height() / previewSize.height
                centerScrollX = (previewWidth - rect.width()) / 2
                centerScrollY = 0.0f
            }
            val linearLayout = LinearLayout(context)
            val layoutParams = ViewGroup.LayoutParams(previewWidth.toInt(), previewHeight.toInt())
            addView(linearLayout, layoutParams)

            linearLayout.addView(_cameraPreview, layoutParams)
            val cx = centerScrollX
            val cy = centerScrollY
            post { scrollTo(cx.toInt(), cy.toInt()) }
        }

        _orientationListener = CameraOrientationListener(context, SensorManager.SENSOR_DELAY_NORMAL)
        enableOrientationListener()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // Disabling scrolling in this ScrollView
        return false
    }

    fun captureStillImage(pictureCallback: PictureCallback?, failed: Runnable) {
        if (_camera != null) {
            val params = _camera!!.parameters
            params.setRotation(0)

            // Set picture size to the maximum supported resolution.
            val supportedPictureSizes = params.supportedPictureSizes
            var maxHeight = 0
            for (size in supportedPictureSizes) {
                if (size.height > maxHeight) {
                    params.setPictureSize(size.width, size.height)
                    maxHeight = size.height
                }
            }

            _camera!!.parameters = params
            _camera!!.takePicture(null, null, pictureCallback)
        } else {
            failed.run()
        }
    }

    private fun safeOpenCamera(facingFront: Boolean): Camera? {
        var result: Camera? = null
        try {
            _cameraId = findFirstCameraId(facingFront)
            if (_cameraId != -1) {
                result = Camera.open(_cameraId)
            }
        } catch (e: RuntimeException) {
            Log.e(LOG_TAG, "Failed to open camera", e)
        }
        return result
    }

    private fun findFirstCameraId(facingFront: Boolean): Int {
        var result = -1
        val facingTarget =
            if (facingFront) CameraInfo.CAMERA_FACING_FRONT else CameraInfo.CAMERA_FACING_BACK
        val count = Camera.getNumberOfCameras()
        val cameraInfo = CameraInfo()
        for (i in 0 until count) {
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == facingTarget) {
                result = i
                break
            }
        }
        if (result == -1) {
            Log.w(
                LOG_TAG,
                "No " + (if (facingFront) "front" else "back") + " -facing camera detected on this device."
            )
        }
        return result
    }

    fun setCameraFacing(facingFront: Boolean): Boolean {
        val result: Boolean
        if (_currentFacingFront != facingFront) {
            // switch cameras
            val id = findFirstCameraId(facingFront)
            if (id == -1) {
                result = false
            } else {
                result = true
                _currentFacingFront = facingFront
                if (_camera != null) {
                    disableOrientationListener()
                    _camera!!.release()
                    _camera = null
                }
                _camera = safeOpenCamera(facingFront)
                _cameraPreview!!.startPreview()
                enableOrientationListener()
            }
        } else {
            result = true
        }
        return result
    }

    /**
     * Take the given bitmap image from the camera and transform it to the correct
     * aspect ratio, size, and rotation.
     *
     * @return jpeg-encoded data of the transformed image.
     */
    fun getTransformedImage(originalImage: Bitmap, exifRotation: Int): ByteArray {
        val cropped = cropResizeAndRotate(originalImage, exifRotation)
        val bos = ByteArrayOutputStream()
        cropped.compress(CompressFormat.JPEG, 90, bos)
        try {
            bos.close()
        } catch (e: IOException) {
            // will not happen - this is a ByteArrayOutputStream
            Log.e(LOG_TAG, "IOException while closing byte array stream", e)
        }
        val jpegData = bos.toByteArray()
        return jpegData
    }

    /**
     * Crop and resize the given image to the dimensions of the rectangle for this camera view.
     *
     * If the image was front-facing, also mirror horizontally.
     */
    private fun cropResizeAndRotate(image: Bitmap, exifRotation: Int): Bitmap {
        val imageWidth = image.width
        val imageHeight = image.height
        val rectWidth = rect.width()
        val rectHeight = rect.height()

        val newHeight = rectWidth * imageHeight / imageWidth
        var scale = rectWidth / imageWidth
        var offsetX = 0
        var offsetY = ((newHeight - rectHeight) / 2 * imageHeight / newHeight).toInt()
        if (newHeight < rectHeight) {
            val newWidth = rectHeight * imageWidth / imageHeight
            scale = rectHeight / imageHeight
            offsetY = 0
            offsetX = ((newWidth - rectWidth) / 2 * imageWidth / newWidth).toInt()
        }

        val m = Matrix()
        // Adjust the image to undo rotation done by JPEG generator
        m.postRotate(-1.0f * exifRotation)
        if (_currentFacingFront) {
            // flip bitmap horizontally since front-facing camera is mirrored
            m.preScale(-1.0f, 1.0f)
        }
        val cameraInfo = CameraInfo()
        Camera.getCameraInfo(_cameraId, cameraInfo)
        val rotation = findDisplayRotation(context, cameraInfo.facing)
        if (rotation == 180) {
            m.preScale(-1.0f, -1.0f)
        }
        m.postScale(scale / _scale, scale / _scale)
        val newBitmap = Bitmap.createBitmap(
            image,
            offsetX,
            offsetY,
            imageWidth - offsetX * 2,
            imageHeight - offsetY * 2,
            m,
            true
        )
        return newBitmap
    }

    private fun enableOrientationListener() {
        synchronized(_orientationListener!!) {
            if (_orientationListener.canDetectOrientation()) {
                _orientationListener.setCameraInfo(_camera, _cameraId)
                _orientationListener.enable()
            }
        }
    }

    private fun disableOrientationListener() {
        synchronized(_orientationListener!!) {
            if (_orientationListener != null) {
                _orientationListener.disable()
                _orientationListener.clearCameraInfo()
            }
        }
    }

    private inner class CameraPreviewView
        (context: Context?) : SurfaceView(context), SurfaceHolder.Callback {
        private val _holder: SurfaceHolder = holder

        init {
            _holder.addCallback(this@CameraPreviewView)
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                if (_camera != null) {
                    _camera!!.setPreviewDisplay(holder)
                    _camera!!.startPreview()
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Error creating surface", e)
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            if (_holder.surface != null) {
                if (_camera != null) {
                    try {
                        _camera!!.stopPreview()
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "Error releasing camera", e)
                    }

                    startPreview()
                }
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            if (_camera != null) {
                Log.i(LOG_TAG, "Releasing camera")
                disableOrientationListener()
                _camera!!.release()
                _camera = null
            }
        }

        fun startPreview() {
            if (_camera != null) {
                try {
                    _camera!!.setPreviewDisplay(_holder)
                    val previewSize = _camera!!.parameters.previewSize
                    Log.i(
                        LOG_TAG,
                        "Preview size: " + previewSize.width + " x " + previewSize.height
                    )
                    _camera!!.startPreview()
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "Error in starting preview", e)
                } catch (e: RuntimeException) {
                    Log.e(LOG_TAG, "Error in starting preview", e)
                }
            }
        }
    }

    /**
     * An [OrientationEventListener] which updates the camera preview
     * based on the device's orientation.
     * @author khu
     */
    private class CameraOrientationListener : OrientationEventListener {
        private var _observedCamera: Camera? = null
        private var _observedCameraId = 0
        private var _display: Display
        private var _previousRotation = -1
        private var _context: Context

        constructor(context: Context) : super(context) {
            _context = context
            _display = findDisplay(context)
        }

        constructor(context: Context, rate: Int) : super(context, rate) {
            _context = context
            _display = findDisplay(context)
        }

        @Synchronized
        fun setCameraInfo(camera: Camera?, cameraId: Int) {
            _observedCamera = camera
            _observedCameraId = cameraId
        }

        @Synchronized
        fun clearCameraInfo() {
            _observedCamera = null
            _observedCameraId = -1
        }

        @Synchronized
        override fun onOrientationChanged(orientation: Int) {
            if (orientation == ORIENTATION_UNKNOWN || _observedCamera == null || _observedCameraId == -1) {
                return
            }

            val cameraInfo = CameraInfo()
            Camera.getCameraInfo(_observedCameraId, cameraInfo)
            val rotation = findDisplayRotation(_context, cameraInfo.facing)

            if (rotation != _previousRotation) {
                // Update the preview
                _observedCamera!!.setDisplayOrientation(rotation)
                _previousRotation = rotation
            }
        }
    }

    companion object {
        private const val LOG_TAG = "ScratchJr.CameraView"

        private fun findDisplay(context: Context): Display {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            return windowManager.defaultDisplay
        }

        private fun findDisplayRotation(context: Context, facing: Int): Int {
            val display = findDisplay(context)
            val r = display.rotation
            var rotation = if ((r == Surface.ROTATION_180)) 180 else 0
            if (facing == CameraInfo.CAMERA_FACING_FRONT) {
                rotation = (rotation + 360) % 360
            }
            return rotation
        }
    }
}