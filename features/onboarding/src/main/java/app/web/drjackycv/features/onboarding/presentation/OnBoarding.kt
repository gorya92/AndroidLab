package app.web.drjackycv.features.onboarding.presentation

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import app.web.drjackycv.core.designsystem.viewBinding
import app.web.drjackycv.features.onboarding.R
import app.web.drjackycv.features.onboarding.databinding.OnboardingBinding
import app.web.drjackycv.features.onboarding.presentation.customView.OnBoardingView

class OnBoarding : Fragment(R.layout.onboarding) {

    private val binding by viewBinding(OnboardingBinding::bind)
    private lateinit var onBoardingView: OnBoardingView
    private lateinit var prefManager: OnBoardingPrefManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBoardingView = binding.container as OnBoardingView
        onBoardingView.setNavController(findNavController())
        prefManager = OnBoardingPrefManager(requireContext())

        if (!prefManager.isFirstTimeLaunch) {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/language".toUri())
                .build()
            findNavController().navigate(request)
        }
    }
}