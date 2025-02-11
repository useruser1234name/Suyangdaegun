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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AccessionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = intent.getStringExtra("uid")
        if (uid.isNullOrEmpty()) {
            Log.e("AccessionActivity", "No UID passed; waiting for signup")
            return
        }
        Log.d("AccessionActivity", "Signup started for uid=$uid")
        setContent {
            // NavHost를 선언하기 전에 LocalViewModelStoreOwner를 제공
            val navController = androidx.navigation.compose.rememberNavController()
            val owner = LocalViewModelStoreOwner.current ?: error("No ViewModelStoreOwner found")
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                AccessionNavGraph(uid, navController) { navigateToMain() }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}


@Composable
fun AccessionNavGraph(uid: String, navController: androidx.navigation.NavHostController, onComplete: () -> Unit) {
    val viewModel: RegistrationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    androidx.navigation.compose.NavHost(navController = navController, startDestination = "gender") {
        composable("gender") { GenderStep(navController, viewModel) }
        composable("nickname") { NicknameStep(navController, viewModel) }
        composable("interests") { InterestsStep(navController, viewModel) }
        composable("birthdate") { BirthdateStep(navController, viewModel) }
        composable("complete") { CompleteStep(onComplete = onComplete, viewModel = viewModel) }
    }
}



@Composable
fun GenderStep(navController: androidx.navigation.NavHostController, viewModel: RegistrationViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(150.dp))
        Text("안녕하세요! 정보를 입력해주세요.", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Text("프로필을 만들고 바로 커뮤니케이션을 시작하는 데 도움이 되는 간단한 정보를 알려주세요.", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
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
                    contentDescription = "man icon",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 8.dp)
                )
                Spacer(modifier = Modifier.width(110.dp))

                Text("남성입니다", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
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
                    contentDescription = "woman icon",
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
fun NicknameStep(navController: androidx.navigation.NavHostController, viewModel: RegistrationViewModel) {
    var nickname by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(150.dp))
        Text("이름을 입력해 주세요.", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Text("실제 이름 사용 권장", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("닉네임") },
            modifier = Modifier.fillMaxWidth().border(1.dp, Color.Gray, RoundedCornerShape(30.dp))
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (nickname.isNotBlank()) {
                    viewModel.setNickname(nickname)
                    navController.navigate("interests")
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3A31))
        ) {
            Text("다음", fontSize = 28.sp, color = Color.White)
        }
    }
}

@Composable
fun InterestsStep(navController: androidx.navigation.NavHostController, viewModel: RegistrationViewModel) {
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
            Button(
                onClick = {
                    if (selectedInterests.contains(interest))
                        selectedInterests.remove(interest)
                    else
                        selectedInterests.add(interest)
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            ) {
                Text(interest)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (selectedInterests.isNotEmpty()) {
                viewModel.setInterests(selectedInterests.toList())
                navController.navigate("birthdate")
            }
        }) {
            Text("다음")
        }
    }
}

@Composable
fun BirthdateStep(navController: androidx.navigation.NavHostController, viewModel: RegistrationViewModel) {
    var birthdate by remember { mutableStateOf("") }
    var birthtime by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    fun createImageFile(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
    var tempImageUri by remember { mutableStateOf(createImageFile(context)) }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) selectedImageUri = uri }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) selectedImageUri = tempImageUri
        else Toast.makeText(context, "사진 촬영 실패", Toast.LENGTH_SHORT).show()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tempImageUri = createImageFile(context)
            takePictureLauncher.launch(tempImageUri)
        } else {
            Toast.makeText(context, "카메라 권한 필요", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("출생년월일 및 시간 입력", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = birthdate,
            onValueChange = { birthdate = it },
            label = { Text("YYYY-MM-DD") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = birthtime,
            onValueChange = { birthtime = it },
            label = { Text("HH:MM") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                Text("갤러리 선택")
            }
            Button(onClick = {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                    tempImageUri = createImageFile(context)
                    takePictureLauncher.launch(tempImageUri)
                } else {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }) {
                Text("사진 촬영")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (selectedImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri),
                contentDescription = "선택된 이미지",
                modifier = Modifier.size(150.dp)
            )
        } else {
            Text("사진이 선택되지 않음")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (birthdate.isNotBlank() && birthtime.isNotBlank() && selectedImageUri != null) {
                viewModel.setBirthdate(birthdate)
                viewModel.setBirthtime(birthtime)
                viewModel.setProfilePicture(selectedImageUri.toString())
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
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("회원가입 완료!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // ✅ 회원가입 필수 데이터가 제대로 저장되었는지 확인 후 저장 진행
            viewModel.saveUserData(
                onSuccess = { onComplete() },
                onFailure = { e ->
                    Log.e("CompleteStep", "회원가입 저장 실패: ${e.message}")
                }
            )
        }) {
            Text("메인 화면으로 이동")
        }
    }
}
