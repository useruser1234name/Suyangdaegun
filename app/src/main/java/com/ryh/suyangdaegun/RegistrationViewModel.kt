package com.ryh.suyangdaegun

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // 회원가입 데이터
    var uid: String? = firebaseAuth.currentUser?.uid
    var gender: String = ""
    var nickname: String = ""
    var birthdate: String = ""
    var birthtime: String = ""
    var interests: List<String> = emptyList()
    var profilePictures: List<String> = emptyList() // 사진 URL 리스트
    var sajuAnalysis: String = ""

    // Firebase Firestore 저장
    fun saveUserDataToFirestore(onComplete: (Boolean) -> Unit) {
        uid?.let { userId ->
            val userData = hashMapOf(
                "uid" to userId,
                "gender" to gender,
                "nickname" to nickname,
                "birthdate" to birthdate,
                "birthtime" to birthtime,
                "interests" to interests,
                "profilePictures" to profilePictures,
                "sajuAnalysis" to sajuAnalysis
            )

            firestore.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }
}
