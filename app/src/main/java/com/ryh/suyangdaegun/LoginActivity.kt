package com.ryh.suyangdaegun

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Login Screen")
        Button(onClick = onNavigateToMain) {
            Text("Go to Main")
        }
    }
}
