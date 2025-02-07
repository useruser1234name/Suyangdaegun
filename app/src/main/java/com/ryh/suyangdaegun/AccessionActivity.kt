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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.ryh.suyangdaegun.RegistrationViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest

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

        Button(
            onClick = {
                if (viewModel.gender.isBlank()) {
                    viewModel.setGender("남성") // 성별 설정
                }
                Log.d("GenderStep", "성별 저장 완료: ${viewModel.gender}")

                // 중복 네비게이션 방지
                if (navController.currentDestination?.route != "nickname") {
                    navController.navigate("nickname")
                }
            },
            modifier = Modifier
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

        Button(
            onClick = {
                if (viewModel.gender.isBlank()) {
                    viewModel.setGender("남성") // 성별 설정
                }
                Log.d("GenderStep", "성별 저장 완료: ${viewModel.gender}")

                // 중복 네비게이션 방지
                if (navController.currentDestination?.route != "nickname") {
                    navController.navigate("nickname")
                }
            },
            modifier = Modifier
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        Text("이름을 입력해 주세요.", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Text("본명을 사용해보세요 ", fontSize = 16.sp)
        Text("실제 이름이 사용자에게 더 매력적입니다.", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("닉네임") },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(30.dp)
                )
        )

        Spacer(modifier = Modifier.height(443.dp))

        Button(
            onClick = {
                if (nickname.isNotBlank()) {
                    viewModel.setNickname(nickname)
                    navController.navigate("birthdate")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D3A31)
            )
        ) {
            Text("다음", fontSize = 28.sp)
        }
    }
}



@Composable
fun BirthdateStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    var birthdate by remember { mutableStateOf("") }
    var birthtime by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // 사진 촬영용 임시 파일 생성 함수
    fun createImageFile(context: Context): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // 임시 저장 URI
    var tempImageUri by remember { mutableStateOf(createImageFile(context)) }

    // PhotoPicker 등록 (갤러리에서 사진 선택)
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    // 사진 촬영 Launcher 결과 처리
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = tempImageUri
        } else {
            Toast.makeText(context, "사진 촬영 실패", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tempImageUri = createImageFile(context) // 새로운 파일 생성
            takePictureLauncher.launch(tempImageUri) // 촬영 실행
        } else {
            Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }


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

        TextField(
            value = birthtime,
            onValueChange = { birthtime = it },
            label = { Text("TT-MM") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 사진 선택 버튼
        Spacer(modifier = Modifier.height(8.dp))

        // 사진 촬영 버튼
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // 갤러리에서 사진 선택
            Button(onClick = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) {
                Text("갤러리에서 선택")
            }

            // 카메라로 사진 촬영
            Button(onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                    tempImageUri = createImageFile(context) // 새로운 파일 생성
                    takePictureLauncher.launch(tempImageUri) // 촬영 실행
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA) // 권한 요청
                }
            }) {
                Text("사진 촬영")
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // 선택된 이미지 미리보기
        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Captured Image",
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
        } ?: Text("선택된 사진이 없습니다.", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (birthdate.isNotBlank() && selectedImageUri != null) {
                viewModel.setProfilePicture(selectedImageUri.toString())
                viewModel.setBirthdate(birthdate)
                navController.navigate("profilePicture")
            } else {
                Toast.makeText(context, "생년월일과 사진을 입력해주세요.", Toast.LENGTH_SHORT).show()
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
