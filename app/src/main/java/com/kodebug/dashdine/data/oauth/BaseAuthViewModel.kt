package com.kodebug.dashdine.data.oauth

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.kodebug.dashdine.data.DashDineApiService
import com.kodebug.dashdine.data.models.OAuthRequest
import kotlinx.coroutines.launch

abstract class BaseAuthViewModel(open val apiService: DashDineApiService) : ViewModel() {
    private val googleAuthUiProvider = GoogleAuthUiProvider()
    private lateinit var callbackManager: CallbackManager

    abstract fun loading()
    abstract fun onGoogleError(mag : String)
    abstract fun onFacebookError(mag : String)
    abstract fun onSocialLoginSuccess(token : String)

    fun onGoogleClicked(context: ComponentActivity){
        initiateGoogleLogin(context = context)
    }
    fun onFacebookClicked(context: ComponentActivity){
        initiateFacebookLogin(context = context)
    }

    protected fun initiateGoogleLogin(context: ComponentActivity) {
        viewModelScope.launch {
            loading()
            val response = googleAuthUiProvider.signIn(
                activityContext = context,
                CredentialManager.create(
                    context = context
                )
            )
            if (response != null) {
                val request = OAuthRequest(
                    token = response.token,
                    provider = "google"
                )
                val res = apiService.oAuth(request)
                Log.d("LoginViewModel", "onGoogleSinInClick: $res")
                if (res.token.isNotEmpty()) {
                    onSocialLoginSuccess(token = res.token)
                } else {
                    onGoogleError("Google sign-in failed")
                }
            } else {
                onGoogleError("Internet connection error")
            }
        }
    }

    protected fun initiateFacebookLogin(context: ComponentActivity) {
        loading()
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    viewModelScope.launch {
                        // App code
                        val request = OAuthRequest(
                            token = loginResult.accessToken.token,
                            provider = "facebook"
                        )
                        val res = apiService.oAuth(request)
                        Log.d("LoginViewModel", "onFacebookSinInClick: $res")
                        if (res.token.isNotEmpty()) {
                            onSocialLoginSuccess(token = res.token)
                        } else {
                            onFacebookError("Facebook sign-in failed token is empty")
                        }
                    }

                }

                override fun onCancel() {
                    // App code
                    onFacebookError("Facebook sign-in cancelled")
                }

                override fun onError(exception: FacebookException) {
                    // App code
                    onFacebookError("Facebook sign-in failed ${exception.message}")
                }
            }
        )
        LoginManager.getInstance().logInWithReadPermissions(
            context,
            callbackManager,
            listOf("public_profile", "email")
        )
    }
}