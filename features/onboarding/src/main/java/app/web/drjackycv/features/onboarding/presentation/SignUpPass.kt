package app.web.drjackycv.features.onboarding.presentation

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
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
import app.web.drjackycv.features.onboarding.databinding.SignupPassFragmentBinding
import app.web.drjackycv.features.onboarding.presentation.OnBoardingPrefManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpPass : Fragment(R.layout.signup_pass_fragment) {

    private val binding by viewBinding(SignupPassFragmentBinding::bind)
    private lateinit var prefManager: OnBoardingPrefManager
    private lateinit var supabaseClient: SupabaseClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supabaseClient = createSupabaseClient(
            supabaseUrl = "https://emursxnkqovvvuzernos.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVtdXJzeG5rcW92dnZ1emVybm9zIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTcxMjc0NjkyNiwiZXhwIjoyMDI4MzIyOTI2fQ.WrgXa6sEc_RIeM-T4duVjSTr5S5TqULkKiovHP2BWwo"
        ) {
            install(Postgrest)
            install(Auth) {
                flowType = FlowType.PKCE
                scheme = "app"
                host = "supabase.com"
            }
            install(Storage)
        }
        prefManager = OnBoardingPrefManager(requireContext())
        validateLoginButton()
        validateSignUpButton()
        validateBackArrow()
    }


    private fun setupListeners() {
        var seepas = false
        var seepas2 = false
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
        binding.seepass2.setOnClickListener {
            if (!seepas) {
                binding.passwordInput2.inputType = InputType.TYPE_CLASS_TEXT
                seepas2 = true
            } else {
                binding.passwordInput2.inputType = passHide
                seepas2 = false
            }
        }
    }

    private fun validateLoginButton() {
        binding.loginButton.setOnClickListener {
            if (binding.passwordInput2.text.toString() == binding.passwordInput.text.toString()) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val result = supabaseClient.auth.signUpWith(Email) {
                            this.email = prefManager.email.toString().trim()
                            this.password = binding.passwordInput.text.toString().trim()
                        }
                        var stat = supabaseClient.auth.sessionStatus.value.toString()
                        val regex = Regex("\\bAuthenticated\\b")
                        val matchResult = regex.find(stat)
                        stat = matchResult?.value ?: ""

                        if (stat != "") {
                            val sessionInfo = supabaseClient.auth.sessionStatus.value.toString()
                            val startIndex = sessionInfo.indexOf("userId=")

                            if (startIndex != -1) {
                                // Находим индекс конца ID пользователя (первый символ после ID)
                                val endIndex = sessionInfo.indexOf(")", startIndex)

                                // Если конец ID пользователя найден
                                if (endIndex != -1) {
                                    // Получаем подстроку, содержащую ID пользователя
                                    val userIdSubstring =
                                        sessionInfo.substring(startIndex + 7, endIndex)
                                    prefManager.id = userIdSubstring
                                }
                            }
                            prefManager.isAuth = true
                            val request = NavDeepLinkRequest.Builder
                                .fromUri("android-app://app.web.drjackycv/profile".toUri())
                                .build()
                            findNavController().navigate(request)
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Ошибка аутентификации",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Ошибка аутентификации",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
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
                .fromUri("android-app://app.web.drjackycv/signup".toUri())
                .build()
            findNavController().navigate(request)
        }
    }


}