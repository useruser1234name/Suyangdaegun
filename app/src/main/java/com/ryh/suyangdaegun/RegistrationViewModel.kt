// RegistrationViewModel.kt
package com.ryh.suyangdaegun

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    var gender: String = ""
        private set
    var nickname: String = ""
        private set
    var birthdate: String = ""
        private set
    var birthtime: String = ""
        private set
    var profilePicture: String = ""
        private set
    var interests: List<String> = listOf()
        private set

    fun setGender(newGender: String) {
        if (newGender.isNotBlank()) {
            gender = newGender
            Log.d("RegistrationViewModel", "성별 설정 완료: $gender")
        } else {
            Log.e("RegistrationViewModel", "성별 값이 비어 있습니다.")
        }
    }

    fun setNickname(newNickname: String) {
        if (newNickname.isNotBlank()) nickname = newNickname
    }

    fun setBirthdate(newBirthdate: String) {
        if (newBirthdate.isNotBlank()) birthdate = newBirthdate
    }

    fun setBirthtime(newBirthtime: String) {
        if (newBirthtime.isNotBlank()) birthtime = newBirthtime
    }

    fun setProfilePicture(newProfilePicture: String) {
        if (newProfilePicture.isNotBlank()) profilePicture = newProfilePicture
    }

    fun setInterests(newInterests: List<String>) {
        if (newInterests.isNotEmpty()) interests = newInterests
    }

    fun saveUserData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            onFailure(Exception("Firebase UID를 가져오지 못했습니다."))
            return
        }
        if (gender.isBlank() || nickname.isBlank() || birthdate.isBlank()) {
            onFailure(Exception("필수 정보가 누락되었습니다."))
            return
        }
        val userData = mapOf(
            "uid" to uid,
            "email" to auth.currentUser?.email.orEmpty(),
            "gender" to gender,
            "nickname" to nickname,
            "birthdate" to birthdate,
            "birthtime" to birthtime,
            "interests" to interests,
            "profilePicture" to profilePicture
        )
        database.getReference("users").child(uid)
            .setValue(userData)
            .addOnSuccessListener {
                Log.d("RegistrationViewModel", "유저 데이터 저장 성공")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("RegistrationViewModel", "유저 데이터 저장 실패", e)
                onFailure(e)
            }
    }
}
