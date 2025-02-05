package com.ryh.suyangdaegun

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.provider.MediaStore
import android.telecom.Call
import android.util.Base64
import android.util.Log
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
import com.google.android.gms.common.api.Response
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

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
    val viewModel: RegistrationViewModel = remember { RegistrationViewModel() }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "gender"
    ) {
        composable("gender") { GenderStep(viewModel = viewModel, onNext = { navController.navigate("nickname") }) }
        composable("nickname") { NicknameStep(viewModel = viewModel, onNext = { navController.navigate("birthdate") }) }
        composable("birthdate") { BirthdateStep(viewModel = viewModel, onNext = { navController.navigate("profilePicture") }) }
        composable("profilePicture") { ProfilePictureStep(viewModel = viewModel, onNext = { navController.navigate("interests") }) }
        composable("interests") { InterestsStep(viewModel = viewModel, onNext = { navController.navigate("complete") }) }
        composable("complete") { CompleteStep(viewModel = viewModel, onNext = {}) }
    }
}



// 성별 선택 화면
@Composable
fun GenderStep(viewModel: RegistrationViewModel, onNext: () -> Unit) {
    var selectedGender by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        NavigationBar { }
        StandardSpacer(0.07f)
        TitleSection(
            "안녕하세요! 정보를 입력해주세요", "프로필을 만들고" +
                    "바로 커뮤니케이션을 시작하는 데 도움이 되는 간단한 정보를 알려주세요."
        )
        StandardSpacer(0.06f)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { selectedGender = "남성" }) { Text("남성") }
            Button(onClick = { selectedGender = "여성" }) { Text("여성") }
        }

        StandardSpacer(0.48f)
        Button(onClick = {
            viewModel.gender = selectedGender
            onNext()
        }, enabled = selectedGender.isNotEmpty()) {
            Text("다음")
        }
    }
}

// 닉네임 입력 화면
@Composable
fun NicknameStep(viewModel: RegistrationViewModel, onNext: () -> Unit) {
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
        Button(onClick = {
            viewModel.nickname = nickname
            onNext()
        }, enabled = nickname.isNotEmpty()) {
            Text("다음")
        }
    }
}

// 생년월일 입력 화면
@Composable
fun BirthdateStep(viewModel: RegistrationViewModel, onNext: () -> Unit) {
    var birthdate by remember { mutableStateOf("") }
    var birthtime by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        NavigationBar { }
        StandardSpacer(0.07f)
        TitleSection("${viewModel.nickname}님 생년월일과 출생 시간을 입력해주세요", "나중에 변경할 수 없습니다.")

        OutlinedTextField(
            value = birthdate,
            onValueChange = { birthdate = it },
            label = { Text("생년월일") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = birthtime,
            onValueChange = { birthtime = it },
            label = { Text("출생 시간") },
            modifier = Modifier.fillMaxWidth()
        )

        StandardSpacer(0.6f)

        Button(onClick = {
            viewModel.birthdate = birthdate
            viewModel.birthtime = birthtime
            onNext()
        }, enabled = birthdate.isNotEmpty() && birthtime.isNotEmpty()) {
            Text("다음")
        }
    }
}

// 관심사 선택 화면
@Composable
fun InterestsStep(viewModel: RegistrationViewModel, onNext: () -> Unit) {
    val allInterests = listOf("독서", "여행", "운동", "음악", "요리")
    var selectedInterests by remember { mutableStateOf(listOf<String>()) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        NavigationBar { }
        StandardSpacer(0.07f)
        TitleSection("${viewModel.nickname} 관심사를 선택해주세요", "")

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

        Button(onClick = {
            viewModel.interests = selectedInterests
            onNext()
        }, enabled = selectedInterests.isNotEmpty()) {
            Text("다음")
        }
    }
}

// 프로필 사진 업로드 화면
@Composable
fun ProfilePictureStep(viewModel: RegistrationViewModel, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("프로필 사진을 업로드하세요")

        // TODO: 사진 업로드 기능 추가 (갤러리에서 선택)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onNext() }) {
            Text("다음")
        }
    }
}

@Composable
fun SajuAnalysisStep(viewModel: RegistrationViewModel, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("관상 및 사주 분석 진행 중...")

        // TODO: Flask 서버 요청 후 결과 표시

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.sajuAnalysis = "GPT 분석 결과..."
            onNext()
        }) {
            Text("다음")
        }
    }
}


// 회원가입 완료 화면
@Composable
fun CompleteStep(viewModel: RegistrationViewModel, onNext: () -> Unit) {
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

