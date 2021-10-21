package dev.hamidrmsj.pose_estimation.ui

import android.graphics.*
import android.media.Image
import android.util.Log
import android.view.SurfaceView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import org.tensorflow.lite.examples.poseestimation.VisualizationUtils
import org.tensorflow.lite.examples.poseestimation.data.Person
import org.tensorflow.lite.examples.poseestimation.ml.PoseDetector
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class LuminosityAnalyzer(private val listener: (luma:Any) -> Any,
                         val poseDetector: PoseDetector,
                         private val surfaceView: SurfaceView) : ImageAnalysis.Analyzer {

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    override fun analyze(image: ImageProxy) {

//        val buffer = image.planes[0].buffer
//        val data = buffer.toByteArray()
//        val pixels = data.map { it.toInt() and 0xFF }
//        val luma = pixels.average()
//
//        listener(luma)

        val rotatedBitmap = image.toRotatedBitmap()

        Log.i("geeegeeheheh", "analyze: $rotatedBitmap")

        processImage(rotatedBitmap, poseDetector, surfaceView)

        image.close()
    }
}

// process image
private fun processImage(bitmap: Bitmap, detector: PoseDetector, surfaceView: SurfaceView) {
    var person: Person? = null
//    val classificationResult: List<Pair<String, Float>>? = null

    detector.estimateSinglePose(bitmap).let {
        person = it
    }

    person?.let {
        Log.i("rhrhrhhrrh", "${person?.score} ")
        visualize(it, bitmap, surfaceView)
    }
}

private const val MIN_CONFIDENCE = .2f

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
//    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    val rotateMatrix = Matrix()
    rotateMatrix.postRotate(90.0f)

    return Bitmap.createBitmap(
        imageBitmap, 0, 0, this.width, this.height,
        rotateMatrix, false
    )

}