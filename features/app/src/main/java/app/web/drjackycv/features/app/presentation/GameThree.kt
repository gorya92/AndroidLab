package app.web.drjackycv.features.app.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import app.web.drjackycv.core.designsystem.gone
import app.web.drjackycv.core.designsystem.viewBinding
import app.web.drjackycv.core.designsystem.visible
import app.web.drjackycv.features.app.R
import app.web.drjackycv.features.app.databinding.GameThreeFragmentBinding
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

class GameThree : Fragment(R.layout.game_three_fragment) {

    private val binding by viewBinding(GameThreeFragmentBinding::bind)
    private lateinit var prefManager: ProfilePrefManager
    private lateinit var supabaseClient: SupabaseClient

    private val RECORD_AUDIO_REQUEST_CODE = 101
    private lateinit var speechRecognizer: SpeechRecognizer
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
        backButton()
        nextClick()
        downloadAllText()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_REQUEST_CODE
            )
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer.setRecognitionListener(recognitionListener)
        listener()
    }


    private fun nextClick() {
        binding.next.setOnClickListener {
            downloadAllText()
            binding.next.gone()
            binding.checkSpeechBtn.visible()
            binding.thirdAfter.gone()
            binding.animalText.setText("")
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
                animal = supabaseClient.from("game2_en").select().decodeList<GameTwoModel>()
                prefManager.pastRU = false


                val randomAnimal = animal.random()
                prefManager.pastCorrectGame2 = randomAnimal.correct

                binding.text.text = randomAnimal.text
                binding.trans.text = randomAnimal.transcription


            } catch (e: Exception) {
            }
        }
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {}

        override fun onResults(results: Bundle?) {
            if (results != null) {
                val matches: ArrayList<String>? =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    updateTextView(recognizedText)
                } else {
                    updateTextView("")
                }
            } else {
                updateTextView("")
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }


    private fun listener() {
        binding.checkSpeechBtn.setOnClickListener {
            binding.animalText.setText("")
            startRecording()
        }
        binding.micro.setOnClickListener {
            stopRecording()
        }
    }

    private fun startRecording() {
        binding.micro.visible()
        binding.checkSpeechBtn.gone()
        binding.thirdAfter.visible()
        val pulseAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.microphone_pulse)
        binding.micro.startAnimation(pulseAnimation)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
        speechRecognizer.startListening(intent)
    }

    private fun stopRecording() {
        speechRecognizer.stopListening()
        binding.micro.clearAnimation()
    }

    private fun updateTextView(text: String) {
        binding.animalText.setText(text)
        if (binding.text.text.toString().toLowerCase() == text.toLowerCase()) {
            binding.animalText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    app.web.drjackycv.core.designsystem.R.color.correct
                )
            )
            prefManager.isStreak += 1
            prefManager.points += 1f
            if (prefManager.isStreak >= 2f) {
                prefManager.points += (2f * prefManager.isStreak)
            }
            setPoints()
            binding.next.visible()
            binding.micro.gone()
        } else {
            binding.animalText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    app.web.drjackycv.core.designsystem.R.color.error
                )
            )
            binding.checkSpeechBtn.visible()
            binding.micro.gone()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

}
