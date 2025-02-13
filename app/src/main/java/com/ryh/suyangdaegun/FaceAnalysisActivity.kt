package com.ryh.suyangdaegun

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//원래는 관상, 사주 정보 호출하여 룸이나 firebase에 저장하려던 페이지
//비운의 작품
@Composable
fun FaceAnalysisScreen(navController: androidx.navigation.NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Face Analysis Screen")
    }
}
