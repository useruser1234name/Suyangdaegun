package com.ryh.suyangdaegun.model

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestoreDb = FirebaseFirestore.getInstance().collection("users")

    private var nickname: String = ""
    private var birthdate: String = ""
    private var birthtime: String = ""
    private var gender: String = ""
    private var email: String = auth.currentUser?.email ?: ""
    private var interests: List<String> = emptyList()
    private var profilePictureUrl: String = ""

    fun setNickname(value: String) { nickname = value }
    fun setBirthdate(value: String) { birthdate = value }
    fun setBirthtime(value: String) { birthtime = value }
    fun setGender(value: String) { gender = value }
    fun setInterests(value: List<String>) { interests = value }
    fun setProfilePicture(value: String) { profilePictureUrl = value }

    fun getBirthdate(): String = birthdate
    fun getBirthtime(): String = birthtime
    fun getProfilePicture(): String? = profilePictureUrl

    fun saveUserData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onFailure(Exception("User not authenticated"))

        if (nickname.isBlank() || birthdate.isBlank() || birthtime.isBlank() || gender.isBlank() || email.isBlank() || interests.isEmpty()) {
            return onFailure(Exception("회원가입 필수 정보 누락"))
        }

        val userData = mapOf(
            "uid" to uid,
            "nickname" to nickname,
            "birthdate" to birthdate,
            "birthtime" to birthtime,
            "gender" to gender,
            "email" to email,
            "interests" to interests,
            "profilePicture" to profilePictureUrl
        )

        firestoreDb.document(uid).set(userData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
