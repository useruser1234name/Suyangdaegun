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
) {//시작 화면은 login이지만 자동 로그인 기능으로 한번 가입 후에는 바로 mainscreen으로 이동
    // 회원탈퇴 기능을 넣지 않았기때문에 앱 삭제 후
    // 파이어베이스에 들어가서 auth 할당 삭제 후 다시 login -> 회원가입 이동 가능
    // 시작 라우트는 "login"으로 설정 (로그인 화면)
    //탈퇴 로그아웃 기능 추가 필요 -> authmanager에 로직 추가
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
                    //원래는 ui구성 시 바로 메인액티비티로 이동하기 위해서 만든 기능 삭제 가능하지만 귀찮음
                }
            )
        }
    }
}

//솔직히 네비게이터는 봐도 봐도 모르겠다
@Composable
fun AppNavigatorMain(navController: NavHostController = rememberNavController()) {
    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModelStoreOwner found")
    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        NavHost(navController = navController, startDestination = "bottomNav") {
            composable("bottomNav") { BottomNavScreen(navController) } //  수정된 BottomNavScreen 사용
            composable("chatting/{chatRoomId}") { backStackEntry ->
                val chatRoomId = backStackEntry.arguments?.getString("chatRoomId") ?: "default"
                val chatViewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = ChatViewModelFactory(chatRoomId)
                )
                ChattingScreen(navController, chatViewModel)
            }
            composable("matching") { MatchingScreen(navController) } //  MatchingScreen도 navController 전달
        }
    }
}
