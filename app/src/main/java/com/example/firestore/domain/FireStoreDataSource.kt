package com.example.firestore.domain

import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FireStoreDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getLettersFlow(): Flow<List<CommonModel>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = db.collection("letters")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val objects = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CommonModel::class.java)?.copy(id = doc.id)
                    }
                    trySend(objects)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    fun getNumbersFlow() : Flow<List<CommonModel>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = db.collection("numbers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val objects = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CommonModel::class.java)?.copy(id = doc.id)
                    }
                    trySend(objects)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    fun getMergeFlow() : Flow<List<CommonModel>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = db.collection("merge")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val objects = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CommonModel::class.java)?.copy(id = doc.id)
                    }
                    trySend(objects)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun hasCollections(code: Int): Boolean {
        return try {
            val collection = when(code) {
                1 -> "letters"
                2 -> "numbers"
                else -> "merge"
            }

            val db = Firebase.firestore
            val count = db.collection(collection)
                .count()
                .get(AggregateSource.SERVER)
                .await()

            count.count > 0
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to check collection", e)
            false
        }
    }

    suspend fun fetchData(code: Int): List<CommonModel> {
        val collection = when(code) {
            1 -> "letters"
            2 -> "numbers"
            else -> "merge"
        }
        val db = Firebase.firestore
        val result = db.collection(collection).get().await()
        val items = withContext(Dispatchers.Default) {
            result.documents.mapNotNull { doc ->
                doc.toObject(CommonModel::class.java)
            }
        }
        return items
    }

    suspend fun generateLetters(): List<CommonModel> {
        if (hasCollections(1)) {
            return fetchData(1)
        }
        val letters = buildList {
            (1..15).forEach {
                val letter = ('A'..'Z').random()
                add(CommonModel(text = letter.toString(), status = 1))
            }
        }
        batchAdd(1, letters)
        return letters
    }

    suspend fun generateNumbers(): List<CommonModel> {
        if (hasCollections(2)) {
            return fetchData(2)
        }
        val nums = buildList {
            (1..15).forEach {
                add(CommonModel(text = (1..100).random().toString(), status = 2))
            }
        }
        batchAdd(2, nums)
        return nums
    }

    suspend fun generateMixed(): List<CommonModel> {
        if (hasCollections(3)) {
            return fetchData(3)
        }
        val items = buildList {
            (1..15).forEach {
                val letter = ('A'..'Z').random()
                val number = (1..100).random().toString()
                val code = "$letter$number"
                add(CommonModel(text = code, status = 3))
            }
        }
        batchAdd(3, items)
        return items
    }


    suspend fun <T> batchAdd(code: Int, items: List<T>) {
        val db = Firebase.firestore
        val batch = db.batch()

        for (item in items) {
            val collection = when(code) {
                1 -> "letters"
                2 -> "numbers"
                else -> "merge"
            }
            val docRef = db.collection(collection).document((item as CommonModel).id)
            batch.set(docRef, item)
        }

        batch.commit().await()
    }

}