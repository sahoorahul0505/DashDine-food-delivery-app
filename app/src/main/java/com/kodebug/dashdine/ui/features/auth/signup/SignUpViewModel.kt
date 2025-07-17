package com.kodebug.dashdine.ui.features.auth.signup

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodebug.dashdine.data.DashDineApiService
import com.kodebug.dashdine.data.models.SignUpRequest
import com.kodebug.dashdine.data.oauth.BaseAuthViewModel
import com.kodebug.dashdine.ui.features.auth.login.LoginViewModel.LoginEvent
import com.kodebug.dashdine.ui.features.auth.login.LoginViewModel.LoginNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(override val apiService: DashDineApiService) :
    BaseAuthViewModel(apiService) {

    private val _uiState = MutableStateFlow<SignUpEvent>(SignUpEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignupNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Loading
            try {
                val response = apiService.signUp(
                    SignUpRequest(
                        name = _name.value,
                        email = _email.value,
                        password = _password.value
                    )
                )
                if (response.token.isNotEmpty()) {
                    _uiState.value = SignUpEvent.Success
                    _navigationEvent.emit(SignupNavigationEvent.NavigationToHome)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SignUpEvent.Error
            }
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _navigationEvent.emit(SignupNavigationEvent.NavigationToLogin)
        }
    }

//    fun onFacebookSignInClicked(context: ComponentActivity) {
//        initiateFacebookLogin(context = context)
//    }
//
//    fun onGoogleSinInClicked(context: ComponentActivity) {
//        initiateGoogleLogin(context = context)
//    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Loading
        }
    }

    override fun onGoogleError(mag: String) {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Error
        }
    }

    override fun onFacebookError(mag: String) {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Error
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Success
            _navigationEvent.emit(SignupNavigationEvent.NavigationToHome)
        }
    }


    sealed class SignupNavigationEvent {
        object NavigationToLogin : SignupNavigationEvent()
        object NavigationToHome : SignupNavigationEvent()
    }

    sealed class SignUpEvent {
        object Nothing : SignUpEvent()
        object Success : SignUpEvent()
        object Error : SignUpEvent()
        object Loading : SignUpEvent()
    }
}