package app.web.drjackycv.features.app.presentation

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import app.web.drjackycv.core.designsystem.setOnReactiveClickListener
import app.web.drjackycv.core.designsystem.viewBinding
import app.web.drjackycv.features.app.databinding.ProfileBinding
import app.web.drjackycv.features.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Profile : Fragment(R.layout.profile) {

    private val binding by viewBinding(ProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}