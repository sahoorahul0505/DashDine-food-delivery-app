package com.kodebug.dashdine.ui.features.auth

import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import com.kodebug.dashdine.data.DashDineApiService
import com.kodebug.dashdine.data.DashDineSession
import com.kodebug.dashdine.data.models.LoginRequest
import com.kodebug.dashdine.data.oauth.BaseAuthViewModel
import com.kodebug.dashdine.ui.features.auth.signup.SignUpViewModel.SignupNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(override val apiService: DashDineApiService, val session: DashDineSession) :
    BaseAuthViewModel(apiService) {
    private val _uiState = MutableStateFlow<AuthEvent>(AuthEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = AuthEvent.Loading
        }
    }

    override fun onGoogleError(msg: String) {
        viewModelScope.launch {
            error = "Google Sign In Failed"
            errorDescription = msg
            _uiState.value = AuthEvent.Error
            _navigationEvent.emit(AuthNavigationEvent.ShowErrorDialog)
        }
    }

    override fun onFacebookError(msg: String) {
        viewModelScope.launch {
            error = "Facebook Sign In Failed"
            errorDescription = msg
            _uiState.value = AuthEvent.Error
            _navigationEvent.emit(AuthNavigationEvent.ShowErrorDialog)
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            session.storeToken(token)
            _uiState.value = AuthEvent.Success
            _navigationEvent.emit(AuthNavigationEvent.NavigationToHome)
        }
    }


    fun onSignUpClick() {
        viewModelScope.launch {
            _navigationEvent.emit(AuthNavigationEvent.NavigationToSignUp)
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _navigationEvent.emit(AuthNavigationEvent.NavigationToLogin)
        }
    }

    sealed class AuthNavigationEvent {
        object NavigationToSignUp : AuthNavigationEvent()
        object NavigationToLogin : AuthNavigationEvent()
        object NavigationToHome : AuthNavigationEvent()
        object ShowErrorDialog : AuthNavigationEvent()
    }

    sealed class AuthEvent {
        object Nothing : AuthEvent()
        object Success : AuthEvent()
        object Error : AuthEvent()
        object Loading : AuthEvent()
    }
}