package com.ryh.suyangdaegun

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MatchingScreen(navController: NavHostController) {
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val sentRequests = DummyRequestData.sentRequests.filter { it.senderUid == currentUserUid }
    val receivedRequests = DummyRequestData.receivedRequests.filter { it.receiverUid == currentUserUid }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("매칭 요청 내역", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("내가 보낸 요청", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(sentRequests) { request ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_1),
                        contentDescription = "보낸 요청 카드",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(request.receiverName, fontSize = 20.sp, color = androidx.compose.ui.graphics.Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("요청 보냄", color = androidx.compose.ui.graphics.Color.White)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Text("내게 온 요청", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(receivedRequests) { request ->
                var expanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (expanded) 180.dp else 120.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_1),
                        contentDescription = "받은 요청 카드",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(request.senderEmail, fontSize = 20.sp, color = androidx.compose.ui.graphics.Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        if (!expanded) {
                            Button(onClick = { expanded = true }) {
                                Text("요청 확인")
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {
                                    val chatRoomId =
                                        if (request.senderUid < request.receiverUid)
                                            "${request.senderUid}_${request.receiverUid}"
                                        else
                                            "${request.receiverUid}_${request.senderUid}"
                                    DummyRequestData.receivedRequests.remove(request)
                                    navController.navigate("chatting/$chatRoomId")
                                }) {
                                    Text("수락")
                                }
                                Button(onClick = { expanded = false }) {
                                    Text("거절")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
