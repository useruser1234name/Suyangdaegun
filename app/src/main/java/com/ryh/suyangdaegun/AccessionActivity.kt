package com.ryh.suyangdaegun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class AccessionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccessionNavigator()
        }
    }
}

@Composable
fun AccessionNavigator() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "nickname"
    ) {
        composable("nickname") { NicknameStep { navController.navigate("birthdate") } }
        composable("birthdate") { BirthdateStep { navController.navigate("gender") } }
        composable("gender") { GenderStep { navController.navigate("profilePicture") } }
        composable("profilePicture") { ProfilePictureStep { navController.navigate("interests") } }
        composable("interests") { InterestsStep { navController.navigate("complete") } }
        composable("complete") { CompleteStep() }
    }
}

@Composable
fun NicknameStep(onNext: () -> Unit) {
    var nickname by remember { mutableStateOf("") }

    FullScreenStep(
        title = "닉네임을 입력해주세요",
        buttonEnabled = nickname.isNotEmpty(),
        onButtonClick = onNext
    ) {
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("닉네임") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BirthdateStep(onNext: () -> Unit) {
    var birthdate by remember { mutableStateOf("") }

    FullScreenStep(
        title = "생년월일을 입력해주세요",
        buttonEnabled = birthdate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")),
        onButtonClick = onNext
    ) {
        OutlinedTextField(
            value = birthdate,
            onValueChange = { birthdate = it },
            label = { Text("생년월일 (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun GenderStep(onNext: () -> Unit) {
    var selectedGender by remember { mutableStateOf("") }

    FullScreenStep(
        title = "성별을 선택해주세요",
        buttonEnabled = selectedGender.isNotEmpty(),
        onButtonClick = onNext
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { selectedGender = "남성" },
//                colors = ButtonDefaults.buttonColors(
//                    backgroundColor = if (selectedGender == "남성") MaterialTheme.colors.primary else MaterialTheme.colors.surface
//                )
            ) {
                Text("남성")
            }
            Button(
                onClick = { selectedGender = "여성" },
//                colors = ButtonDefaults.buttonColors(
//                    backgroundColor = if (selectedGender == "여성") MaterialTheme.colors.primary else MaterialTheme.colors.surface
//                )
            ) {
                Text("여성")
            }
        }
    }
}

@Composable
fun ProfilePictureStep(onNext: () -> Unit) {
    FullScreenStep(
        title = "프로필 사진을 업로드해주세요",
        buttonEnabled = true,
        onButtonClick = onNext
    ) {
        // 사진 업로드 UI
        Text("사진 업로드 기능은 여기에 구현됩니다.")
    }
}

@Composable
fun InterestsStep(onNext: () -> Unit) {
    var selectedInterests by remember { mutableStateOf(listOf<String>()) }
    val allInterests = listOf("독서", "여행", "운동", "음악", "영화")

    FullScreenStep(
        title = "관심사를 선택해주세요",
        buttonEnabled = selectedInterests.isNotEmpty(),
        onButtonClick = onNext
    ) {
        Column {
            allInterests.forEach { interest ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedInterests.contains(interest),
                        onCheckedChange = {
                            selectedInterests = if (selectedInterests.contains(interest)) {
                                selectedInterests - interest
                            } else {
                                selectedInterests + interest
                            }
                        }
                    )
                    Text(interest, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}
@Composable
fun CompleteStep() {
    FullScreenStep(
        title = "회원가입이 완료되었습니다!",
        buttonEnabled = true,
        onButtonClick = { /* Navigate to Main Screen */ }
    ) {
        Text("회원가입이 성공적으로 완료되었습니다.")
    }
}

@Composable
fun FullScreenStep(
    title: String,
    buttonEnabled: Boolean,
    onButtonClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = title, modifier = Modifier.padding(bottom = 16.dp))
            content()
        }
        Button(
            onClick = onButtonClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = buttonEnabled
        ) {
            Text("다음")
        }
    }
}
