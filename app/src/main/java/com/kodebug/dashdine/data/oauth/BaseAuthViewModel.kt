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
import com.kodebug.dashdine.data.DashDineSession
import com.kodebug.dashdine.data.models.OAuthRequest
import com.kodebug.dashdine.data.remote.ApiResponse
import com.kodebug.dashdine.data.remote.safeApiCall
import kotlinx.coroutines.launch

abstract class BaseAuthViewModel(open val apiService: DashDineApiService) : ViewModel() {
    private val googleAuthUiProvider = GoogleAuthUiProvider()
    private lateinit var callbackManager: CallbackManager

    var error : String = ""
    var errorDescription = ""

    abstract fun loading()
    abstract fun onGoogleError(msg : String)
    abstract fun onFacebookError(msg : String)
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
            try {
                val response = googleAuthUiProvider.signIn(
                    activityContext = context,
                    CredentialManager.create(
                        context = context
                    )
                )
                fetchDasDineAppToken(token = response.token, provider = "google"){

                    onGoogleError(it)
                }
            }catch (e: Throwable){
                onGoogleError(e.message.toString())
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
                    fetchDasDineAppToken(token = loginResult.accessToken.token, provider = "facebook"){
                        onGoogleError(it)
                    }
                }

                override fun onCancel() {
                    // App code
                    onFacebookError("Facebook sign-in cancelled")
                }

                override fun onError(exception: FacebookException) {
                    // App code
                    onFacebookError("Facebook sign-in failed \n${exception.message}")
                }
            }
        )
        LoginManager.getInstance().logInWithReadPermissions(
            context,
            callbackManager,
            listOf("public_profile", "email")
        )
    }

    private fun fetchDasDineAppToken(token: String, provider: String, onError: (String)-> Unit){
        viewModelScope.launch {
            val request = OAuthRequest(
                token = token,
                provider = provider
            )
            val res = safeApiCall { apiService.oAuth(request) }
            when(res){
                is ApiResponse.Success -> {
                    onSocialLoginSuccess(token = res.data.token)
                }
                else -> {
                    val error = (res as? ApiResponse.Error)?.code
                    if (error!=null){
                        when(error){
                            401 -> onError("Invalid token") //
                            404 -> onError("User not found") //
                            409 -> onError("User already exists")
                            400 -> onError("Bad request")
                            500 -> onError("Server error") //
                            503 -> onError("Service unavailable")
                            504 -> onError("Gateway timeout")
                            502 -> onError("Bad gateway")
                            else -> onError("Unknown error")
                        }
                    } else {
                        onError("Failed to fetch token \n Check your internet connection")
                    }
                }
            }
        }
    }
}