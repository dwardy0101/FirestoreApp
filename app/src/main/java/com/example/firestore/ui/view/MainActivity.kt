package com.example.firestore.ui.view

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firestore.MyFireStore
import com.example.firestore.R
import com.example.firestore.databinding.ActivityMainBinding
import com.example.firestore.domain.CommonModel
import com.example.firestore.domain.FireStoreDataSource
import com.example.firestore.domain.toLetters
import com.example.firestore.domain.toMerge
import com.example.firestore.domain.toNumbers
import com.example.firestore.ui.adapter.MainAdapter
import com.example.firestore.ui.model.MergeModel
import com.example.firestore.ui.model.NumberModel
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val visibleIds = mutableListOf<String>()
    private val listenerRegistrations = mutableListOf<ListenerRegistration>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dataSource = FireStoreDataSource()
        lifecycleScope.launch {
            val lettersDeferred = async { dataSource.generateLetters() }
            val numbersDeferred = async { dataSource.generateNumbers() }
            val mixedDeferred = async { dataSource.generateMixed() }

            val letters: List<CommonModel> = lettersDeferred.await()
            val numbers: List<CommonModel> = numbersDeferred.await()
            val mixed: List<CommonModel> = mixedDeferred.await()

            // Now you can use the results
            Log.d("Results", "Letters: $letters")
            Log.d("Results", "Numbers: $numbers")
            Log.d("Results", "Mixed: $mixed")


            binding.recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL))
            binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            binding.recyclerView.adapter = MainAdapter(letters.toLetters(), numbers.toNumbers(), mixed.toMerge())
        }

//        val adapter = UserAdapter()
//
//        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.adapter = adapter
//
//        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//
//                listenerRegistrations.forEach { it.remove() }
//                listenerRegistrations.clear()
//                visibleIds.clear()
//
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val firstVisible = layoutManager.findFirstVisibleItemPosition()
//                val lastVisible = layoutManager.findLastVisibleItemPosition()
//
//                for (i in firstVisible..lastVisible) {
//                    val user = adapter.items[i]
//                    visibleIds.add(user.id)
//                }
//
//                visibleIds.forEach { id ->
//                    val listenerRegistration = Firebase.firestore.collection("users")
//                        .document(id)
//                        .addSnapshotListener { document, _ ->
//                            document?.toObject(User::class.java)?.let { updatedUser ->
//                                adapter.updateData(updatedUser)
//                            }
//                        }
//                    listenerRegistrations.add(listenerRegistration)
//                }
//            }
//        })
//
//        lifecycleScope.launch {
//            if (MyFireStore.hasCollections()) {
//                adapter.setData(MyFireStore.fetchUsers())
//            } else {
//                adapter.setData(MyFireStore.generateUsers())
//            }
//        }


    }

    override fun onDestroy() {
        super.onDestroy()
//        MyFireStore.unsubscribe()
    }
}