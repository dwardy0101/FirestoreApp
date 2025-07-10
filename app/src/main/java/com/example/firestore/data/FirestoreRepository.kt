package com.example.firestore.data

import android.util.Log
import com.example.firestore.domain.CommonModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random
import kotlin.random.nextInt

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    private val _updatesFlow = MutableSharedFlow<CommonModel>(replay = 1)
    val updateFlow: SharedFlow<CommonModel> = _updatesFlow

    val listeners: MutableMap<String, ListenerRegistration> = mutableMapOf()

    fun addListener(itemId: String, collection: String) {
        val listener = db.collection(collection)
            .document(itemId)
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    val updatedItem = snapshot.toObject(CommonModel::class.java)
                    updatedItem?.let {
                        Log.d("FIREX", "emit  = $itemId")
                        _updatesFlow.tryEmit(updatedItem)
                    }
                }
            }
        listeners.put(itemId, listener)
        Log.d("FIREX", "add item id = $itemId")
    }

    fun removeListener(ids: List<String>) {
        val keysToRemove = listeners.filterKeys { it !in ids }.keys.toList()
        keysToRemove.forEach { key ->
            listeners[key]?.remove()
            listeners.remove(key)
            Log.d("FIREX", "removed item id = $key")
        }
    }

    fun isPresent(id: String) = listeners.containsKey(id)

    suspend fun fetchData(itemId: String, collection: String) {
         try {
            val snapshot = db.collection(collection)
                .whereEqualTo("id", itemId)
                .get()
                .await()
             Log.d("FIREX", "fetched item id = $itemId")
            val model = snapshot.documents.first().toObject(CommonModel::class.java)
            model?.let {
                _updatesFlow.tryEmit(model)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun randomizeStatus(collection: String, id: String) {

        val min = when(collection) {
            "letters" -> 1
            "numbers" -> 2
            else -> 3
        }

        val state = listOf<Int>(min, 4).random()

        db.collection(collection)
            .document(id)
            .update("status", state)
    }
}
