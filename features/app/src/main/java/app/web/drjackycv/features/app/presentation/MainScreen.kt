package app.web.drjackycv.features.app.presentation


import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import app.web.drjackycv.core.designsystem.viewBinding
import app.web.drjackycv.features.app.R
import app.web.drjackycv.features.app.databinding.MainFragmentBinding
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainScreen : Fragment(R.layout.main_fragment) {

    private val binding by viewBinding(MainFragmentBinding::bind)
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
        toProfile()
        binding.profiletv.text = "${binding.profiletv.text} ${prefManager.email}"
        downloadAndDisplayProfilePicture()
        getAllUsersData()
        toGame()
    }

    private fun getAllUsersData() {
        CoroutineScope(Dispatchers.Main).launch {


            val users = supabaseClient.from("users").select().decodeList<Users>()

            val user = users.find { it.email == prefManager.email }

            if (user != null) {
                prefManager.points = user.points
            } else {
                prefManager.points = 0f
            }

            val sortedUsers = users.sortedByDescending { it.points }

            binding.firstTopEmail.text = sortedUsers[0].email
            binding.secondTopEmail.text = sortedUsers[1].email
            binding.thirdTopEmail.text = sortedUsers[2].email


            binding.firstTopPoints.text =
                "${sortedUsers[0].points} ${binding.firstTopPoints.text}"
            binding.secondTopPoints.text =
                "${sortedUsers[1].points} ${binding.secondTopPoints.text}"
            binding.thirdTopPoints.text =
                "${sortedUsers[2].points} ${binding.thirdTopPoints.text}"


        }
    }

    private fun toGame() {
        binding.firstGame.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/gameone".toUri())
                .build()
            findNavController().navigate(request)
        }
        binding.secondGame.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/gametwo".toUri())
                .build()
            findNavController().navigate(request)
        }
    }

    private fun toProfile() {
        binding.profileAvatar.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/profile".toUri())
                .build()
            findNavController().navigate(request)
        }
    }

    private fun downloadAndDisplayProfilePicture() {
        val userId = prefManager.id // Get user ID from shared preferences
        val filename = "image$userId.png"

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val imageData = supabaseClient.storage
                    .from("user-avatars")
                    .downloadPublic(filename)

                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                withContext(Dispatchers.Main) {
                    binding.profileAvatar.setImageBitmap(bitmap)
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun changeTheme() {
        if (prefManager.isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
