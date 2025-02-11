package com.ryh.suyangdaegun

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ryh.suyangdaegun.auth.AuthManager

@Composable
fun AppNavigatorAuth(
    googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>,
    authManager: AuthManager,
    navController: NavHostController = rememberNavController()
) {
    // 시작 라우트는 "login"으로 설정 (로그인 화면)
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onGoogleSignInClick = {
                    authManager.signInWithGoogle(
                        launcher = googleSignInLauncher,
                        onFailure = { e ->
                            // 로그인 실패 시 로그 출력 및 사용자에게 안내
                            Log.e("AppNavigatorAuth", "Google 로그인 실패", e)
                        }
                    )
                },
                onKakaoSignInClick = {
                    // Kakao 로그인 구현 (필요 시)
                }
            )
        }
    }
}

@Composable
fun AppNavigatorMain(navController: NavHostController = rememberNavController()) {
    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModelStoreOwner found")
    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        NavHost(navController = navController, startDestination = "bottomNav") {
            composable("bottomNav") { BottomNavScreen(navController) } // ✅ 수정된 BottomNavScreen 사용
            composable("chatting/{chatRoomId}") { backStackEntry ->
                val chatRoomId = backStackEntry.arguments?.getString("chatRoomId") ?: "default"
                val chatViewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = ChatViewModelFactory(chatRoomId)
                )
                ChattingScreen(navController, chatViewModel)
            }
            composable("matching") { MatchingScreen(navController) } // ✅ MatchingScreen도 navController 전달
        }
    }
}
