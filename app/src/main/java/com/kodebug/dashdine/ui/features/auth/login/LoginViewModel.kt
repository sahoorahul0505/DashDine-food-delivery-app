package com.kodebug.dashdine.ui.features.auth.login

import android.content.Context
import android.util.Log
import android.util.Patterns
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
import com.kodebug.dashdine.data.DashDineSession
import com.kodebug.dashdine.data.models.LoginRequest
import com.kodebug.dashdine.data.models.OAuthRequest
import com.kodebug.dashdine.data.oauth.BaseAuthViewModel
import com.kodebug.dashdine.data.oauth.GoogleAuthUiProvider
import com.kodebug.dashdine.data.remote.ApiResponse
import com.kodebug.dashdine.data.remote.safeApiCall
import com.kodebug.dashdine.ui.features.auth.signup.SignUpViewModel.SignUpEvent
import com.kodebug.dashdine.ui.features.auth.signup.SignUpViewModel.SignupNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(override val apiService: DashDineApiService, val session: DashDineSession) :
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
            if (email.value.isEmpty() || password.value.isEmpty()){
                error = "Missing Credentials"
                errorDescription = "Email and password cannot be empty."
                _uiState.value = LoginEvent.Error
                _navigationEvent.emit(LoginNavigationEvent.ShowErrorDialog)
                return@launch


            }else if (Patterns.EMAIL_ADDRESS.matcher(_email.value).matches().not()){
                error = "Invalid Email Format"
                errorDescription = "Please enter a valid email address."
                _uiState.value = LoginEvent.Error
                _navigationEvent.emit(LoginNavigationEvent.ShowErrorDialog)
                return@launch
            }

            _uiState.value = LoginEvent.Loading

            val response = safeApiCall {
                apiService.login(
                    LoginRequest(
                        email = _email.value,
                        password = _password.value
                    )
                )
            }

            when (response) {
                is ApiResponse.Success -> {
                    session.storeToken(response.data.token)
                    _uiState.value = LoginEvent.Success
                    Log.d("TOKEN", response.data.token)
                    _navigationEvent.emit(LoginNavigationEvent.NavigationToHome)
                }

                else -> {
                    val errorResponse = (response as? ApiResponse.Error)?.code ?: 0
                    error = ""
                    errorDescription = ""
                    when (errorResponse) {
                        400 -> {
                            error = "Invalid Credential"
                            errorDescription = "Please enter correct details"
                        }
                        401 -> {
                            error = "Unauthorized Access"
                            errorDescription = "You are not authorized to perform this action."
                        }
                        403 -> {
                            error = "Access Denied"
                            errorDescription = "You don’t have permission to log in."
                        }
                        404 -> {
                            error = "User Not Found"
                            errorDescription = "No account found with this email."
                        }

                        500 -> {
                            error = "Server Error"
                            errorDescription = "Something went wrong on our end. \nPlease try again later."
                        }

                        503 -> {
                            error = "Service Unavailable"
                            errorDescription = "The server is temporarily unavailable. \nPlease try again later."
                        }

                        else -> {
                            error = "Login Failed"
                            errorDescription =
                                "An unexpected error occurred. \nPlease check your internet connection"
                        }

                    }
                    _uiState.value = LoginEvent.Error
                    _navigationEvent.emit(LoginNavigationEvent.ShowErrorDialog)
                }
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

    override fun onGoogleError(msg: String) {
        viewModelScope.launch {
            error = "Google Sign In Failed"
            errorDescription = msg
            _uiState.value = LoginEvent.Error
            _navigationEvent.emit(LoginNavigationEvent.ShowErrorDialog)
        }
    }

    override fun onFacebookError(msg: String) {
        viewModelScope.launch {
            error = "Facebook Sign In Failed"
            errorDescription = msg
            _uiState.value = LoginEvent.Error
            _navigationEvent.emit(LoginNavigationEvent.ShowErrorDialog)
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            session.storeToken(token)
            _uiState.value = LoginEvent.Success
            _navigationEvent.emit(LoginNavigationEvent.NavigationToHome)
        }
    }


    sealed class LoginNavigationEvent {
        object NavigationToSignUp : LoginNavigationEvent()
        object NavigationToHome : LoginNavigationEvent()
        object ShowErrorDialog : LoginNavigationEvent()
    }

    sealed class LoginEvent {
        object Nothing : LoginEvent()
        object Success : LoginEvent()
        object Error : LoginEvent()
        object Loading : LoginEvent()
    }
}