package com.example.firestore

import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.random.Random

object MyFireStore {

    var unsubscribe: ListenerRegistration? = null

    init {
        val db = Firebase.firestore
//        unsubscribe = db.collection("users")
//            .addSnapshotListener { snapshots, e ->
//                if (e != null) {
//                    Log.w("Firestore", "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//
//                for (doc in snapshots!!) {
//                    Log.d("Firestore", "${doc.id} => ${doc.data}")
//                }
//            }
    }

    suspend fun hasCollections(): Boolean {
        return try {
            val db = Firebase.firestore
            val count = db.collection("users")
                .count()
                .get(AggregateSource.SERVER)
                .await()

            count.count > 10
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to check collection", e)
            false
        }
    }


    suspend fun fetchUsers(): List<User> {
        val db = Firebase.firestore
        val result = db.collection("users").get().await()
        val users = withContext(Dispatchers.Default) {
            result.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
        }
        return users
    }

    suspend fun generateUsers(): List<User> {
        val users = buildList {
            (0 until 10).forEach {
                val status = Random.nextInt(3) + 1
                add(User(status = status))
            }
        }

        batchAdd(users)

        return users
    }


suspend fun batchAdd(users: List<User>) {
    val db = Firebase.firestore

    val chunks = users.chunked(10)

    for (chunk in chunks) {
        val batch = db.batch()

        for (user in chunk) {
            val docRef = db.collection("users").document(user.id)
            batch.set(docRef, user)
        }

        batch.commit().await()
    }
}



fun unsubscribe() {
        unsubscribe?.remove()
    }
}