package com.example.firestore.ui.model

import java.util.UUID

data class NumberModel(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val status: Int
)
