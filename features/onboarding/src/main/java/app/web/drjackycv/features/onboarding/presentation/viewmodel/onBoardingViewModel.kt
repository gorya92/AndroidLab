package app.web.drjackycv.features.onboarding.presentation.viewmodel

import android.content.res.Resources
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.web.drjackycv.common.exceptions.Failure
import app.web.drjackycv.core.designsystem.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnBoardingViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    @Inject
    lateinit var resources: Resources

    private val _failure: Channel<Failure> = Channel(Channel.BUFFERED)
    val failure: Flow<Failure> = _failure.receiveAsFlow()

    fun handleFailure(throwable: Throwable, retryAction: () -> Unit) {
        val failure = when (throwable) {
            is Failure.NoInternet -> {
                Failure.NoInternet(resources.getString(R.string.error_no_internet))
            }

            is Failure.Api -> {
                Failure.Api(throwable.msg)
            }

            is Failure.Timeout -> {
                Failure.Timeout(resources.getString(R.string.error_timeout))
            }

            is Failure.Unknown -> {
                Failure.Unknown(resources.getString(R.string.error_unknown))
            }

            else -> {
                Failure.Unknown(resources.getString(R.string.error_unknown))
            }
        }

        failure.retryAction = retryAction
        viewModelScope.launch {
            _failure.send(failure)
        }
    }

}

sealed interface CharacterUiState {
    data class Error(val error: Failure) : CharacterUiState
    object Loading : CharacterUiState
}