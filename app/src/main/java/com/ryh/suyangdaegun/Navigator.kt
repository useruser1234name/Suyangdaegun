// AppNavigator.kt
package com.ryh.suyangdaegun

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.ryh.suyangdaegun.auth.AuthManager

@Composable
fun AppNavigator(
    googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>,
    authManager: AuthManager,
    navController: androidx.navigation.NavHostController = rememberNavController()
) {
    val registrationViewModel: RegistrationViewModel = viewModel()

    CompositionLocalProvider(LocalViewModelStoreOwner provides LocalViewModelStoreOwner.current!!) {
        NavHost(navController = navController, startDestination = "auth") {
            // 인증 플로우
            navigation(startDestination = "login", route = "auth") {
                composable("login") {
                    LoginScreen(
                        onGoogleSignInClick = {
                            authManager.signInWithGoogle(
                                launcher = googleSignInLauncher,
                                onFailure = { e -> Log.e("LoginScreen", "Google 로그인 실패", e) }
                            )
                        },
                        onKakaoSignInClick = {
                            // 예시로 카카오 로그인 시 바로 메인으로 이동
                            navController.navigate("main") { popUpTo("auth") { inclusive = true } }
                        }
                    )
                }
                composable("gender") { GenderStep(navController, registrationViewModel) }
                composable("nickname") { NicknameStep(navController, registrationViewModel) }
                composable("interests") { InterestsStep(navController, registrationViewModel) }
                composable("birthdate") { BirthdateStep(navController, registrationViewModel) }
                composable("complete") {
                    CompleteStep(
                        onComplete = { navController.navigate("main") { popUpTo("auth") { inclusive = true } } },
                        viewModel = registrationViewModel
                    )
                }
            }
            // 메인 플로우 (하단 네비게이션 포함)
            navigation(startDestination = "bottomNav", route = "main") {
                composable("bottomNav") { BottomNavScreen(rootNavController = navController) }
                composable("chatting/{chatRoomId}") { backStackEntry ->
                    val chatRoomId = backStackEntry.arguments?.getString("chatRoomId") ?: "default"
                    val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(chatRoomId))
                    ChattingScreen(navController, chatViewModel)
                }
                composable("faceAnalysis") { FaceAnalysisScreen(navController) }
            }
        }
    }
}
