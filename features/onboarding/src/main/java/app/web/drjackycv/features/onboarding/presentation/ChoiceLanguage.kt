package app.web.drjackycv.features.onboarding.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import app.web.drjackycv.core.designsystem.viewBinding
import app.web.drjackycv.features.onboarding.R
import app.web.drjackycv.features.onboarding.databinding.LanguageChoiceFragmentBinding

class ChoiceLanguage : Fragment(R.layout.language_choice_fragment) {

    private val binding by viewBinding(LanguageChoiceFragmentBinding::bind)
    private lateinit var prefManager: OnBoardingPrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = OnBoardingPrefManager(requireContext())
        setupListeners()
        if (prefManager.isLanguagedChoised) {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/login".toUri())
                .build()
            findNavController().navigate(request)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupListeners() {
        if (prefManager.isRussianActive) {
            binding.russianbtn.background = resources.getDrawable(R.drawable.rounded_active)
            binding.englishbtn.background = resources.getDrawable(R.drawable.rounded_inactive)
            setLocale(requireContext(), "ru")
        } else {
            binding.russianbtn.background = resources.getDrawable(R.drawable.rounded_inactive)
            binding.englishbtn.background = resources.getDrawable(R.drawable.rounded_active)
            setLocale(requireContext(), "en")
        }
        updateButtonTexts()
        binding.russianbtn.setOnClickListener {
            binding.russianbtn.background = resources.getDrawable(R.drawable.rounded_active)
            binding.englishbtn.background = resources.getDrawable(R.drawable.rounded_inactive)
            prefManager.isRussianActive = true
            setLocale(requireContext(), "ru")
            updateButtonTexts()
        }
        binding.englishbtn.setOnClickListener {
            binding.russianbtn.background = resources.getDrawable(R.drawable.rounded_inactive)
            binding.englishbtn.background = resources.getDrawable(R.drawable.rounded_active)
            setLocale(requireContext(), "en")
            prefManager.isRussianActive = false
            updateButtonTexts()
        }
        binding.choosebtn.setOnClickListener {
            prefManager.isLanguagedChoised = true
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/login".toUri())
                .build()
            findNavController().navigate(request)
        }
    }

    private fun updateButtonTexts() {
        binding.russiantv.text = getString(R.string.russian)
        binding.englishtv.text = getString(R.string.english)
        binding.choosebtn.text = getString(R.string.choose)
        binding.languageSelect.text = getString(R.string.language_select)
        binding.motherLanguage.text = getString(R.string.what_is_your_mother_language)
    }
}