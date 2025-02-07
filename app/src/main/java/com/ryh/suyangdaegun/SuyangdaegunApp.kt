package com.ryh.suyangdaegun

import android.app.Application
import com.ryh.suyangdaegun.auth.AuthManager

class SuyangdaegunApp : Application() {
    lateinit var authManager: AuthManager

    override fun onCreate() {
        super.onCreate()
        authManager = AuthManager(this)
    }
}
