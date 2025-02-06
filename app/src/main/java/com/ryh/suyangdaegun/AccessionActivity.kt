package com.ryh.suyangdaegun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ryh.suyangdaegun.auth.RegistrationViewModel

class AccessionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = intent.getStringExtra("uid") ?: ""

        setContent {
            val navController = rememberNavController()
            AccessionNavGraph(uid, navController) { navigateToMain() }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun AccessionNavGraph(uid: String, navController: NavHostController, onComplete: () -> Unit) {
    val viewModel: RegistrationViewModel = viewModel()

    NavHost(navController = navController, startDestination = "gender") {
        composable("gender") { GenderStep(navController, viewModel) }
        composable("nickname") { NicknameStep(navController, viewModel) }
        composable("birthdate") { BirthdateStep(navController, viewModel) }
        composable("profilePicture") { ProfilePictureStep(navController, viewModel) }
        composable("interests") { InterestsStep(navController, viewModel) }
        composable("complete") { CompleteStep(onComplete, viewModel) }
    }
}



@Composable
fun GenderStep(navController: NavHostController, viewModel: RegistrationViewModel) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,

        ) {
        Spacer(modifier = Modifier.height(150.dp))

        Text("안녕하세요 정보를 입력해주세요.", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Text("프로필을 만들고 바로 커뮤니케이션을 시작하는 데 도움이 되는 간단한 정보를 알려주세요.", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {
            viewModel.setGender("남성")
            navController.navigate("nickname")
        }, modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF5F5F8)
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.man),
                    contentDescription = "Kakao icon",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 8.dp)
                )
                Spacer(modifier = Modifier.width(110.dp))

                Text("남성입니다", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(23.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.setGender("여성")
            navController.navigate("nickname")
        }, modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF5F5F8)
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.woman),
                    contentDescription = "Kakao icon",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 8.dp)
                )
                Spacer(modifier = Modifier.width(110.dp))
                Text("여성입니다", color = Color.Black)
            }
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
            viewModel.setProfilePicture("example_image_url")
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
fun CompleteStep(onComplete: () -> Unit, viewModel: RegistrationViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("회원가입 완료!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.saveUserData(
                onSuccess = { onComplete() },
                onFailure = { e -> Log.e("CompleteStep", "회원가입 저장 실패", e) }
            )
        }) {
            Text("메인 화면으로 이동")
        }
    }
}
