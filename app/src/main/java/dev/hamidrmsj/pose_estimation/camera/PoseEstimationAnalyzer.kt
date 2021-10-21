package dev.hamidrmsj.pose_estimation.camera

import android.graphics.*
import android.view.SurfaceView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import dev.hamidrmsj.pose_estimation.data.Person
import dev.hamidrmsj.pose_estimation.ml.PoseDetector
import java.io.ByteArrayOutputStream


class PoseEstimationAnalyzer(private val poseDetector: PoseDetector,
                             private val surfaceView: SurfaceView) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {

        val rotatedBitmap = image.toRotatedBitmap()

        processImage(rotatedBitmap, poseDetector, surfaceView)

        image.close()
    }

}

// This function processes camera input images to estimate human poses and draw lines on them
private fun processImage(bitmap: Bitmap, detector: PoseDetector, surfaceView: SurfaceView) {

    val person: Person = detector.estimateSinglePose(bitmap)

    visualize(person, bitmap, surfaceView)
}

private const val MIN_CONFIDENCE = .3f

// This Function draw lines based on human pose
private fun visualize(person: Person, bitmap: Bitmap, surfaceView: SurfaceView) {
    var outputBitmap = bitmap

    if (person.score > MIN_CONFIDENCE) {
        outputBitmap = VisualizationUtils.drawBodyKeypoints(bitmap, person)
    }

    val holder = surfaceView.holder
    val surfaceCanvas = holder.lockCanvas()
    surfaceCanvas?.let { canvas ->
        val screenWidth: Int
        val screenHeight: Int
        val left: Int
        val top: Int

        if (canvas.height > canvas.width) {
            val ratio = outputBitmap.height.toFloat() / outputBitmap.width
            screenWidth = canvas.width
            left = 0
            screenHeight = (canvas.width * ratio).toInt()
            top = (canvas.height - screenHeight) / 2
        } else {
            val ratio = outputBitmap.width.toFloat() / outputBitmap.height
            screenHeight = canvas.height
            top = 0
            screenWidth = (canvas.height * ratio).toInt()
            left = (canvas.width - screenWidth) / 2
        }
        val right: Int = left + screenWidth
        val bottom: Int = top + screenHeight

        canvas.drawBitmap(
            outputBitmap, Rect(0, 0, outputBitmap.width, outputBitmap.height),
            Rect(left, top, right, bottom), null
        )
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }
}


// This function first convert ImageProxy to Bitmap and then Rotate it and return Rotated bitmap
fun ImageProxy.toRotatedBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val vuBuffer = planes[2].buffer // VU

    val ySize = yBuffer.remaining()
    val vuSize = vuBuffer.remaining()

    val nv21 = ByteArray(ySize + vuSize)

    yBuffer.get(nv21, 0, ySize)
    vuBuffer.get(nv21, ySize, vuSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
    val imageBytes = out.toByteArray()
    val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    val rotateMatrix = Matrix()
    rotateMatrix.postRotate(90.0f)

    return Bitmap.createBitmap(
        imageBitmap, 0, 0, this.width, this.height,
        rotateMatrix, false
    )

}