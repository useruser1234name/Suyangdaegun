package com.ryh.suyangdaegun

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


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

@Composable
fun AppNavigator(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("accession") {
            AccessionNavigator(navController = navController) // NavController 전달
        }
        composable("main") {
            MainScreen(
                onNavigateToMatching = { navController.navigate("matching") },
                onNavigateToChatList = { navController.navigate("chatList") },
                onNavigateToMyPage = { navController.navigate("myPage") }
            )
        }
    }
}






// 네비게이션 컨트롤러
@Composable
fun AccessionNavigator(navController: NavHostController) {
    val viewModel: RegistrationViewModel = viewModel()

    NavHost(
        navController = navController, // AppNavigator에서 전달받은 navController 사용
        startDestination = "gender"
    ) {
        composable("gender") {
            GenderStep(
                viewModel = viewModel,
                onNext = { navController.navigate("nickname") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("nickname") {
            NicknameStep(
                viewModel = viewModel,
                onNext = { navController.navigate("birthdate") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("birthdate") {
            BirthdateStep(
                viewModel = viewModel,
                onNext = { navController.navigate("profilePicture") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("profilePicture") {
            ProfilePictureStep(
                viewModel = viewModel,
                onNext = { navController.navigate("interests") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("interests") {
            InterestsStep(
                viewModel = viewModel,
                onNext = { navController.navigate("complete") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("complete") {
            CompleteStep(viewModel = viewModel, navController = navController)
        }
    }
}


