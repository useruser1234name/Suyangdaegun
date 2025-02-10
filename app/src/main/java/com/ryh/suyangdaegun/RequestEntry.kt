// RequestEntry.kt
package com.ryh.suyangdaegun

data class RequestEntry(
    val senderUid: String,
    val senderEmail: String,
    val receiverUid: String,
    val receiverEmail: String,
    val receiverName: String
)
