package dev.hamidrmsj.pose_estimation.ml

import android.graphics.Bitmap
import dev.hamidrmsj.pose_estimation.data.Person

interface PoseDetector : AutoCloseable {

    fun estimateSinglePose(bitmap: Bitmap): Person

    fun lastInferenceTimeNanos(): Long
}
