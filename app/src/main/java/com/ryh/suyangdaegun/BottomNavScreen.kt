package com.ryh.suyangdaegun

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp


@Composable
fun BottomNavScreen(navController: NavHostController) { // `navController` 추가
    val localNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by localNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = {
                        Image(
                            painter = painterResource(
                                id = if (currentRoute == "mainScreen") {
                                    R.drawable.ic_home1  // 선택됐을 때 이미지
                                } else {
                                    R.drawable.ic_home   // 선택되지 않았을 때 이미지
                                }
                            ),
                            contentDescription = "홈 아이콘",
                            modifier = Modifier.size(width = 34.dp, height = 32.dp)
                        )
                    },
                    label = { Text("홈") },
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
                    icon = {
                        Image(
                            painter = painterResource(
                                id = if (currentRoute == "matching") { // 조건 수정됨
                                    R.drawable.ic_heart1  // 선택됐을 때 이미지
                                } else {
                                    R.drawable.ic_heart   // 선택되지 않았을 때 이미지
                                }
                            ),
                            contentDescription = "인연 아이콘",
                            modifier = Modifier.size(width = 34.dp, height = 32.dp)
                        )
                    },
                    label = { Text("인연") },
                    selected = currentRoute == "matching",
                    onClick = {
                        localNavController.navigate("matching") { // `localNavController`로 변경
                            popUpTo(localNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Image(
                            painter = painterResource(
                                id = if (currentRoute == "chatList") { // 조건 수정됨
                                    R.drawable.ic_chat1  // 선택됐을 때 이미지
                                } else {
                                    R.drawable.ic_chat   // 선택되지 않았을 때 이미지
                                }
                            ),
                            contentDescription = "채팅 아이콘",
                            modifier = Modifier.size(width = 34.dp, height = 32.dp)
                        )
                    },
                    label = { Text("채팅") },
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
                    icon = {
                        Image(
                            painter = painterResource(
                                id = if (currentRoute == "myPage") { // 조건 수정됨
                                    R.drawable.ic_me1  // 선택됐을 때 이미지
                                } else {
                                    R.drawable.ic_me   // 선택되지 않았을 때 이미지
                                }
                            ),
                            contentDescription = "내 정보 아이콘",
                            modifier = Modifier.size(width = 34.dp, height = 32.dp)
                        )
                    },
                    label = { Text("내 정보") },
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
            composable("matching") { MatchingScreen(navController) }
            composable("chatList") { ChatListScreen(navController) }
            composable("myPage") { MyPageScreen(navController) }
        }
    }
}


