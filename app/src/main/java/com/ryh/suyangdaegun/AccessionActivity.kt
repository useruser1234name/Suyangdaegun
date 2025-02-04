package com.ryh.suyangdaegun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

// 네비게이션 컨트롤러
@Composable
fun AccessionNavigator() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "gender"
    ) {
        composable("gender") { GenderStep { navController.navigate("nickname") } }
        composable("nickname") { NicknameStep { navController.navigate("birthdate") } }
        composable("birthdate") { BirthdateStep { navController.navigate("profilePicture") } }
        composable("profilePicture") { ProfilePictureStep { navController.navigate("interests") } }
        composable("interests") { InterestsStep { navController.navigate("complete") } }
        composable("complete") { CompleteStep() }
    }
}



// 성별 선택 화면
@Composable
fun GenderStep(onNext: () -> Unit) {
    var selectedGender by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        NavigationBar { }
        StandardSpacer(0.07f)
        TitleSection("안녕하세요!", "프로필을 만들기 위해 간단한 정보를 입력해주세요.")
        StandardSpacer(0.06f)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { selectedGender = "남성" }) { Text("남성") }
            Button(onClick = { selectedGender = "여성" }) { Text("여성") }
        }

        StandardSpacer(0.48f)
        NextButton(onNext = onNext, enabled = selectedGender.isNotEmpty())
    }
}

// 닉네임 입력 화면
@Composable
fun NicknameStep(onNext: () -> Unit) {
    var nickname by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        NavigationBar { }
        StandardSpacer(0.07f)
        TitleSection("이름을 설정해주세요", "본명을 사용하면 더 신뢰받을 수 있어요.")

        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("이름") },
            modifier = Modifier.fillMaxWidth()
        )

        StandardSpacer(0.6f)
        NextButton(onNext = onNext, enabled = nickname.isNotEmpty())
    }
}

// 생년월일 입력 화면
@Composable
fun BirthdateStep(onNext: () -> Unit) {
    var birthdate by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        NavigationBar { }
        StandardSpacer(0.07f)
        TitleSection("생년월일을 입력해주세요", "나중에 변경할 수 없습니다.")

        OutlinedTextField(
            value = birthdate,
            onValueChange = { birthdate = it },
            label = { Text("YYYY-MM-DD") },
            modifier = Modifier.fillMaxWidth()
        )

        StandardSpacer(0.6f)
        NextButton(onNext = onNext, enabled = birthdate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }
}

// 관심사 선택 화면
@Composable
fun InterestsStep(onNext: () -> Unit) {
    var selectedInterests by remember { mutableStateOf(listOf<String>()) }
    val allInterests = listOf("독서", "여행", "운동", "음악", "영화")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        NavigationBar { }
        StandardSpacer(0.07f)
        TitleSection("관심사를 선택해주세요", "좋아하는 활동을 선택하세요.")

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

        StandardSpacer(0.4f)
        NextButton(onNext = onNext, enabled = selectedInterests.isNotEmpty())
    }
}

// 프로필 사진 업로드 화면
@Composable
fun ProfilePictureStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("프로필 사진을 업로드해주세요")
        Spacer(modifier = Modifier.padding(16.dp))
        Text("사진 업로드 기능은 여기에 구현됩니다.")
        StandardSpacer(0.2f)
        NextButton(onNext = onNext)
    }
}

// 회원가입 완료 화면
@Composable
fun CompleteStep() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("회원가입이 완료되었습니다!")
        Spacer(modifier = Modifier.padding(16.dp))
        NextButton(onNext = { /* 메인 화면으로 이동 */ })
    }
}

// 공통 네비게이션 바
@Composable
fun NavigationBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(0.14f)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
        }
        Divider(
            thickness = 1.dp,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(0.86f)
        )
    }
}

// 공통 타이틀 텍스트
@Composable
fun TitleSection(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(title)
        Text(subtitle)
    }
}

// 공통 Spacer
@Composable
fun StandardSpacer(heightFraction: Float) {
    Spacer(modifier = Modifier.fillMaxHeight(heightFraction))
}

// 공통 Next 버튼
@Composable
fun NextButton(onNext: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onNext,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("다음")
    }
}