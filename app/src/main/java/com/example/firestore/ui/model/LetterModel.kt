package com.example.firestore.ui.model

import com.example.firestore.ui.MappableToCommon
import java.util.UUID

data class LetterModel(
    override val id: String = UUID.randomUUID().toString(),
    override val text: String = "",
    override var status: Int = 0
) : MappableToCommon
