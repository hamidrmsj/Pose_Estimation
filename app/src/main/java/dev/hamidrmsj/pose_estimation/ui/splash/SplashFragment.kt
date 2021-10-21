package dev.hamidrmsj.pose_estimation.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.hamidrmsj.pose_estimation.R
import dev.hamidrmsj.pose_estimation.databinding.FragmentPoseEstimationBinding
import dev.hamidrmsj.pose_estimation.databinding.FragmentSplashBinding
import dev.hamidrmsj.pose_estimation.ui.MainActivity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.navigation.fragment.findNavController


class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToPoseEstimationFragment())
        }, 2000)
    }

}