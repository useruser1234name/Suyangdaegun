package com.ryh.suyangdaegun

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.ryh.suyangdaegun.auth.AuthManager


class SuyangdaegunApp : Application() {
    lateinit var authManager: AuthManager
    override fun onCreate() {
        super.onCreate()

        // 🔹 Firebase 초기화 확인
        FirebaseApp.initializeApp(this)

        authManager = AuthManager(this)

        // 🔹 로그인 세션 확인 (앱 강제 종료 방지)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Log.d("SuyangdaegunApp", "User already logged in: ${currentUser.uid}")
        } else {
            Log.w("SuyangdaegunApp", "No user session found, user must log in")
        }
    }
}
