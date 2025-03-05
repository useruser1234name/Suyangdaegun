package com.ryh.suyangdaegun.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ChatGptService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .retryOnConnectionFailure(true) // ✅ 자동 재시도 기능 추가
        .build()

    private val url = "https://api.openai.com/v1/chat/completions"

    // ✅ Firebase Remote Config 인스턴스
    private val remoteConfig = FirebaseRemoteConfig.getInstance()

    // ✅ API 키 가져오기 함수
    private fun getApiKey(): String {
        val apiKey = remoteConfig.getString("OPENAI_API_KEY")
        Log.d("ChatGptService", "Firebase에서 가져온 API 키: $apiKey")
        return apiKey
    }

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ChatGptService", "✅ Firebase Remote Config 업데이트 성공")
                } else {
                    Log.e("ChatGptService", "🔥 Firebase Remote Config 업데이트 실패")
                }
            }
    }

    fun getTodayFortune(birthdate: String, birthtime: String, callback: (String) -> Unit) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            callback("⚠️ OpenAI API 키 없음. Firebase 설정 확인 필요.")
            return
        }

        val prompt = """
        🔹 오늘의 운세를 사주팔자를 기반으로 **명확한 구조**로 제공해주세요.

        💡 **필수 조건**  
        1️⃣ 각 운세는 다음 6가지 항목으로 제공해야 합니다.  
        - **요약:** 오늘 하루의 전반적인 분위기  
        - **직장운:** 직장 및 업무 관련 운세  
        - **연애운:** 연애 및 대인관계 운세  
        - **재물운:** 금전 흐름 및 투자, 소비 운세  
        - **건강운:** 건강 관련 조언  
        - **총운:** 오늘 하루를 어떻게 보내면 좋을지에 대한 종합적인 조언  

        📌 **사용자 정보**  
        - **생년월일:** $birthdate  
        - **태어난 시간:** $birthtime (한국 시간, GMT+9 기준)  

        ✅ 반드시 위 **6가지 항목을 포함하여** 응답해 주세요.  
    """.trimIndent()

        val messages = listOf(
            mapOf("role" to "system", "content" to "당신은 전문적인 한국 사주 해설가입니다."),
            mapOf("role" to "user", "content" to prompt)
        )

        getResponse(messages, callback, n = 1)
    }


    fun getResponse(messages: List<Map<String, String>>, callback: (String) -> Unit, n: Int = 3) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            callback("⚠️ OpenAI API 키 없음. Firebase 설정 확인 필요.")
            return
        }

        val json = JSONObject()
        json.put("model", "gpt-4-turbo")
        json.put("messages", JSONArray(messages))
        json.put("max_tokens", 1500) // ✅ 응답 길이 증가
        json.put("n", n)
        json.put("temperature", 0.7)

        val mediaType = "application/json".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ChatGptService", "🔥 GPT 요청 실패: ${e.message}")
                callback("GPT 요청 실패: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    Log.e("ChatGptService", "⚠️ OpenAI 응답이 비어 있음")
                    callback("GPT 응답이 없습니다. 다시 시도해주세요.")
                    return
                }
                Log.d("ChatGptService", "✅ OpenAI API 응답: $responseBody")

                try {
                    val jsonResponse = JSONObject(responseBody)
                    val choicesArray = jsonResponse.optJSONArray("choices") ?: JSONArray()
                    if (choicesArray.length() == 0) {
                        Log.e("ChatGptService", "⚠️ 'choices' 배열이 비어 있음")
                        callback("GPT 응답이 비어 있습니다.")
                        return
                    }

                    val responseText = choicesArray.optJSONObject(0)
                        ?.optJSONObject("message")
                        ?.optString("content", "")

                    if (responseText.isNullOrEmpty()) {
                        Log.e("ChatGptService", "⚠️ GPT 응답이 비어 있음")
                        callback("GPT 응답이 올바르지 않습니다. 다시 시도해주세요.")
                        return
                    }

                    // ✅ JSON 코드 블록(````json`````) 제거
                    var cleanedResponse = responseText.trim()
                    if (cleanedResponse.startsWith("```json")) {
                        cleanedResponse = cleanedResponse.removePrefix("```json").trim()
                    }
                    if (cleanedResponse.endsWith("```")) {
                        cleanedResponse = cleanedResponse.removeSuffix("```").trim()
                    }

                    // ✅ JSON 변환 없이 텍스트 그대로 반환
                    callback(cleanedResponse)
                } catch (e: Exception) {
                    Log.e("ChatGptService", "🔥 응답 처리 중 오류 발생: ${e.message}")
                    callback("GPT 응답을 처리하는 중 오류가 발생했습니다.")
                }
            }
        })
    }




    fun getMultipleResponses(
        chatHistory: List<Map<String, String>>,
        input: String,
        callback: (List<String>) -> Unit
    ) {
        val messages = chatHistory + mapOf("role" to "user", "content" to input)

        getResponse(messages, callback = { response ->
            val responses = response.split("\n").filter { it.isNotBlank() }
            callback(responses.take(3))  // ✅ callback이 정상적으로 전달되도록 수정
        })
    }

}

fun saveAnalysisToFirebase(userId: String, jsonResponse: JSONObject) {
    val firestore = FirebaseFirestore.getInstance()

    // 🔹 JSON을 Firestore에 저장 가능한 Map으로 변환
    val faceReadingMap = jsonObjectToMap(jsonResponse.getJSONObject("1")) // 🔹 관상 데이터
    val fortuneTellingMap = jsonObjectToMap(jsonResponse.getJSONObject("2")) // 🔹 사주 데이터

    val data = hashMapOf(
        "1" to faceReadingMap,  // 🔹 Firestore에서 "1" (관상) 데이터로 저장
        "2" to fortuneTellingMap,  // 🔹 Firestore에서 "2" (사주) 데이터로 저장
        "timestamp" to System.currentTimeMillis()
    )

    firestore.collection("users")
        .document(userId)
        .set(data, SetOptions.merge()) // 기존 데이터 유지하면서 병합
        .addOnSuccessListener {
            Log.d("ChatGptService", "✅ Firestore에 관상+사주 데이터 저장 완료")
        }
        .addOnFailureListener { e ->
            Log.e("ChatGptService", "🔥 Firestore 저장 실패: ${e.message}")
        }

}

// 🔹 JSON을 Map으로 변환하는 함수 추가
fun jsonObjectToMap(jsonObject: JSONObject): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    val keys = jsonObject.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val value = jsonObject.get(key)
        map[key] = if (value is JSONObject) {
            jsonObjectToMap(value)  // ✅ 중첩된 JSONObject도 변환
        } else {
            value  // ✅ 일반 데이터 그대로 저장
        }
    }
    return map
}
