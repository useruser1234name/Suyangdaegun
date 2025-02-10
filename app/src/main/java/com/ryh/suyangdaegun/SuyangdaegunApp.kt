// SuyangdaegunApp.kt
package com.ryh.suyangdaegun

import android.app.Application
import com.ryh.suyangdaegun.auth.AuthManager

class SuyangdaegunApp : Application() {
    lateinit var authManager: AuthManager

    override fun onCreate() {
        super.onCreate()
        // Firebase는 google-services.json 파일을 통해 자동 초기화됩니다.
        authManager = AuthManager(this)
    }
}
