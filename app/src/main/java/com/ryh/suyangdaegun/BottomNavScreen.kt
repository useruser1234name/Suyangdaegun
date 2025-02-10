// BottomNavScreen.kt
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
fun BottomNavScreen(rootNavController: NavHostController) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = { },
                    label = { Text("메인") },
                    selected = currentRoute == "mainScreen",
                    onClick = {
                        navController.navigate("mainScreen") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { },
                    label = { Text("매칭") },
                    selected = currentRoute == "matching",
                    onClick = {
                        navController.navigate("matching") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { },
                    label = { Text("채팅리스트") },
                    selected = currentRoute == "chatList",
                    onClick = {
                        navController.navigate("chatList") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { },
                    label = { Text("마이페이지") },
                    selected = currentRoute == "myPage",
                    onClick = {
                        navController.navigate("myPage") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "mainScreen",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("mainScreen") { MainScreen(rootNavController) }
            composable("matching") { MatchingScreen(rootNavController) }
            composable("chatList") { ChatListScreen(rootNavController) }
            composable("myPage") { MyPageScreen(rootNavController) }
        }
    }
}
