package com.ryh.suyangdaegun

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AppNavigator(navController: NavHostController = rememberNavController()) {
    val viewModel: RegistrationViewModel = viewModel() // RegistrationViewModel 생성

    CompositionLocalProvider(LocalViewModelStoreOwner provides LocalViewModelStoreOwner.current!!) {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") { LoginScreen(navController) }
            composable("main") { MainScreen(navController) }
            composable("matching") { MatchingScreen(navController) }
            composable("chatList") { ChatListScreen(navController) }
            composable("chatting") { ChattingScreen(navController) }
            composable("myPage") { MyPageScreen(navController) }
            composable("faceAnalysis") { FaceAnalysisScreen(navController) }

            // 회원가입 관련 경로
            composable("gender") { GenderStep(navController, viewModel) }
            composable("nickname") { NicknameStep(navController, viewModel) }
            composable("birthdate") { BirthdateStep(navController, viewModel) }
            composable("profilePicture") { ProfilePictureStep(navController, viewModel) }
            composable("interests") { InterestsStep(navController, viewModel) }
            composable("complete") { CompleteStep(navController, viewModel) }
        }
    }
}