package com.ryh.suyangdaegun

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationViewModel : ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // 사용자 정보 변수
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

    // 정보 설정 메서드
    fun setGender(newGender: String) {
        if (newGender.isNotBlank()) {
            gender = newGender
            Log.d("RegistrationViewModel", "성별 설정 완료: $gender") // 로그 추가
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

    // Firestore에 사용자 데이터 저장
    fun saveUserData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        if (uid == null) {
            onFailure(Exception("Firebase UID를 가져오지 못했습니다."))
            return
        }

        if (gender.isBlank() || nickname.isBlank() || birthdate.isBlank()) {
            onFailure(Exception("필수 정보가 누락되었습니다."))
            return
        }


        val userData = hashMapOf(
            "uid" to uid, // UID 추가
            "email" to auth.currentUser?.email.orEmpty(), // 이메일 추가
            "gender" to gender.ifEmpty { "미설정" },
            "nickname" to nickname.ifEmpty { "미설정" },
            "birthdate" to birthdate.ifEmpty { "미설정" },
            "birthtime" to birthtime.ifEmpty { "미설정" },
            "profilePicture" to profilePicture.ifEmpty { "미설정" },
            "interests" to if (interests.isNotEmpty()) interests else listOf("미설정")
        )

        db.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("RegistrationViewModel", "유저 데이터 저장 성공했습니다")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("RegistrationViewModel", "유저 데이터 저장 실패했습니다", it)
                onFailure(it)
            }
    }
}
