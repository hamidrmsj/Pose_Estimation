package dev.hamidrmsj.pose_estimation.ui.pose_estimation

import CameraSource
import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dev.hamidrmsj.pose_estimation.R
import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.lifecycleScope
import dev.hamidrmsj.pose_estimation.databinding.FragmentPoseEstimationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.poseestimation.data.Device



class PoseEstimationFragment : Fragment() {

    private lateinit var binding: FragmentPoseEstimationBinding

    private var cameraSource: CameraSource? = null

    private var device = Device.CPU

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPoseEstimationBinding.inflate(inflater)
        if(!requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            // show dialog: your device hasn't any camera device
        } else {
            checkCameraPermission()
        }
        return binding.root
    }

    private fun checkCameraPermission() {
        Dexter.withActivity(requireActivity())
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted, open the camera
                    openCamera()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun openCamera() {
        if (cameraSource == null) {
            cameraSource =
                CameraSource(binding.surfaceView, object : CameraSource.CameraSourceListener {
                    override fun onFPSListener(fps: Int) {
//                        tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                    }

                    override fun onDetectedInfo(
                        personScore: Float?,
                        poseLabels: List<Pair<String, Float>>?
                    ) {
//                        tvScore.text = getString(R.string.tfe_pe_tv_score, personScore ?: 0f)
//                        poseLabels?.sortedByDescending { it.second }?.let {
//                            tvClassificationValue1.text = getString(
//                                R.string.tfe_pe_tv_classification_value,
//                                convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
//                            )
//                            tvClassificationValue2.text = getString(
//                                R.string.tfe_pe_tv_classification_value,
//                                convertPoseLabels(if (it.size >= 2) it[1] else null)
//                            )
//                            tvClassificationValue3.text = getString(
//                                R.string.tfe_pe_tv_classification_value,
//                                convertPoseLabels(if (it.size >= 3) it[2] else null)
//                            )
//                        }
                    }

                }).apply {
                    // Find the First camera of the device that is not Front Camera
                    prepareCamera()
                }
//            isPoseClassifier()
            lifecycleScope.launch(Dispatchers.Main) {
                cameraSource?.initCamera()
            }
        }
        createPoseEstimator()
    }



    private fun createPoseEstimator() {
        val poseDetector = MoveNet.create(requireContext(), device)
        cameraSource?.setDetector(poseDetector)
    }


}