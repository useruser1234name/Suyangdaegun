package com.ryh.suyangdaegun

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

//@Composable
//fun AccessionScreen(navController: NavHostController) {
//    val viewModel: RegistrationViewModel = viewModel()
//
//    CompositionLocalProvider(LocalViewModelStoreOwner provides LocalViewModelStoreOwner.current!!) {
//        NavHost(
//            navController = navController,
//            startDestination = "gender"
//        ) {
//            composable("gender") { GenderStep(navController, viewModel) }
//            composable("nickname") { NicknameStep(navController, viewModel) }
//            composable("birthdate") { BirthdateStep(navController, viewModel) }
//            composable("profilePicture") { ProfilePictureStep(navController, viewModel) }
//            composable("interests") { InterestsStep(navController, viewModel) }
//            composable("complete") {
//                CompleteStep(navController, viewModel)
//            }
//        }
//    }
//}


@Composable
fun GenderStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("성별을 선택하세요", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.setGender("남성")
            navController.navigate("nickname")
        }) {
            Text("남성")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.setGender("여성")
            navController.navigate("nickname")
        }) {
            Text("여성")
        }
    }
}

@Composable
fun NicknameStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    var nickname by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("닉네임을 입력하세요", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("닉네임") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (nickname.isNotBlank()) {
                viewModel.setNickname(nickname)
                navController.navigate("birthdate")
            }
        }) {
            Text("다음")
        }
    }
}


@Composable
fun BirthdateStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    var birthdate by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("생년월일을 입력하세요", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = birthdate,
            onValueChange = { birthdate = it },
            label = { Text("YYYY-MM-DD") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (birthdate.isNotBlank()) {
                viewModel.setBirthdate(birthdate)
                navController.navigate("profilePicture")
            }
        }) {
            Text("다음")
        }
    }
}

@Composable
fun ProfilePictureStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("프로필 사진을 업로드하세요", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.setProfilePicture("example_image_url") // 임시 URL
            navController.navigate("interests")
        }) {
            Text("사진 업로드")
        }
    }
}

@Composable
fun InterestsStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    val interests = listOf("운동", "독서", "음악", "여행")
    val selectedInterests = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("관심사를 선택하세요", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        interests.forEach { interest ->
            Button(onClick = {
                if (selectedInterests.contains(interest)) {
                    selectedInterests.remove(interest)
                } else {
                    selectedInterests.add(interest)
                }
            }) {
                Text(interest)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.setInterests(selectedInterests)
            navController.navigate("complete")
        }) {
            Text("다음")
        }
    }
}

@Composable
fun CompleteStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("회원가입이 완료되었습니다!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // 최상위 NavController로 돌아가기
            navController.navigate("login") {
                popUpTo("gender") { inclusive = true }
            }
        }) {
            Text("로그인 화면으로 이동")
        }
    }
}

