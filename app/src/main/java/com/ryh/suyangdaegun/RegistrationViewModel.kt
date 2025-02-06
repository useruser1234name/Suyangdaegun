package com.ryh.suyangdaegun.auth

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
    var profilePicture: String = ""
        private set
    var interests: List<String> = listOf()
        private set

    // 정보 설정 메서드
    fun setGender(newGender: String) {
        gender = newGender
    }

    fun setNickname(newNickname: String) {
        nickname = newNickname
    }

    fun setBirthdate(newBirthdate: String) {
        birthdate = newBirthdate
    }

    fun setProfilePicture(newProfilePicture: String) {
        profilePicture = newProfilePicture
    }

    fun setInterests(newInterests: List<String>) {
        interests = newInterests
    }

    // ✅ Firestore에 사용자 데이터 저장
    fun saveUserData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = auth.currentUser?.uid ?: return // ✅ UID 가져오기
        val userData = hashMapOf(
            "gender" to gender,
            "nickname" to nickname,
            "birthdate" to birthdate,
            "profilePicture" to profilePicture,
            "interests" to interests
        )

        db.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
