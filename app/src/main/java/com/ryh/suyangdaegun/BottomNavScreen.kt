package com.ryh.suyangdaegun

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavScreen(navController: NavHostController) { // ✅ `navController` 추가
    val localNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by localNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = { /* 아이콘 추가 가능 */ },
                    label = { Text("메인") },
                    selected = currentRoute == "mainScreen",
                    onClick = {
                        localNavController.navigate("mainScreen") {
                            popUpTo(localNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { /* 아이콘 추가 가능 */ },
                    label = { Text("매칭") },
                    selected = currentRoute == "matching",
                    onClick = {
                        localNavController.navigate("matching") { // ✅ `localNavController`로 변경
                            popUpTo(localNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { /* 아이콘 추가 가능 */ },
                    label = { Text("채팅리스트") },
                    selected = currentRoute == "chatList",
                    onClick = {
                        localNavController.navigate("chatList") {
                            popUpTo(localNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { /* 아이콘 추가 가능 */ },
                    label = { Text("마이페이지") },
                    selected = currentRoute == "myPage",
                    onClick = {
                        localNavController.navigate("myPage") {
                            popUpTo(localNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = localNavController,
            startDestination = "mainScreen",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("mainScreen") { MainScreen(navController) }
            composable("matching") { MatchingScreen(navController) } // ✅ `navController` 올바르게 전달
            composable("chatList") { ChatListScreen(navController) }
            composable("myPage") { MyPageScreen(navController) }
        }
    }
}
