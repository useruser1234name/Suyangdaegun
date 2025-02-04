package com.ryh.suyangdaegun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
        Text("Main Screen")
        Button(onClick = onNavigateToMatching) {
            Text("Go to Matching")
        }
        Button(onClick = onNavigateToChatList) {
            Text("Go to Chat List")
        }
        Button(onClick = onNavigateToMyPage) {
            Text("Go to My Page")
        }
    }
}





@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login Screen
        composable("login") {
            LoginScreen(
                onNavigateToMain = { navController.navigate("main") }
            )
        }

        // Main Screen
        composable("main") {
            MainScreen(
                onNavigateToMatching = { navController.navigate("matching") },
                onNavigateToChatList = { navController.navigate("chatList") },
                onNavigateToMyPage = { navController.navigate("myPage") }
            )
        }

        // Matching Screen
        composable("matching") {
            MatchingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Chat List Screen
        composable("chatList") {
            ChatListScreen(
                onNavigateToChat = { navController.navigate("chat") }
            )
        }

        // Chat Screen
        composable("chat") {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // My Page Screen
        composable("myPage") {
            MyPageScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
