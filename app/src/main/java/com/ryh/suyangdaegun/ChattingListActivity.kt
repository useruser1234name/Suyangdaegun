package com.ryh.suyangdaegun

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


@Preview(showBackground = true)
@Composable
fun ChatListScreenPreview() {
    ChatListScreen(navController = rememberNavController())
}

@Composable
fun ChatListScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
    ) {

        Text(
            "채팅",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 22.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Divider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("chatting") }) {
            Text("채팅 화면으로 이동")
        }
    }
}
