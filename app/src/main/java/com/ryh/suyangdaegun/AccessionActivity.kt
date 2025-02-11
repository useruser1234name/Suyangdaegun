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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
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
fun AccessionNavGraph(
    uid: String,
    navController: androidx.navigation.NavHostController,
    onComplete: () -> Unit
) {
    val viewModel: RegistrationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = "gender"
    ) {
        composable("gender") { GenderStep(navController, viewModel) }
        composable("nickname") { NicknameStep(navController, viewModel) }
        composable("interests") { InterestsStep(navController, viewModel) }
        composable("birthdate") { BirthdateStep(navController, viewModel) }
        composable("complete") { CompleteStep(onComplete = onComplete, viewModel = viewModel) }
    }
}


@Composable
fun GenderStep(
    navController: androidx.navigation.NavHostController,
    viewModel: RegistrationViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding(),
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
fun NicknameStep(
    navController: androidx.navigation.NavHostController,
    viewModel: RegistrationViewModel
) {
    var nickname by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding(),
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
                .border(1.dp, Color.Gray, RoundedCornerShape(30.dp))
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (nickname.isNotBlank()) {
                    viewModel.setNickname(nickname)
                    navController.navigate("interests")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3A31))
        ) {
            Text("다음", fontSize = 28.sp, color = Color.White)
        }
    }
}

@Composable
fun InterestsStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    val interests = listOf("독서", "요리", "게임", "사진", "음악감상", "영화관람", "노래방", "연극", "춤")
    val active = listOf("런닝", "등산", "자전거", "테니스", "캠핑", "골프", "헬스", "클라이밍")
    val pet = listOf("고양이", "강아지", "파충류", "물고기", "새")

    val selectedInterests = remember { mutableStateListOf<String>() }
    val selectedactive = remember { mutableStateListOf<String>() }
    val selectedpet = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text("관심사를 선택해 주세요", fontWeight = FontWeight.Bold, fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))
        Text("취미", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            for (i in 0..2) {
                Row {
                    for (j in 0..2) {
                        val index = i * 3 + j
                        if (index < interests.size) {
                            Button(
                                onClick = {
                                    if (selectedInterests.contains(interests[index])) {
                                        selectedInterests.remove(interests[index])
                                    } else {
                                        selectedInterests.add(interests[index])
                                    }
                                },
                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                                shape = RoundedCornerShape(32.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedInterests.contains(interests[index]))
                                        Color(0xFFE8ECF4) else Color.White
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (selectedInterests.contains(interests[index]))
                                        Color(0xFF2D3A31) else Color.LightGray
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp
                                )
                            ) {
                                Text(
                                    text = interests[index],
                                    color = if (selectedInterests.contains(interests[index]))
                                        Color(0xFF2D3A31) else Color.Gray,
                                    style = TextStyle(fontSize = 14.sp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // active 부분
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("활동", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            for (i in 0..2) {
                Row {
                    for (j in 0..2) {
                        val index = i * 3 + j
                        if (index < active.size) {
                            Button(
                                onClick = {
                                    if (selectedactive.contains(active[index])) {
                                        selectedactive.remove(active[index])
                                    } else {
                                        selectedactive.add(active[index])
                                    }
                                },
                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                                shape = RoundedCornerShape(32.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedactive.contains(active[index]))
                                        Color(0xFFE8ECF4) else Color.White
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (selectedactive.contains(active[index]))
                                        Color(0xFF2D3A31) else Color.LightGray
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp
                                )
                            ) {
                                Text(
                                    text = active[index],
                                    color = if (selectedactive.contains(active[index]))
                                        Color(0xFF2D3A31) else Color.Gray,
                                    style = TextStyle(fontSize = 14.sp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // pet 부분
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("동물", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            for (i in 0..1) {
                Row {
                    for (j in 0..2) {
                        val index = i * 3 + j
                        if (index < pet.size) {
                            Button(
                                onClick = {
                                    if (selectedpet.contains(pet[index])) {
                                        selectedpet.remove(pet[index])
                                    } else {
                                        selectedpet.add(pet[index])
                                    }
                                },
                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                                shape = RoundedCornerShape(32.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedpet.contains(pet[index]))
                                        Color(0xFFE8ECF4) else Color.White
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (selectedpet.contains(pet[index]))
                                        Color(0xFF2D3A31) else Color.LightGray
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp
                                )
                            ) {
                                Text(
                                    text = pet[index],
                                    color = if (selectedpet.contains(pet[index]))
                                        Color(0xFF2D3A31) else Color.Gray,
                                    style = TextStyle(fontSize = 14.sp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.setInterests(selectedInterests)
                navController.navigate("birthdate")
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
fun BirthdateStep(
    navController: androidx.navigation.NavHostController,
    viewModel: RegistrationViewModel
) {
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("생년월일, 시간을 입력해 주세요", fontSize = 24.sp)
        Text("실제 정보를 입력해 주세요, 사주정보를 분석하는데 사용됩니다")
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = birthdate,
            onValueChange = { birthdate = it },
            label = { Text("예시) 19740213") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = birthtime,
            onValueChange = { birthtime = it },
            label = { Text("시간") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3A31))
            ) {
                Text("갤러리 선택")
            }
            Button(onClick = {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    tempImageUri = createImageFile(context)
                    takePictureLauncher.launch(tempImageUri)
                } else {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3A31))) {
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
            Text("사진이 선택되지 않았습니다")
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
            Text("다음", fontSize = 28.sp, color = Color.White)
        }
    }
}

@Composable
fun CompleteStep(onComplete: () -> Unit, viewModel: RegistrationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(150.dp))
        Text("회원가입 완료!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // ✅ 회원가입 필수 데이터가 제대로 저장되었는지 확인 후 저장 진행
                viewModel.saveUserData(
                    onSuccess = { onComplete() },
                    onFailure = { e ->
                        Log.e("CompleteStep", "회원가입 저장 실패: ${e.message}")
                    }
                )
            }, modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D3A31)
            )
        )  {
        Text("메인 화면으로 이동")
    }
    }
}
