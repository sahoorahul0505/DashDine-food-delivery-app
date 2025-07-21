package com.kodebug.dashdine.ui.features.auth.signup

import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodebug.dashdine.data.DashDineApiService
import com.kodebug.dashdine.data.DashDineSession
import com.kodebug.dashdine.data.models.SignUpRequest
import com.kodebug.dashdine.data.oauth.BaseAuthViewModel
import com.kodebug.dashdine.data.remote.ApiResponse
import com.kodebug.dashdine.data.remote.safeApiCall
import com.kodebug.dashdine.ui.features.auth.AuthViewModel.AuthNavigationEvent
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
class SignUpViewModel @Inject constructor(
    override val apiService: DashDineApiService,
    val session: DashDineSession
) :
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

            if (name.value.isEmpty() || email.value.isEmpty() || password.value.isEmpty()) {
                error = "Insufficient Details"
                errorDescription = "Please fill all the details"
                _uiState.value = SignUpEvent.Error
                _navigationEvent.emit(SignupNavigationEvent.ShowErrorDialog)
                return@launch
            }

            if (Patterns.EMAIL_ADDRESS.matcher(_email.value).matches().not()) {
                error = "Invalid Email Format"
                errorDescription = "Please enter a valid email address."
                _uiState.value = SignUpEvent.Error
                _navigationEvent.emit(SignupNavigationEvent.ShowErrorDialog)
                return@launch
            }
            _uiState.value = SignUpEvent.Loading

            val response = safeApiCall {
                apiService.signUp(
                    SignUpRequest(
                        name = _name.value,
                        email = _email.value,
                        password = _password.value
                    )
                )
            }
            when (response) {
                is ApiResponse.Success -> {
                    session.storeToken(response.data.token)
                    _uiState.value = SignUpEvent.Success
                    _navigationEvent.emit(SignupNavigationEvent.NavigationToHome)
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

                        409 -> {
                            error = "User Already Exists"
                            errorDescription = "User with this email already exists"
                        }

                        500 -> {
                            error = "Server Error / Email Already Exist"
                            errorDescription = "Something went wrong on our end. \nPlease try again later."
                        }

                        503 -> {
                            error = "Service Unavailable"
                            errorDescription = "The server is temporarily unavailable. \nPlease try again later."
                        }

                        else -> {
                            error = "Sign Up Failed"
                            errorDescription =
                                "An unexpected error occurred. \n Please check your internet connection."
                        }

                    }
                    _uiState.value = SignUpEvent.Error
                    _navigationEvent.emit(SignupNavigationEvent.ShowErrorDialog)
//                    return@launch
                }
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

    override fun onGoogleError(msg: String) {
        viewModelScope.launch {
            error = "Google Sign In Failed"
            errorDescription = msg
            _uiState.value = SignUpEvent.Error
            _navigationEvent.emit(SignupNavigationEvent.ShowErrorDialog)
        }
    }

    override fun onFacebookError(msg: String) {
        viewModelScope.launch {
            error = "facebook Sign In Failed"
            errorDescription = msg
            _uiState.value = SignUpEvent.Error
            _navigationEvent.emit(SignupNavigationEvent.ShowErrorDialog)
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            session.storeToken(token)
            _uiState.value = SignUpEvent.Success
            _navigationEvent.emit(SignupNavigationEvent.NavigationToHome)
        }
    }


    sealed class SignupNavigationEvent {
        object NavigationToLogin : SignupNavigationEvent()
        object NavigationToHome : SignupNavigationEvent()
        object ShowErrorDialog : SignupNavigationEvent()
    }

    sealed class SignUpEvent {
        object Nothing : SignUpEvent()
        object Success : SignUpEvent()
        object Error : SignUpEvent()
        object Loading : SignUpEvent()
    }
}