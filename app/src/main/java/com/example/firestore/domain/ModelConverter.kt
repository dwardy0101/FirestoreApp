package com.example.firestore.domain

import com.example.firestore.ui.MappableToCommon
import com.example.firestore.ui.model.LetterModel
import com.example.firestore.ui.model.MergeModel
import com.example.firestore.ui.model.NumberModel

fun List<CommonModel>.toLetters(): List<LetterModel> {
    return this.map {
        LetterModel(id = it.id, text = it.text, status = it.status)
    }
}

fun List<CommonModel>.toNumbers(): List<NumberModel> {
    return this.map {
        NumberModel(id = it.id, text = it.text, status = it.status)
    }
}

fun List<CommonModel>.toMerge(): List<MergeModel> {
    return this.map {
        MergeModel(id = it.id, text = it.text, status = it.status)
    }
}

fun List<MappableToCommon>.toCommonModel(): List<CommonModel> {
    return this.map {
        CommonModel(id = it.id, text = it.text, status = it.status)
    }
}