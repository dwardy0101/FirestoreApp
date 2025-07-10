package com.example.firestore.ui.adapter

import android.graphics.Color
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firestore.databinding.CommonItemBinding
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firestore.ui.model.LetterModel
import com.google.firebase.firestore.ListenerRegistration
import com.example.firestore.data.FirestoreRepository
import com.example.firestore.domain.TrackVisibleItemsUseCase
import com.example.firestore.domain.toCommonModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LetterAdapter (
    val scope: CoroutineScope,
    private val useCase: TrackVisibleItemsUseCase = TrackVisibleItemsUseCase(FirestoreRepository())
): RecyclerView.Adapter<LetterAdapter.LetterViewHolder>() {

    init {
        scope.launch {
            useCase.updatesFlow.collect { common ->
                Log.d("FIREX", "collect  = ${common.text}")
                var item = items.first { it.id == common.id }
                item.status = common.status
                val index = items.indexOf(item)
                notifyItemChanged(index, item)
            }
        }
    }

    private var items: MutableList<LetterModel> = mutableListOf()
    private val listeners = SparseArray<ListenerRegistration>()

    fun setData(letters: List<LetterModel>) {
        this.items = letters.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LetterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LetterViewHolder(CommonItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: LetterViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size

    suspend fun trackVisibleViews(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()

        val range = first..last

        if (range.first < 0 && range.last < 0) return
        useCase.trackVisibleItems("letters", range, items.toCommonModel())

//        scope.launch {
//            while (isActive) {
//                var index = (first..last).random()
//                index = index.coerceAtMost(last)
//                index = index.coerceAtLeast(first)
//                useCase.randomizeStatus("letters", items[index].id)
//
//                Log.d("FIREX", "LetterAdapter random ${items[index].text}")
//                delay(10_000)
//            }
//        }

//        Log.d("FSTORE", "LetterAdapter*${this}: ${first}-${last}")
//        // remove listener for not visible item
//        if (listeners.isNotEmpty()) {
//            for (i in 0 until items.size) {
//                if ((i < first || i > last) && listeners.indexOfKey(i) >= 0) {
//                    listeners.get(i).remove()
//                    listeners.remove(i)
//                    Log.d("FSTORE", "LetterAdapter*${this} removed listener position = $i")
//                }
//            }
//        }
//
//        (first..last).forEach {
//            // fetch data
//            val index = it
//            val snapshot = Firebase.firestore.collection("letters")
//                .whereEqualTo("id", items[index].id)
//                .get()
//                .await()
//            val model = snapshot.documents.first().toObject(LetterModel::class.java)
//
//            Log.d("FSTORE", "LetterAdapter*${this} fetched position = $index")
//
//            model?.let {
//                items[index] = model
//                notifyItemChanged(index, model)
//            }
//
//            // add listener
//            if (listeners.indexOfKey(index) < 0) {
//                val listener = Firebase.firestore.collection("letters")
//                    .document(items[index].id)
//                    .addSnapshotListener { doc, _ ->
//                        val model = doc?.toObject(LetterModel::class.java)
//                        model?.let {
//                            items[index] = model
//                            notifyItemChanged(index, model)
//                        }
//                    }
//                listeners.put(index, listener)
//                Log.d("FSTORE", "LetterAdapter*${this} add listener position = $index")
//            }
//        }
    }

    inner class LetterViewHolder(
        private val binding: CommonItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.itemText.text = items[position].text
            setColor(items[position].status)
        }

        fun setColor(status: Int) {
            binding.container.setBackgroundColor(
                when(status) {
//                    1 -> Color.GREEN
//                    2 -> Color.RED
                    1 -> "#BBD8A3".toColorInt()
                    else -> Color.GRAY
                }
            )
        }
    }
}