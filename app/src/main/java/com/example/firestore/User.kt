package com.example.firestore

import android.graphics.Color
import java.util.UUID
import kotlin.uuid.Uuid

data class User(
    val id: String = UUID.randomUUID().toString(),
    val status: Int = Color.GRAY
)
