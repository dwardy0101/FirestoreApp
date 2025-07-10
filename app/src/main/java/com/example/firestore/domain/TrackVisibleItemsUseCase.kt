package com.example.firestore.domain

import com.example.firestore.data.FirestoreRepository
import kotlin.collections.map

class TrackVisibleItemsUseCase(
    private val firestoreRepository: FirestoreRepository
) {

    val updatesFlow = firestoreRepository.updateFlow

    suspend fun trackVisibleItems(collection: String, range: IntRange, items: List<CommonModel>) {
        if (firestoreRepository.listeners.isNotEmpty())  {
            // remove listener
            val ids = items.subList(range.first, range.last).map { item -> item.id }
            firestoreRepository.removeListener(ids)
        }

        if (!range.isEmpty()) {
            // fetch and add listener
            for (i in range.first..range.last) {
                firestoreRepository.fetchData(items[i].id, collection)
                if (!firestoreRepository.isPresent(items[i].id)) {
                    firestoreRepository.addListener(items[i].id, collection)
                }
            }
        }
    }

    fun randomizeStatus(collection: String, id: String) {
        firestoreRepository.randomizeStatus(collection, id)
    }
}
