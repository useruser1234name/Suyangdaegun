package com.ryh.suyangdaegun

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MatchingScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Matching Screen")
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
    }
}
