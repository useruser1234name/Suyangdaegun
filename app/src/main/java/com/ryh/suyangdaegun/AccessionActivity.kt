// AccessionActivity.kt
package com.ryh.suyangdaegun

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import com.ryh.suyangdaegun.auth.AuthManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AccessionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = intent.getStringExtra("uid")
        if (uid.isNullOrEmpty()) {
            Log.e("AccessionActivity", "UID가 전달되지 않음, 회원가입 화면을 닫지 않고 대기")
            return
        }
        Log.d("AccessionActivity", "회원가입 시작: uid=$uid")
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
    val viewModel: RegistrationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    // 등록 순서: gender → nickname → interests → birthdate → complete
    NavHost(navController = navController, startDestination = "gender") {
        composable("gender") { GenderStep(navController, viewModel) }
        composable("nickname") { NicknameStep(navController, viewModel) }
        composable("interests") { InterestsStep(navController, viewModel) }
        composable("birthdate") { BirthdateStep(navController, viewModel) }
        composable("complete") {
            CompleteStep(onComplete = { onComplete() }, viewModel = viewModel)
        }
    }
}

// 각 단계의 Composable 함수는 아래와 같이 구성합니다.

@Composable
fun GenderStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(150.dp))
        Text("안녕하세요 정보를 입력해주세요.", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Text("프로필을 만들고 바로 커뮤니케이션을 시작하는 데 도움이 되는 간단한 정보를 알려주세요.", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = {
                viewModel.setGender("남성")
                navController.navigate("nickname")
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F8))
        ) {
            Image(
                painter = painterResource(id = R.drawable.man),
                contentDescription = "남성 아이콘",
                modifier = Modifier.size(30.dp).padding(end = 8.dp)
            )
            Spacer(modifier = Modifier.width(110.dp))
            Text("남성입니다", color = Color.Black)
        }
        Spacer(modifier = Modifier.height(23.dp))
        Button(
            onClick = {
                viewModel.setGender("여성")
                navController.navigate("nickname")
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F8))
        ) {
            Image(
                painter = painterResource(id = R.drawable.woman),
                contentDescription = "여성 아이콘",
                modifier = Modifier.size(30.dp).padding(end = 8.dp)
            )
            Spacer(modifier = Modifier.width(110.dp))
            Text("여성입니다", color = Color.Black)
        }
    }
}

@Composable
fun NicknameStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    var nickname = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(150.dp))
        Text("이름을 입력해 주세요.", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Text("본명을 사용해보세요. 실제 이름이 사용자에게 더 매력적입니다.", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = nickname.value,
            onValueChange = { nickname.value = it },
            label = { Text("닉네임") },
            modifier = Modifier.fillMaxWidth().border(1.dp, Color.Gray, RoundedCornerShape(30.dp))
        )
        Spacer(modifier = Modifier.height(443.dp))
        Button(
            onClick = {
                if (nickname.value.isNotBlank()) {
                    viewModel.setNickname(nickname.value)
                    navController.navigate("interests")
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3A31))
        ) {
            Text("다음", fontSize = 28.sp)
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
                if (selectedInterests.contains(interest))
                    selectedInterests.remove(interest)
                else
                    selectedInterests.add(interest)
            }) {
                Text(interest)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (selectedInterests.isNotEmpty()) {
                    viewModel.setInterests(selectedInterests.toList())
                    navController.navigate("birthdate")
                }
            }
        ) {
            Text("다음")
        }
    }
}

@Composable
fun BirthdateStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    var birthdate = remember { mutableStateOf("") }
    var birthtime = remember { mutableStateOf("") }
    var selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    fun createImageFile(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    var tempImageUri = remember { mutableStateOf(createImageFile(context)) }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) selectedImageUri.value = uri
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri.value = tempImageUri.value
        } else {
            Toast.makeText(context, "사진 촬영 실패", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tempImageUri.value = createImageFile(context)
            takePictureLauncher.launch(tempImageUri.value)
        } else {
            Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("생년월일을 입력하세요", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = birthdate.value,
            onValueChange = { birthdate.value = it },
            label = { Text("YYYY-MM-DD") }
        )
        TextField(
            value = birthtime.value,
            onValueChange = { birthtime.value = it },
            label = { Text("HH:MM") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) {
                Text("갤러리에서 선택")
            }
            Button(onClick = {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    tempImageUri.value = createImageFile(context)
                    takePictureLauncher.launch(tempImageUri.value)
                } else {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }) {
                Text("사진 촬영")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (selectedImageUri.value != null) {
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri.value),
                contentDescription = "선택된 이미지",
                modifier = Modifier.height(150.dp)
            )
        } else {
            Text("선택된 사진이 없습니다.")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (birthdate.value.isNotBlank() && birthtime.value.isNotBlank() && selectedImageUri.value != null) {
                viewModel.setBirthdate(birthdate.value)
                viewModel.setBirthtime(birthtime.value)
                viewModel.setProfilePicture(selectedImageUri.value.toString())
                navController.navigate("complete")
            } else {
                Toast.makeText(context, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
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
