package com.ryh.suyangdaegun.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

@Composable
fun FaceAnalysisScreen(navController: NavHostController) {
    var faceResult by remember { mutableStateOf("얼굴 분석 불러오는 중...") }
    var fortuneResult by remember { mutableStateOf("사주 분석 불러오는 중...") }
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // 🔹 Firestore에서 사용자 데이터 가져오기
    LaunchedEffect(Unit) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val faceReadingJson = document.get("1") as? Map<String, Any> // 🔹 "1" = 관상 데이터
                    val fortuneTellingJson = document.get("2") as? Map<String, Any> // 🔹 "2" = 사주 데이터

                    // 🔹 JSON 데이터를 보기 좋게 변환
                    faceResult = if (faceReadingJson != null) JSONObject(faceReadingJson).toString(4) else "관상 데이터 없음"
                    fortuneResult = if (fortuneTellingJson != null) JSONObject(fortuneTellingJson).toString(4) else "사주 데이터 없음"
                } else {
                    faceResult = "데이터 없음"
                    fortuneResult = "데이터 없음"
                }
            }
            .addOnFailureListener {
                faceResult = "불러오기 실패"
                fortuneResult = "불러오기 실패"
            }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔹 얼굴 분석 결과")
            Text(faceResult)

            Spacer(modifier = Modifier.height(16.dp))

            Text("🔹 사주 분석 결과")
            Text(fortuneResult)

            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
