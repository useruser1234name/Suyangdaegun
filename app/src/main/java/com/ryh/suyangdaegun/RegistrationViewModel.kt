package com.ryh.suyangdaegun

import androidx.lifecycle.ViewModel

class RegistrationViewModel : ViewModel() {
    private var gender: String? = null
    private var nickname: String? = null
    private var birthdate: String? = null
    private var profilePicture: String? = null
    private var interests: List<String> = emptyList()

    fun setGender(value: String) { gender = value }
    fun setNickname(value: String) { nickname = value }
    fun setBirthdate(value: String) { birthdate = value }
    fun setProfilePicture(value: String) { profilePicture = value }
    fun setInterests(value: List<String>) { interests = value }
}
