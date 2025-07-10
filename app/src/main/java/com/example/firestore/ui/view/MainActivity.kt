package com.example.firestore.ui.view

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firestore.R
import com.example.firestore.databinding.ActivityMainBinding
import com.example.firestore.domain.CommonModel
import com.example.firestore.domain.FireStoreDataSource
import com.example.firestore.domain.toLetters
import com.example.firestore.domain.toMerge
import com.example.firestore.domain.toNumbers
import com.example.firestore.ui.adapter.MainAdapter
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    val visibleItemsLiveData = MutableLiveData<IntRange>()
    @OptIn(FlowPreview::class)
    val debouncedFlow = visibleItemsLiveData.asFlow()
//        .debounce(300) // waits 300ms after last scroll update
//        .distinctUntilChanged()

    val isConnectedFlow: Flow<Boolean>
        get() = callbackFlow {
            var connectivityManager: ConnectivityManager? = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    connectivityManager?.getNetworkCapabilities(network)?.let {
                        if (it.hasCapability(NET_CAPABILITY_INTERNET)) {
                            trySend(true)
                        }
                    }
                }

                override fun onLost(network: Network) {
                    trySend(false)
                }

                override fun onUnavailable() {
                    trySend(false)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    capabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, capabilities)
                    if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        trySend(true)
                    } else {
                        trySend(false)
                    }
                }
            }
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()

            connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)

            awaitClose {
                connectivityManager?.unregisterNetworkCallback(networkCallback)
            }
        }

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

        lifecycleScope.launch {
            debouncedFlow.collect { range ->
                (binding.recyclerView.adapter as MainAdapter).onAdapterViewVisibilityChanged(range)
            }
        }

        val dataSource = FireStoreDataSource()

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val isConnected = isConnectedFlow.first()
//                    isConnectedFlow.stateIn(
//                    scope = this,
//                    started = SharingStarted.Eagerly,
//                    initialValue = false
//                )

                val lettersDeferred = async { dataSource.generateLetters(isConnected) }
                val numbersDeferred = async { dataSource.generateNumbers(isConnected) }
                val mixedDeferred = async { dataSource.generateMixed(isConnected) }

                val letters: List<CommonModel> = lettersDeferred.await()
                val numbers: List<CommonModel> = numbersDeferred.await()
                val mixed: List<CommonModel> = mixedDeferred.await()

                // Now you can use the results
                Log.d("Results", "Letters: $letters")
                Log.d("Results", "Numbers: $numbers")
                Log.d("Results", "Mixed: $mixed")


                val adapter = MainAdapter(letters.toLetters(), numbers.toNumbers(), mixed.toMerge())
                this@MainActivity.lifecycle.addObserver(adapter)

                binding.recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL))
                binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val layoutManger = recyclerView.layoutManager as LinearLayoutManager
                        val firstVisible = layoutManger.findFirstVisibleItemPosition()
                        val lastVisible = layoutManger.findLastVisibleItemPosition()
                        visibleItemsLiveData.value = firstVisible..lastVisible
                    }
                })
            }

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