package com.ryh.suyangdaegun

import androidx.compose.runtime.mutableStateListOf

data class RequestEntry(
    val senderUid: String = "",
    val senderEmail: String = "",
    val receiverUid: String = "",
    val receiverEmail: String = "",
    val receiverName: String = ""
)

object DummyRequestData {
    val sentRequests = mutableStateListOf<RequestEntry>()
    val receivedRequests = mutableStateListOf<RequestEntry>()
}
