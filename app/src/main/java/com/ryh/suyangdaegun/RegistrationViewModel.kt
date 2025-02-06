package com.ryh.suyangdaegun

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegistrationViewModel : ViewModel() {
    var gender by mutableStateOf("")
        private set
    var nickname by mutableStateOf("")
        private set
    var birthdate by mutableStateOf("")
        private set
    var profilePicture by mutableStateOf("")
        private set
    var interests = mutableStateListOf<String>()

    fun setGender(value: String) { gender = value }
    fun setNickname(value: String) { nickname = value }
    fun setBirthdate(value: String) { birthdate = value }
    fun setProfilePicture(value: String) { profilePicture = value }
    fun setInterests(value: List<String>) { interests.clear(); interests.addAll(value) }
}
