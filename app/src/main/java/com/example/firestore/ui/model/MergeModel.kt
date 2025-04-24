package com.example.firestore.ui.model

import java.util.UUID

data class MergeModel(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val status: Int
)
