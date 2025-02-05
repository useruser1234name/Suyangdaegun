package com.ryh.suyangdaegun

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(navController = rememberNavController()
            )
        }
    }

    private fun navigateToAccessionNavigator() {
        val intent = Intent(this, AccessionActivity::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
}

//    private fun googleSignIn() {
//        // 예제: Google 로그인 로직 구현
//        Toast.makeText(this, "Google 로그인 구현 필요", Toast.LENGTH_SHORT).show()
//        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(null, null))
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//                    navigateToMain()
//                } else {
//                    Toast.makeText(this, "Google 로그인 실패", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//    private fun kakaoSignIn() {
//        Toast.makeText(this, "Kakao 로그인 구현 필요", Toast.LENGTH_SHORT).show()
//        // Kakao 로그인 로직 추가
//        navigateToMain() // 성공 시 메인 화면 이동
//    }
//
//    private fun naverSignIn() {
//        Toast.makeText(this, "Naver 로그인 구현 필요", Toast.LENGTH_SHORT).show()
//        // Naver 로그인 로직 추가
//        navigateToMain() // 성공 시 메인 화면 이동
//    }
//
//    private fun navigateToMain() {
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
//    }
//}

@Composable
fun LoginScreen(navController: NavController)
 {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .fillMaxWidth(0.65f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "SUNYANG", fontSize = 50.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "새로운 인연", fontSize = 36.sp, fontWeight = FontWeight.SemiBold)
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.26f)
        ) {
            Button(
                onClick = { navController.navigate("accession") }, // 회원가입 네비게이션
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Text("Kakao 로그인")
            }

            Button(
                onClick = { navController.navigate("accession") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Text("Google 로그인")
            }

            Button(
                onClick = { navController.navigate("accession") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Text("Naver 로그인")
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("로딩 중...", modifier = Modifier.padding(16.dp))
    }
}
