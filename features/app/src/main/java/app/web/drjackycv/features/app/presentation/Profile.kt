package app.web.drjackycv.features.app.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import app.web.drjackycv.core.designsystem.viewBinding
import app.web.drjackycv.features.app.R
import app.web.drjackycv.features.app.databinding.ProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class Profile : Fragment(R.layout.profile) {

    private val binding by viewBinding(ProfileBinding::bind)
    private lateinit var prefManager: ProfilePrefManager
    private lateinit var supabaseClient: SupabaseClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = ProfilePrefManager(requireContext())
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

        chaingeimage()
        changeTheme()
        changeLanguage()
        binding.profiletv.text = "${binding.profiletv.text} ${prefManager.email}"
        downloadAndDisplayProfilePicture()
        signOut()
    }

    private fun downloadAndDisplayProfilePicture() {
        val userId = prefManager.id // Get user ID from shared preferences
        val filename = "image$userId.png"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageData = supabaseClient.storage
                    .from("user-avatars")
                    .downloadPublic(filename)

                if (imageData != null) {
                    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    withContext(Dispatchers.Main) {
                        binding.profileAvatar.setImageBitmap(bitmap)
                    }
                } else {
                    // Handle case where image is not found
                    // (e.g., display default image)
                }
            } catch (e: Exception) {
                // Handle download error
            }
        }
    }

    private fun chaingeimage() {
        binding.changeImage.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(requireContext(), this)
        }
        binding.profileAvatar.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(requireContext(), this)
        }
    }

    private fun changeTheme() {
        if (prefManager.isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.switchTheme.setOnClickListener {
            if (!prefManager.isDarkTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            if (prefManager.isRussianActive) {
                setLocale(requireContext(), "ru")
            } else {
                setLocale(requireContext(), "en")
            }
            prefManager.isDarkTheme = !prefManager.isDarkTheme
        }
        binding.Logout.background = resources.getDrawable(R.drawable.rounded_inactive)

    }

    private fun changeLanguage() {
        binding.changeLanguage.setOnClickListener {
            prefManager.isLanguagedChoised = false
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/language".toUri())
                .build()
            findNavController().navigate(request)
        }
    }

    private fun signOut() {
        binding.Logout.setOnClickListener {
            prefManager.isAuth = false
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    supabaseClient.auth.clearSession()
                } catch (e: Exception) {

                }
            }
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/login".toUri())
                .build()
            findNavController().navigate(request)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            val selectedImageUri: Uri? = result.uri
            binding.profileAvatar.setImageURI(selectedImageUri)
            if (selectedImageUri != null) {
                val userId = prefManager.id // Get user ID from shared preferences

                uploadImageToStorage(selectedImageUri)
            }
        }
    }


    private fun uploadImageToStorage(imageUri: Uri?) {
        if (imageUri != null) {
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            val byteArray = inputStream?.readBytes()

            if (byteArray != null) {
                uploadImageToStorage(byteArray)
            } else {
                Snackbar.make(
                    requireView(),
                    "Не удалось прочитать изображение",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } else {
            Snackbar.make(requireView(), "Изображение не выбрано", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToStorage(byteArray: ByteArray) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                supabaseClient.storage
                    .from("user-avatars")
                    .upload("image${prefManager.id}.png", byteArray, upsert = true)
            } catch (e: Exception) {
            }

        }
    }

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }

}
