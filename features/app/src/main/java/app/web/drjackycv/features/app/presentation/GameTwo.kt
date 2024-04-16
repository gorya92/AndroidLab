package app.web.drjackycv.features.app.presentation

import android.annotation.SuppressLint
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
import app.web.drjackycv.core.designsystem.gone
import app.web.drjackycv.core.designsystem.viewBinding
import app.web.drjackycv.core.designsystem.visible
import app.web.drjackycv.features.app.R
import app.web.drjackycv.features.app.databinding.GameOneFragmentBinding
import app.web.drjackycv.features.app.databinding.GameTwoFragmentBinding
import app.web.drjackycv.features.app.databinding.ProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class GameTwo : Fragment(R.layout.game_two_fragment) {

    private val binding by viewBinding(GameTwoFragmentBinding::bind)
    private lateinit var prefManager: ProfilePrefManager
    private lateinit var supabaseClient: SupabaseClient
    private var clicked: Int = 0

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
        checkButton()
        backButton()
        nextClick()
        downloadAllText()
    }

    private fun checkButton() {
        binding.check.setOnClickListener {
            when (clicked) {
                0 -> {}
                1 -> {
                    binding.check.gone()
                    binding.next.visible()
                    if (prefManager.pastCorrectGame2 == 1) {
                        binding.firstBtn.background =
                            resources.getDrawable(R.drawable.rounded_button_correct)
                        prefManager.isStreak += 1
                        prefManager.points += 1f
                        if (prefManager.isStreak >= 2f) {
                            prefManager.points += (0.2f * prefManager.isStreak)
                        }
                        setPoints()
                    } else {
                        prefManager.isStreak = 0
                        binding.firstBtn.background =
                            resources.getDrawable(R.drawable.rounded_button_incorrect)
                        if (prefManager.pastCorrectGame2 == 3) {
                            binding.thirdBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                        if (prefManager.pastCorrectGame2 == 2) {
                            binding.secondBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                        if (prefManager.pastCorrectGame2 == 4) {
                            binding.fourthBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                    }
                    binding.firstBtn.isEnabled = false
                    binding.secondBtn.isEnabled = false
                    binding.fourthBtn.isEnabled = false
                    binding.thirdBtn.isEnabled = false
                }

                2 -> {
                    binding.check.gone()
                    binding.next.visible()
                    if (prefManager.pastCorrectGame2 == 2) {
                        binding.secondBtn.background =
                            resources.getDrawable(R.drawable.rounded_button_correct)
                        prefManager.isStreak += 1
                        prefManager.points += 1f
                        if (prefManager.isStreak >= 2f) {
                            prefManager.points += (0.2f * prefManager.isStreak)
                        }
                        setPoints()
                    } else {
                        prefManager.isStreak = 0
                        binding.secondBtn.background =
                            resources.getDrawable(R.drawable.rounded_button_incorrect)
                        if (prefManager.pastCorrectGame2 == 3) {
                            binding.thirdBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                        if (prefManager.pastCorrectGame2 == 4) {
                            binding.fourthBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                        if (prefManager.pastCorrectGame2 == 1) {
                            binding.firstBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                    }
                    binding.firstBtn.isEnabled = false
                    binding.secondBtn.isEnabled = false
                    binding.fourthBtn.isEnabled = false
                    binding.thirdBtn.isEnabled = false
                }

                3 -> {
                    binding.check.gone()
                    binding.next.visible()
                    if (prefManager.pastCorrectGame2 == 3) {
                        binding.thirdBtn.background =
                            resources.getDrawable(R.drawable.rounded_button_correct)
                        prefManager.isStreak += 1
                        prefManager.points += 1f
                        if (prefManager.isStreak >= 2f) {
                            prefManager.points += (0.2f * prefManager.isStreak)
                        }
                        setPoints()
                    } else {
                        prefManager.isStreak = 0
                        binding.thirdBtn.background =
                            resources.getDrawable(R.drawable.rounded_button_incorrect)
                        if (prefManager.pastCorrectGame2 == 4) {
                            binding.fourthBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                        if (prefManager.pastCorrectGame2 == 2) {
                            binding.secondBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                        if (prefManager.pastCorrectGame2 == 1) {
                            binding.firstBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                    }
                    binding.firstBtn.isEnabled = false
                    binding.secondBtn.isEnabled = false
                    binding.fourthBtn.isEnabled = false
                    binding.thirdBtn.isEnabled = false
                }

                4 -> {
                    binding.check.gone()
                    binding.next.visible()
                    if (prefManager.pastCorrectGame2 == 4) {
                        binding.fourthBtn.background =
                            resources.getDrawable(R.drawable.rounded_button_correct)
                        prefManager.isStreak += 1
                        prefManager.points += 1f
                        if (prefManager.isStreak >= 2f) {
                            prefManager.points += (0.2f * prefManager.isStreak)
                        }
                        setPoints()
                    } else {
                        binding.fourthBtn.background =
                            resources.getDrawable(R.drawable.rounded_button_incorrect)
                        if (prefManager.pastCorrectGame2 == 3) {
                            binding.thirdBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                        if (prefManager.pastCorrectGame2 == 2) {
                            binding.secondBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                        if (prefManager.pastCorrectGame2 == 1) {
                            binding.firstBtn.background =
                                resources.getDrawable(R.drawable.rounded_button_correct)
                        }
                    }
                    binding.firstBtn.isEnabled = false
                    binding.secondBtn.isEnabled = false
                    binding.fourthBtn.isEnabled = false
                    binding.thirdBtn.isEnabled = false
                }
            }
        }
        binding.firstBtn.setOnClickListener {
            clicked = 1
            binding.firstBtn.background =
                resources.getDrawable(R.drawable.rounded_button_correct_all)
            binding.firstBtn.isEnabled = false
            binding.secondBtn.isEnabled = false
            binding.fourthBtn.isEnabled = false
            binding.thirdBtn.isEnabled = false
        }
        binding.secondBtn.setOnClickListener {
            clicked = 2
            binding.secondBtn.background =
                resources.getDrawable(R.drawable.rounded_button_correct_all)
            binding.firstBtn.isEnabled = false
            binding.secondBtn.isEnabled = false
            binding.fourthBtn.isEnabled = false
            binding.thirdBtn.isEnabled = false
        }
        binding.thirdBtn.setOnClickListener {
            clicked = 3
            binding.thirdBtn.background =
                resources.getDrawable(R.drawable.rounded_button_correct_all)
            binding.firstBtn.isEnabled = false
            binding.secondBtn.isEnabled = false
            binding.fourthBtn.isEnabled = false
            binding.thirdBtn.isEnabled = false
        }
        binding.fourthBtn.setOnClickListener {
            clicked = 4
            binding.fourthBtn.background =
                resources.getDrawable(R.drawable.rounded_button_correct_all)
            binding.firstBtn.isEnabled = false
            binding.secondBtn.isEnabled = false
            binding.fourthBtn.isEnabled = false
            binding.thirdBtn.isEnabled = false
        }
    }


    private fun nextClick() {
        binding.next.setOnClickListener {
            binding.firstBtn.isEnabled = true
            binding.secondBtn.isEnabled = true
            binding.fourthBtn.isEnabled = true
            binding.thirdBtn.isEnabled = true
            clicked = 0
            binding.check.visible()
            binding.next.gone()
            downloadAllText()
        }
    }

    private fun setPoints() {
        CoroutineScope(Dispatchers.IO).launch {
            try {

                supabaseClient.from("users").delete {
                    filter {
                        Users::email eq prefManager.email
                    }
                }
                val city = Users(email = prefManager.email!!, points = prefManager.points)
                supabaseClient.from("users").insert(city)
            } catch (e: Exception) {

            }
        }
    }

    private fun backButton() {
        binding.back.setOnClickListener {
            prefManager.isStreak = 0
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/main".toUri())
                .build()
            findNavController().navigate(request)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun downloadAllText() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val animal: List<GameTwoModel>
                if (prefManager.pastRU) {
                    animal = supabaseClient.from("game2_en").select().decodeList<GameTwoModel>()
                    prefManager.pastRU = false
                } else {
                    animal = supabaseClient.from("game2_ru").select().decodeList<GameTwoModel>()
                    prefManager.pastRU = true
                }

                val randomAnimal = animal.random()
                prefManager.pastCorrectGame2 = randomAnimal.correct

                binding.text.text = randomAnimal.text
                binding.trans.text = randomAnimal.transcription
                binding.firstBtn.text = randomAnimal.first
                binding.secondBtn.text = randomAnimal.second
                binding.thirdBtn.text = randomAnimal.third
                binding.fourthBtn.text = randomAnimal.fourth

                binding.firstBtn.background = resources.getDrawable(R.drawable.base)
                binding.secondBtn.background = resources.getDrawable(R.drawable.base)
                binding.thirdBtn.background = resources.getDrawable(R.drawable.base)
                binding.fourthBtn.background = resources.getDrawable(R.drawable.base)


            } catch (e: Exception) {
            }
        }
    }

}
