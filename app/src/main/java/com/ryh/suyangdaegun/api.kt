package com.ryh.suyangdaegun

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object ApiService {
    private val client = OkHttpClient()
    private const val BASE_URL = "http://127.0.0.1:5001"

    data class UserData(
        val birthdate: String = "",
        val email: String = "",
        val gender: String = "",
        val interests: List<String> = emptyList(),
        val nickname: String = "",
        val uid: String = ""
    )

    suspend fun sendUserData(userData: UserData): Result<Unit> = suspendCoroutine { continuation ->
        val json = JSONObject().apply {
            put("birthdate", userData.birthdate)
            put("email", userData.email)
            put("gender", userData.gender)
            put("interests", userData.interests)
            put("nickname", userData.nickname)
            put("uid", userData.uid)
        }

        val request = Request.Builder()
            .url("$BASE_URL/users")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resumeWithException(
                        IOException("서버 에러: ${response.code}")
                    )
                }
                response.close()
            }
        })
    }
}