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


//@Composable
//fun AppNavigator() {
//    val navController = rememberNavController()
//    val firebaseAuth = FirebaseAuth.getInstance()
//    val coroutineScope = rememberCoroutineScope()
//
//    // 로그인 상태 확인
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            delay(1500) // 로딩 화면 대체
//            if (firebaseAuth.currentUser != null) {
//                navController.navigate("main") {
//                    popUpTo("loading") { inclusive = true }
//                }
//            } else {
//                navController.navigate("accession") {
//                    popUpTo("loading") { inclusive = true }
//                }
//            }
//        }
//    }
//
//    NavHost(
//        navController = navController,
//        startDestination = "login"
//    ) {
//        composable("loading") { LoadingScreen() }
//        composable("login") {
//            LoginScreen(
//                onGoogleLogin = { navController.navigate("accession") },
//                onKakaoLogin = { navController.navigate("accession") },
//                onNaverLogin = { navController.navigate("accession") }
//            )
//        }
//        composable("main") {
//            MainScreen(
//                onNavigateToMatching = { navController.navigate("matching") },
//                onNavigateToChatList = { navController.navigate("chatList") },
//                onNavigateToMyPage = { navController.navigate("myPage") }
//            )
//        }
//        composable("accession") {
//            AccessionNavigator(navController = navController)
//        }
//        composable("matching") {
//            MatchingScreen(onNavigateBack = { navController.popBackStack() })
//        }
//        composable("chatList") {
//            ChatListScreen(onNavigateToChat = { navController.navigate("chat") })
//        }
//        composable("chat") {
//            ChatScreen(viewModel = ChatViewModel())
//        }
//        composable("myPage") {
//            MyPageScreen(onNavigateBack = { navController.popBackStack() })
//        }
//    }
//}

//@Composable
//fun AppNavigator(navController: NavHostController, startDestination: String) {
//    CompositionLocalProvider(LocalViewModelStoreOwner provides LocalViewModelStoreOwner.current!!) {
//        NavHost(navController = navController, startDestination = startDestination) {
//            composable("login") { LoginScreen(navController) }
//            composable("accession") { AccessionNavigator(navController) }
//            composable("main") { MainScreen(navController) }
//        }
//    }
//}



//// 네비게이션 컨트롤러
//@Composable
//fun AccessionNavigator(navController: NavHostController) {
//    val viewModel: RegistrationViewModel = viewModel()
//
//    NavHost(
//        navController = navController,
//        startDestination = "gender"
//    ) {
//        composable("gender") {
//            GenderStep(navController, viewModel)
//        }
//        composable("nickname") {
//            NicknameStep(navController, viewModel)
//        }
//        composable("birthdate") {
//            BirthdateStep(navController, viewModel)
//        }
//        composable("profilePicture") {
//            ProfilePictureStep(navController, viewModel)
//        }
//        composable("interests") {
//            InterestsStep(navController, viewModel)
//        }
//        composable("complete") {
//            CompleteStep(navController, viewModel)
//        }
//    }
//}
