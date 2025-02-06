package com.ryh.suyangdaegun

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser // 자동 로그인 체크용
    }

    fun initializeGoogleSignIn(activity: Activity) {
        oneTapClient = Identity.getSignInClient(activity)

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("YOUR_WEB_CLIENT_ID") // Firebase 콘솔에서 가져온 Web Client ID 입력
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    fun signInWithGoogle(activity: Activity, onResult: (Boolean, Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val signInIntent = oneTapClient.beginSignIn(signInRequest).result.pendingIntent.intentSender
                activity.startIntentSenderForResult(
                    signInIntent, GOOGLE_SIGN_IN, null, 0, 0, 0, null
                )
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Google Sign-In Failed", e)
                onResult(false, false, e.localizedMessage)
            }
        }
    }

    fun handleSignInResult(data: Intent?, onResult: (Boolean, Boolean, String?) -> Unit) {
        val googleCredential = Identity.getSignInClient(data!!.context).getSignInCredentialFromIntent(data)
        val googleIdToken = googleCredential.googleIdToken

        if (googleIdToken != null) {
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val isNewUser = task.result.additionalUserInfo?.isNewUser == true
                        onResult(true, isNewUser, null) // isNewUser = true이면 회원가입 진행
                    } else {
                        onResult(false, false, task.exception?.message)
                    }
                }
        } else {
            onResult(false, false, "Google ID Token is null")
        }
    }

    fun signOut() {
        auth.signOut()
    }

    companion object {
        const val GOOGLE_SIGN_IN = 9001
    }
}
