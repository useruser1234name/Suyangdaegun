package com.ryh.suyangdaegun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigator()
        }
    }
}

@Composable
fun MainScreen(
    onNavigateToMatching: () -> Unit,
    onNavigateToChatList: () -> Unit,
    onNavigateToMyPage: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Main Screen", modifier = Modifier.padding(16.dp))
        Button(onClick = onNavigateToMatching, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text("Go to Matching")
        }
        Button(onClick = onNavigateToChatList, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text("Go to Chat List")
        }
        Button(onClick = onNavigateToMyPage, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text("Go to My Page")
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val firebaseAuth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()

    // 로그인 상태 확인
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            delay(1000) // 로딩 화면 대체
            if (firebaseAuth.currentUser != null) {
                navController.navigate("main") {
                    popUpTo("loading") { inclusive = true }
                }
            } else {
                navController.navigate("login") {
                    popUpTo("loading") { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "loading"
    ) {
        composable("loading") { LoadingScreen() }
        composable("login") {
            LoginScreen(
                onGoogleLogin = { /* Google 로그인 구현 */ },
                onKakaoLogin = { /* Kakao 로그인 구현 */ },
                onNaverLogin = { /* Naver 로그인 구현 */ }
            )
        }
        composable("main") {
            MainScreen(
                onNavigateToMatching = { navController.navigate("matching") },
                onNavigateToChatList = { navController.navigate("chatList") },
                onNavigateToMyPage = { navController.navigate("myPage") }
            )
        }
        composable("matching") {
            MatchingScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("chatList") {
            ChatListScreen(onNavigateToChat = { navController.navigate("chat") })
        }
        composable("chat") {
            ChatScreen(viewModel = ChatViewModel())
        }
        composable("myPage") {
            MyPageScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}