package com.ryh.suyangdaegun

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Divider
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
import com.ryh.suyangdaegun.auth.RegistrationViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

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
        verticalArrangement = Arrangement.Top,

        ) {
        Spacer(modifier = Modifier.height(150.dp))

        Text("이름을 설정해주세요.", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Text("본명을 사용해보세요- 실제 이름이 사용자에게 더 매력적입니다.", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("닉네임") }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,

        ) {
        Spacer(modifier = Modifier.height(150.dp))

        Text("--님, 생년월일을 입력해 주세요.", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Text("실제 정보를 입력해 주세요, 나중에 변경할 수 없습니다.")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = birthdate,
            onValueChange = { birthdate = it },
            label = { Text("YYYY-MM-DD-hour") }
        )

        Spacer(modifier = Modifier.height(443.dp))

        Button(
            onClick = {
                if (birthdate.isNotBlank()) {
                    viewModel.setBirthdate(birthdate)
                    navController.navigate("profilePicture")
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D3A31)
            )
        )
        {
            Text("다음", fontSize = 28.sp)
        }
    }
}

@Composable
fun ProfilePictureStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,

        ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text("사진을 추가할 차례에요", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Text("관상 분석 위한 사진을 등록할 차례입니다. 얼굴의 좌측1장, 전면1장,우측1장을 등록해주세요.", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(84.dp))
        Row() {
            Box(modifier = Modifier
                .clickable {/**/
                }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "plus icon",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 8.dp)
                )

            }
            Box(modifier = Modifier
                .clickable {/**/

                }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "plus icon",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 8.dp)
                )

            }
            Box(modifier = Modifier
                .clickable {/**/

                }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "plus icon",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 8.dp)
                )

            }
        }
        Spacer(modifier = Modifier.height(341.dp))
        Button(
            onClick = {
                viewModel.setProfilePicture("example_image_url") // 임시 URL
                navController.navigate("interests")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D3A31)
            )
        )
        {
            Text("다음", fontSize = 28.sp)
        }
    }
}

@Composable
fun InterestsStep(navController: NavHostController, viewModel: RegistrationViewModel) {
    val interests = listOf("독서", "요리", "게임", "사진", "음악감상", "영화관람", "노래방", "연극", "춤")
    val active = listOf("런닝", "등산", "자전거", "테니스", "캠핑", "골프", "헬스", "클라이밍")
    val pet = listOf("동물", "고양이", "강아지", "파충류", "물고기", "새")

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
                                }
                            ) {
                                Text(interests[index])
                            }
                        }
                    }
                }
            }
        }
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
                                }
                            ) {
                                Text(active[index])
                            }
                        }
                    }
                }
            }
        }

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
                                }
                            ) {
                                Text(pet[index])
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
                navController.navigate("complete")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D3A31)
            )
        )
        {
            Text("다음", fontSize = 28.sp)
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
        Text("회원가입 완료!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.saveUserData(
                onSuccess = { navController.navigate("main") },
                onFailure = { e -> Log.e("CompleteStep", "회원가입 저장 실패", e) }
            )
        }) {
            Text("메인 화면으로 이동")
        }
    }
}