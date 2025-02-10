// DummyRequestData.kt
package com.ryh.suyangdaegun

import androidx.compose.runtime.mutableStateListOf

object DummyRequestData {
    val sentRequests = mutableStateListOf<RequestEntry>()
    val receivedRequests = mutableStateListOf<RequestEntry>()
}
