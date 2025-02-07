package com.ryh.suyangdaegun.auth

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.ryh.suyangdaegun.R
import com.ryh.suyangdaegun.SuyangdaegunApp

class AuthManager(private val activity: SuyangdaegunApp) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val signInClient: SignInClient = Identity.getSignInClient(activity)
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signInWithGoogle(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        onFailure: (Exception) -> Unit
    ) {
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
                launcher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun handleSignInResult(
        data: Intent?,
        onSuccess: (Boolean, String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val credential = signInClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken ?: throw Exception("ID 토큰 없음")
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            checkUserExists(user.uid, onSuccess, onFailure)
                        } else {
                            onFailure(Exception("Firebase 사용자 없음"))
                        }
                    } else {
                        onFailure(task.exception ?: Exception("로그인 실패"))
                    }
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }


    private fun checkUserExists(
        uid: String,
        onSuccess: (Boolean, String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (uid.isEmpty()) {
            onFailure(Exception("UID가 빈 값으로 전달됨"))
            return
        }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onSuccess(true, uid)
                } else {
                    onSuccess(false, uid)
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

}
