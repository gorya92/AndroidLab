package app.web.drjackycv.features.onboarding.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.http.HttpResponseCache.install
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import app.web.drjackycv.core.designsystem.viewBinding
import app.web.drjackycv.features.onboarding.R
import app.web.drjackycv.features.onboarding.databinding.LoginFragmentBinding
import app.web.drjackycv.features.onboarding.presentation.viewmodel.OnBoardingViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.serializer
import java.security.MessageDigest
import java.util.UUID

class Login : Fragment(R.layout.login_fragment) {

    private val binding by viewBinding(LoginFragmentBinding::bind)
    private lateinit var prefManager: OnBoardingPrefManager
    private lateinit var supabaseClient: SupabaseClient
    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = OnBoardingPrefManager(requireContext())
        setupListeners()
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
        if (prefManager.isAuth) {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/main".toUri())
                .build()
            findNavController().popBackStack()
            findNavController().navigate(request)
        }
        binding.emailInput.doAfterTextChanged {
            validateEmail()
        }
        validateLoginButton()
        validateSignUpButton()
        validateBackArrow()
        validateGoogleButton()
        setLanguage()
    }

    private fun setLanguage() {
        if (prefManager.isRussianActive) {
            setLocale(requireContext(), "ru")
        } else {
            setLocale(requireContext(), "en")
        }
        updateScreen()
    }

    private fun updateScreen() {
        binding.signup.text = getString(R.string.signup)
        binding.forFree.text = getString(R.string.for_free_join_now_and_n_start_learning)
        binding.email.text = getString(R.string.email_address)
        binding.password.text = getString(R.string.password)
        binding.forgotPassword.text = getString(R.string.forgot_password)
        binding.loginButton.text = getString(R.string.login)
        binding.notYourMember.text = getString(R.string.not_you_member)
        binding.signupLink.text = getString(R.string.signup_small)
        binding.useCanUse.text = getString(R.string.use_can_use)
        binding.googleBtn.text = getString(R.string.google_for)
        binding.signSmall.text = getString(R.string.sign_in_small)
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
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val result = supabaseClient.auth.signInWith(Email) {
                            this.email = binding.emailInput.text.toString().trim()
                            this.password = binding.passwordInput.text.toString().trim()
                        }
                        var stat = supabaseClient.auth.sessionStatus.value.toString()
                        val regex = Regex("\\bAuthenticated\\b")
                        val matchResult = regex.find(stat)
                        val sessionInfo = supabaseClient.auth.sessionStatus.value.toString()

                        stat = matchResult?.value ?: ""
                        if (stat != "") {
                            // Ищем индекс начала ID пользователя
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
                            prefManager.email = binding.emailInput.text.toString().trim()
                            val request = NavDeepLinkRequest.Builder
                                .fromUri("android-app://app.web.drjackycv/main".toUri())
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

    private fun validateGoogleButton() {
        binding.googleBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val credentialManager = CredentialManager.create(requireContext())

                    // Generate a nonce and hash it with sha-256
                    // Providing a nonce is optional but recommended
                    val rawNonce = UUID.randomUUID()
                        .toString() // Generate a random String. UUID should be sufficient, but can also be any other random string.
                    val bytes = rawNonce.toString().toByteArray()
                    val md = MessageDigest.getInstance("SHA-256")
                    val digest = md.digest(bytes)
                    val hashedNonce =
                        digest.fold("") { str, it -> str + "%02x".format(it) } // Hashed nonce to be passed to Google sign-in


                    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId("WEB_GOOGLE_CLIENT_ID")
                        .setNonce(hashedNonce) // Provide the nonce if you have one
                        .build()

                    val request: GetCredentialRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(
                        request = request,
                        context = requireContext(),
                    )

                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(result.credential.data)

                    val googleIdToken = googleIdTokenCredential.idToken

                    supabaseClient.auth.signInWith(IDToken) {
                        idToken = googleIdToken
                        provider = Google
                        nonce = rawNonce
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun validateSignUpButton() {
        binding.signupLink.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/signup".toUri())
                .build()
            findNavController().navigate(request)
        }
    }

    private fun validateBackArrow() {
        binding.back.setOnClickListener {
            prefManager.isLanguagedChoised = false
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/language".toUri())
                .build()
            findNavController().navigate(request)
        }
    }

}