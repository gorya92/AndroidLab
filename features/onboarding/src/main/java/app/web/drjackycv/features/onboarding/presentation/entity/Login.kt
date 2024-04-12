package app.web.drjackycv.features.onboarding.presentation.entity

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import app.web.drjackycv.core.designsystem.viewBinding
import app.web.drjackycv.features.onboarding.R
import app.web.drjackycv.features.onboarding.databinding.LanguageChoiceFragmentBinding
import app.web.drjackycv.features.onboarding.databinding.LoginFragmentBinding
import app.web.drjackycv.features.onboarding.presentation.OnBoardingPrefManager

class Login : Fragment(R.layout.signup_fragment) {

    private val binding by viewBinding(LoginFragmentBinding::bind)
    private lateinit var prefManager: OnBoardingPrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = OnBoardingPrefManager(requireContext())
        setupListeners()
        binding.emailInput.doAfterTextChanged {
            validateEmail()
        }
        validateLoginButton()
        validateSignUpButton()
    }

    private fun setupListeners() {
        var seepas = false
        val passHide = binding.passwordInput.inputType
        binding.seepass.setOnClickListener {
            if (!seepas) {
                binding.passwordInput.inputType = InputType.TYPE_CLASS_TEXT
                seepas = true
            } else {
                binding.passwordInput.inputType = passHide
                seepas = false
            }
        }
    }

    private fun validateEmail(): Boolean {
        val emailInput = binding.emailInput.text.toString().trim()
        val emailPattern = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,4}\$")
        return if (emailInput.matches(emailPattern)) {
            binding.emailInput.error = null
            true
        } else {
            binding.emailInput.error = "Invalid email address"
            false
        }
    }

    private fun validateLoginButton() {
        binding.loginButton.setOnClickListener {
            if (binding.emailInput.text.toString() != "" && validateEmail() && binding.passwordInput.text != null) {

            }
        }
    }

    private fun validateSignUpButton() {
        binding.signupLink.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/language".toUri())
                .build()
            findNavController().navigate(request)
        }
    }


}