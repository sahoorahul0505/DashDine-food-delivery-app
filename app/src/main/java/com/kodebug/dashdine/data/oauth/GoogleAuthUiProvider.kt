package com.kodebug.dashdine.data.oauth

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kodebug.dashdine.data.models.GoogleAccount
import com.kodebug.dashdine.ui.GoogleServerClientID

class GoogleAuthUiProvider {
    suspend fun signIn(
        activityContext: Context,
        credentialManager: CredentialManager
    ): GoogleAccount {
        val cred = credentialManager.getCredential(
            context = activityContext,
            request = getCredentialRequest()
        ).credential
        return handleCredentials(cred)
    }

    fun handleCredentials(cred: Credential): GoogleAccount {
        when {
            cred is CustomCredential && cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(cred.data)
                Log.d("GoogleAuthUiProvider", "GoogleIdTokenCredential: $googleIdTokenCredential")
                return GoogleAccount(
                    token = googleIdTokenCredential.idToken,
                    displayName = googleIdTokenCredential.displayName ?: "",
                    profileImageUri = googleIdTokenCredential.profilePictureUri.toString()
                )
            }

            else -> {
                throw IllegalStateException("Invalid credential type")
            }
        }
    }

    private fun getCredentialRequest(): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(
                    GoogleServerClientID
                ).build()
            )
            .build()
    }
}