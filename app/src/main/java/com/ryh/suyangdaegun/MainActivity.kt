package com.ryh.suyangdaegun

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 메인 네비게이션 실행
        setContent { AppNavigatorMain() }
    }
}

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val viewModel: MatchingViewModel = viewModel()
    val recommendedUsers = listOf(
        "🎵 음악 좋아하는 사람",
        "🎨 예술 감성이 있는 사람",
        "📚 독서광",
        "🎮 게임 좋아하는 사람",
        "🌍 여행을 좋아하는 사람"
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("오늘의 추천", modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn {
            items(recommendedUsers) { title ->
                MatchUserCard(title, viewModel, rootNavController)
            }
        }
    }
}

@Composable
fun MatchUserCard(title: String, viewModel: MatchingViewModel, navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { showDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("매칭 요청") },
            text = { Text("이 사용자와 매칭 요청을 보낼까요?") },
            confirmButton = {
                Button(onClick = {
                    val targetEmail = "a01062946631@gmail.com"
                    viewModel.getUserUidByEmail(targetEmail) { targetUid ->
                        if (targetUid != null) {
                            viewModel.sendMatchRequestToFirestore(targetUid) { success ->
                                if (success) Log.d("Matching", "매칭 요청 성공!")
                                showDialog = false
                            }
                        } else {
                            Log.e("Matching", "사용자를 찾을 수 없습니다.")
                        }
                    }
                }) { Text("YES") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("NO") }
            }
        )
    }
}
