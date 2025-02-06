package com.ryh.suyangdaegun.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.ryh.suyangdaegun.R

class AuthManager(private val activity: Activity) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val signInClient: SignInClient = Identity.getSignInClient(activity)
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signInWithGoogle(onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(activity.getString(R.string.default_web_client_id)) // google-services.json에서 ID 가져오기
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        signInClient.beginSignIn(signInRequest)
            .addOnSuccessListener(activity) { result ->
                try {
                    activity.startIntentSenderForResult(
                        result.pendingIntent.intentSender,
                        RC_SIGN_IN,
                        null,
                        0,
                        0,
                        0
                    )
                } catch (e: Exception) {
                    onFailure(e)
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


    fun handleSignInResult(data: Intent?, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        val oneTapClient = Identity.getSignInClient(activity)

        oneTapClient.getSignInCredentialFromIntent(data)?.googleIdToken?.let { idToken ->
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            checkUserExists(user.uid, onSuccess, onFailure)
                        } else {
                            onFailure(Exception("로그인 실패"))
                        }
                    } else {
                        onFailure(task.exception ?: Exception("로그인 실패"))
                    }
                }
        } ?: onFailure(Exception("Google ID Token이 없습니다."))
    }


    private fun checkUserExists(uid: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onSuccess(true) // 기존 회원 -> 메인 화면으로 이동
                } else {
                    onSuccess(false) // 신규 회원 -> 회원가입 진행
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    companion object {
        const val RC_SIGN_IN = 1001
    }
}
