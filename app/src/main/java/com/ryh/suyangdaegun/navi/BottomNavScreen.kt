package com.ryh.suyangdaegun.navi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.ryh.suyangdaegun.screen.ChatListScreen
import com.ryh.suyangdaegun.screen.MainScreen
import com.ryh.suyangdaegun.screen.MatchingScreen
import com.ryh.suyangdaegun.screen.MyPageScreen
import com.ryh.suyangdaegun.R

@Composable
fun BottomNavScreen(navController: NavHostController) {
    val localNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White, // 네비게이션 바 배경색 유지
                modifier = Modifier.height(80.dp) // 네비게이션 바 높이 유지
            ) {
                val navBackStackEntry by localNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val items = listOf(
                    Triple("mainScreen", R.drawable.ic_home, R.drawable.ic_home1),
                    Triple("matching", R.drawable.ic_heart, R.drawable.ic_heart1),
                    Triple("chatList", R.drawable.ic_chat, R.drawable.ic_chat1),
                    Triple("myPage", R.drawable.ic_me, R.drawable.ic_me1)
                )

                val labels = listOf("홈", "인연", "채팅", "내 정보")

                items.forEachIndexed { index, (route, icon, selectedIcon) ->
                    val isSelected = currentRoute == route

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            localNavController.navigate(route) {
                                popUpTo(localNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 56.dp else 48.dp) // 선택된 버튼 크기 조정
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFFE75480) else Color.Transparent), // 선택된 버튼 배경색 적용
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Image(
                                        painter = painterResource(id = if (isSelected) selectedIcon else icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(34.dp)
                                    )
                                    if (isSelected) { // ✅ 선택된 버튼만 텍스트 표시
                                        Text(
                                            text = labels[index],
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        },
                        label = { if (!isSelected) Text(labels[index], color = Color.Gray) }, // ✅ 선택되지 않은 버튼만 label 표시
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent // ✅ 기본 선택 효과 제거
                        )
                    )
                }
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
