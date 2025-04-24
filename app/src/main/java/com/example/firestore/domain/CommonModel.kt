package com.example.firestore.domain

import java.util.UUID

data class CommonModel(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val status: Int = 0
)