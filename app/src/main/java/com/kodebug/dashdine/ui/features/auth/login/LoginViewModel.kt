package com.kodebug.dashdine.ui.features.auth.login

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.kodebug.dashdine.data.DashDineApiService
import com.kodebug.dashdine.data.models.LoginRequest
import com.kodebug.dashdine.data.models.OAuthRequest
import com.kodebug.dashdine.data.oauth.BaseAuthViewModel
import com.kodebug.dashdine.data.oauth.GoogleAuthUiProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(override val apiService: DashDineApiService) :
    BaseAuthViewModel(apiService) {


    private val _uiState = MutableStateFlow<LoginEvent>(LoginEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.value = LoginEvent.Loading
            try {
                val response = apiService.login(
                    LoginRequest(
                        email = _email.value,
                        password = _password.value
                    )
                )
                if (response.token.isNotEmpty()) {
                    Log.d("LoginClicked", "onLoginClick: ${response.token}")
                    _uiState.value = LoginEvent.Success
                    _navigationEvent.emit(LoginNavigationEvent.NavigationToHome)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = LoginEvent.Error
            }
        }
    }

//    fun onGoogleSinInClicked(context: ComponentActivity) {
//        initiateGoogleLogin(context = context)
//    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _navigationEvent.emit(LoginNavigationEvent.NavigationToSignUp)
        }
    }

//    fun onFacebookSignInClicked(context: ComponentActivity) {
//        initiateFacebookLogin(context = context)
//    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = LoginEvent.Loading
        }
    }

    override fun onGoogleError(mag: String) {
        viewModelScope.launch {
            _uiState.value = LoginEvent.Error
        }
    }

    override fun onFacebookError(mag: String) {
        viewModelScope.launch {
            _uiState.value = LoginEvent.Error
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            _uiState.value = LoginEvent.Success
            _navigationEvent.emit(LoginNavigationEvent.NavigationToHome)
        }
    }


    sealed class LoginNavigationEvent {
        object NavigationToSignUp : LoginNavigationEvent()
        object NavigationToHome : LoginNavigationEvent()
    }

    sealed class LoginEvent {
        object Nothing : LoginEvent()
        object Success : LoginEvent()
        object Error : LoginEvent()
        object Loading : LoginEvent()
    }
}