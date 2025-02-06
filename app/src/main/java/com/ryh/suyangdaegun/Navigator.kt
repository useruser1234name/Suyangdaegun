package com.ryh.suyangdaegun

import LoginScreen
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ryh.suyangdaegun.auth.AuthManager
import com.ryh.suyangdaegun.auth.RegistrationViewModel

@Composable
fun AppNavigator(
    googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>,
    authManager: AuthManager,
    navController: NavHostController = rememberNavController()
) {
    val viewModel: RegistrationViewModel = viewModel()

    CompositionLocalProvider(LocalViewModelStoreOwner provides LocalViewModelStoreOwner.current!!) {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") {
                LoginScreen(
                    onGoogleSignInClick = {
                        authManager.signInWithGoogle(
                            launcher = googleSignInLauncher,
                            onFailure = { e -> Log.e("LoginScreen", "Google 로그인 실패", e) }
                        )
                    },
                    onKakaoSignInClick = {
                        // ✅ 카카오 로그인 처리 후 바로 메인 화면으로 이동
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }


            composable("main") { MainScreen(navController) }
            composable("matching") { MatchingScreen(navController) }
            composable("chatList") { ChatListScreen(navController) }
            composable("chatting") { ChattingScreen(navController) }
            composable("myPage") { MyPageScreen(navController) }
            composable("faceAnalysis") { FaceAnalysisScreen(navController) }

            composable("gender") { GenderStep(navController, viewModel) }
            composable("nickname") { NicknameStep(navController, viewModel) }
            composable("birthdate") { BirthdateStep(navController, viewModel) }
            composable("profilePicture") { ProfilePictureStep(navController, viewModel) }
            composable("interests") { InterestsStep(navController, viewModel) }

            composable("complete") {
                CompleteStep(
                    onComplete = { navController.navigate("main") { popUpTo("login") { inclusive = true } } },
                    viewModel = viewModel
                )
            }
        }
    }
}


