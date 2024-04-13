package app.web.drjackycv.features.onboarding.presentation

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
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
import app.web.drjackycv.features.onboarding.databinding.SignupFragmentBinding

class SignUp : Fragment(R.layout.signup_fragment) {

    private val binding by viewBinding(SignupFragmentBinding::bind)
    private lateinit var prefManager: OnBoardingPrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = OnBoardingPrefManager(requireContext())
        saveState()
        binding.emailInput.doAfterTextChanged {
            validateEmail()
        }
        validateLoginButton()
        validateSignUpButton()
        validateBackArrow()

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
            if (binding.emailInput.text.toString() != "" && validateEmail() && binding.lastnameInput.text.toString() != "" && binding.firstnameInput.text.toString() != "") {
                val request = NavDeepLinkRequest.Builder
                    .fromUri("android-app://app.web.drjackycv/signupPass".toUri())
                    .build()
                findNavController().navigate(request)
            }
        }
    }

    private fun saveState() {
        if (prefManager.firstName != "") {
            binding.firstnameInput.setText(prefManager.firstName)
        }
        if (prefManager.lastName != "") {
            binding.lastnameInput.setText(prefManager.lastName)
        }
        if (prefManager.email != "") {
            binding.emailInput.setText(prefManager.email)
        }
        binding.firstnameInput.doAfterTextChanged {
            prefManager.firstName = binding.firstnameInput.text.toString()
        }
        binding.lastnameInput.doAfterTextChanged {
            prefManager.lastName = binding.lastnameInput.text.toString()
        }
        binding.emailInput.doAfterTextChanged {
            prefManager.email = binding.emailInput.text.toString()
        }
    }

    private fun validateSignUpButton() {
        binding.signupLink.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/login".toUri())
                .build()
            findNavController().navigate(request)
        }
    }

    private fun validateBackArrow() {
        binding.back.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/login".toUri())
                .build()
            findNavController().navigate(request)
        }
    }


}