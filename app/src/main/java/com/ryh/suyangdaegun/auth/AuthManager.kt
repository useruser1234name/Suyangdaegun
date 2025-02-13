package com.ryh.suyangdaegun.auth

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ryh.suyangdaegun.R
import com.ryh.suyangdaegun.SuyangdaegunApp
//회원가입 파이어베이스 구글 로그인 - uid할당 -> 기능 사용(매칭, 메시지 전송 등)
class AuthManager(private val activity: SuyangdaegunApp) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val signInClient: SignInClient = Identity.getSignInClient(activity)
    //구글 로그인
    fun signInWithGoogle(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        onFailure: (Exception) -> Unit // 🔹 추가된 콜백
    ) {
        Log.d("AuthManager", "signInWithGoogle called")
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(activity.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        signInClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                Log.d("AuthManager", "beginSignIn successful; launching pendingIntent")
                launcher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
            }
            .addOnFailureListener { e ->
                Log.e("AuthManager", "beginSignIn failed", e)
                onFailure(e) // 🔹 onFailure 콜백 호출
            }
    }

    fun handleSignInResult(
        data: Intent?,
        onSuccess: (isNewUser: Boolean, uid: String, email: String?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val credential = signInClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken ?: throw Exception("No ID token")
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false
                            Log.d("AuthManager", "Firebase login success: ${user.uid}, isNewUser=$isNewUser")
                            onSuccess(isNewUser, user.uid, user.email)
                        } else {
                            onFailure(Exception("No Firebase user"))
                        }
                    } else {
                        onFailure(task.exception ?: Exception("Login failed"))
                    }
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
