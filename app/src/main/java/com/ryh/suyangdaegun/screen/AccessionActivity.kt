package com.ryh.suyangdaegun.screen

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import coil3.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.Text
import com.ryh.suyangdaegun.R
import com.ryh.suyangdaegun.model.ChatGptService
import com.ryh.suyangdaegun.model.RegistrationViewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import androidx.navigation.compose.NavHost
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit


//회원가입 플로우 여러 kt파일 만들기 귀찮아서 여기 몰아서 작성함
//무려 800줄짜리 코드이다
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
    navController: NavHostController,
    onComplete: () -> Unit
) {
    val viewModel: RegistrationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    NavHost(
        navController = navController,
        startDestination = "gender"
    ) {
        composable("gender") { GenderStep(navController, viewModel) }
        composable("nickname") { NicknameStep(navController, viewModel) }
        composable("interests") { InterestsStep(navController, viewModel) }
        composable("birthdate") { BirthdateStep(navController, viewModel) }

        composable("face_analysis") { FaceAnalysisScreen(navController) }

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

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back",
                modifier = Modifier.size(30.dp)
            )

            Box( // 🔹 중앙 정렬을 위한 Box 사용
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "사주정보 입력",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(30.dp))
        }
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFFFFA500))) {
                    append("STEP 1")
                }
                append(" /4") // 기본 색상 유지
            },
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("안녕하세요! 성별을 알려주세요.", fontWeight = FontWeight.Bold, fontSize = 24.sp)

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

                Text("남자", color = Color.Black)
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
                Text("여자", color = Color.Black)
            }
        }
    }
}

object UserState {
    var nickname by mutableStateOf("")
}

@Composable
fun NicknameStep(
    navController: androidx.navigation.NavHostController,
    viewModel: RegistrationViewModel
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back",
                modifier = Modifier.size(30.dp)
            )

            Box( // 🔹 중앙 정렬을 위한 Box 사용
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "사주정보 입력",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(30.dp))
        }

        Spacer(modifier = Modifier.height(50.dp))
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFFFFA500))) {
                    append("STEP 2")
                }
                append(" /4") // 기본 색상 유지
            },
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "어떤 이름으로\n불러드릴까요?",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = UserState.nickname,
            onValueChange = { UserState.nickname = it },
            label = { Text("닉네임") },
            modifier = Modifier
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (UserState.nickname.isNotBlank()) {
                    viewModel.setNickname(UserState.nickname)
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

@Composable  //현재는 취미만 저장 됨 사유 귀찮음 users class에 active, pet 추가 하면 저장 가능
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back",
                modifier = Modifier.size(30.dp)
            )

            Box( // 🔹 중앙 정렬을 위한 Box 사용
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "사주정보 입력",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(30.dp))
        }
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFFFFA500))) {
                    append("STEP 3")
                }
                append(" /4") // 기본 색상 유지
            },
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))
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
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val chatGptService = ChatGptService()

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
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back",
                modifier = Modifier.size(30.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("사주정보 입력", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(30.dp))
        }

        Spacer(modifier = Modifier.height(50.dp))
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFFFFA500))) { append("STEP 4") }
                append(" /4")
            },
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("생년월일, 태어난 시간을 입력해 주세요", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = birthdate,
            onValueChange = { birthdate = it },
            label = { Text("예시) 1974.02.13") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = birthtime,
            onValueChange = { birthtime = it },
            label = { Text("태어난시간 00:00") },
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
            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        tempImageUri = createImageFile(context)
                        takePictureLauncher.launch(tempImageUri)
                    } else {
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3A31))
            ) {
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

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = {
                    if (birthdate.isNotBlank() && birthtime.isNotBlank() && selectedImageUri != null) {
                        isLoading = true
                        uploadImageToCloudRun(
                            imageUri = selectedImageUri!!, // ✅ Uri 전달
                            birthdate = birthdate, // ✅ 생년월일
                            birthtime = birthtime, // ✅ 태어난 시간
                            userId = "사용자_UID_여기에", // ✅ Firestore 저장을 위한 UID 추가 (Firebase에서 가져오기)
                            navController = navController, // ✅ 네비게이션 컨트롤러 전달
                            chatGptService = chatGptService, // ✅ GPT 서비스 객체 전달
                            context = context // ✅ 컨텍스트 전달
                        )
                    } else {
                        Toast.makeText(context, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3A31))
            ) {
                Text("다음", fontSize = 28.sp, color = Color.White)
            }
        }
    }
}

fun uploadImageToCloudRun(
    imageUri: Uri,
    birthdate: String,
    birthtime: String,
    userId: String, // ✅ Firestore 저장을 위해 userId 추가
    navController: NavHostController,
    chatGptService: ChatGptService,
    context: Context
) {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // ✅ 연결 시간 30초로 증가
        .readTimeout(30, TimeUnit.SECONDS)    // ✅ 읽기 시간 30초로 증가
        .writeTimeout(30, TimeUnit.SECONDS)   // ✅ 쓰기 시간 30초로 증가
        .build()
    val cloudRunUrl = "https://face-analysis-service-1064499536859.asia-northeast3.run.app/predict"

    // ✅ 리사이징 후 Base64 변환
    val resizedImageByteArray = resizeImage(context, imageUri, maxSize = 1024)
    val base64Image = Base64.encodeToString(resizedImageByteArray, Base64.NO_WRAP)

    // JSON 요청 생성
    val jsonObject = JSONObject().apply {
        put("image", base64Image) // Google Cloud Run에 전송할 이미지 데이터
    }

    val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url(cloudRunUrl)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Upload", "Cloud Run API 호출 실패: ${e.message}")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResponse(call: Call, response: Response) {
            response.use { res ->
                if (!res.isSuccessful) {
                    Log.e("Upload", "서버 응답 오류: ${res.code}")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "서버 응답 오류: ${res.code}", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                val responseText = res.body?.string() ?: "{}" // 응답이 null이면 빈 JSON 사용
                try {
                    val faceAnalysisResult = JSONObject(responseText) // ✅ `JSONObject`로 유지
                    Handler(Looper.getMainLooper()).post {
                        chatGptService.getFaceReadingAndFortune(
                            userId,  // ✅ Firestore 저장을 위해 `userId` 전달
                            birthdate,
                            birthtime,
                            faceAnalysisResult // ✅ `JSONObject` 그대로 전달 (타입 일치)
                        ) { gptResponse ->

                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("face_analysis", faceAnalysisResult) // ✅ `JSONObject` 그대로 저장

                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("gpt_result", gptResponse)

                            navController.navigate("face_analysis") // 🔹 네비게이터 이동
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Upload", "JSON 파싱 오류: ${e.message}")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "데이터 처리 오류", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    })
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
                // 회원가입 필수 데이터가 제대로 저장되었는지 확인 후 저장 진행
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
        ) {
            Text("메인 화면으로 이동")
        }
    }
}

fun resizeImage(context: Context, imageUri: Uri, maxSize: Int = 512): ByteArray {
    val inputStream = context.contentResolver.openInputStream(imageUri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    val width = originalBitmap.width
    val height = originalBitmap.height
    val scale = maxSize.toFloat() / maxOf(width, height)

    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

    val byteArrayOutputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream) // ✅ JPEG 품질 50으로 낮춤

    Log.d("ResizeImage", "Resized image size: ${byteArrayOutputStream.size()} bytes")

    return byteArrayOutputStream.toByteArray()
}
