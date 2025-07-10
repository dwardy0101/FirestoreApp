package com.example.firestore.ui.adapter

import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firestore.databinding.MainItemBinding
import com.example.firestore.ui.model.LetterModel
import com.example.firestore.ui.model.MergeModel
import com.example.firestore.ui.model.NumberModel
import com.example.firestore.ui.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainAdapter(
    val letters: List<LetterModel>,
    val numbers: List<NumberModel>,
    val mixed: List<MergeModel>
) : RecyclerView.Adapter<MainAdapter.ItemViewHolder>(), DefaultLifecycleObserver {

    val sparseArray = SparseArray<RecyclerView.Adapter<*>>()
    val sparseViewArray = SparseArray<RecyclerView>()
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MainItemBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 6

    suspend fun onAdapterViewVisibilityChanged(range: IntRange) {
        Log.d("FSTORE", "onAdapterViewVisibilityChanged ${range.first}-${range.last}")
        (range.first..range.last).forEach {
            val adapter = sparseArray.get(it)
            val recyclerView = sparseViewArray.get(it)
            when(adapter) {
                is MergeAdapter -> adapter.trackVisibleViews(recyclerView)
                is NumberAdapter -> adapter.trackVisibleViews(recyclerView)
                is LetterAdapter -> adapter.trackVisibleViews(recyclerView)
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        scope.cancel()
    }

    inner class ItemViewHolder(
        val binding: MainItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.sectionRecyclerView.layoutManager =
                LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)
            binding.sectionRecyclerView.addItemDecoration(
                DividerItemDecoration(binding.root.context, DividerItemDecoration.HORIZONTAL)
            )
        }

        fun bind(position: Int) {
            if (binding.sectionRecyclerView.adapter == null) {
                var adapter = if (position % 3 == 0) {
                    MergeAdapter(scope).also {
                        it.setData(mixed)
                    }
                } else if (position % 2 == 0 && position != itemCount) {
                    NumberAdapter(scope).also {
                        it.setData(numbers)
                    }
                } else {
                    LetterAdapter(scope).also {
                        it.setData(letters)
                    }
                }

                binding.sectionRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)

                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (recyclerView.adapter is MergeAdapter) {
                                scope.launch {
                                    (recyclerView.adapter as MergeAdapter).trackVisibleViews(recyclerView)
                                }
                            }
                            if (recyclerView.adapter is NumberAdapter) {
                                scope.launch {
                                    (recyclerView.adapter as NumberAdapter).trackVisibleViews(recyclerView)
                                }
                            }
                            if (recyclerView.adapter is LetterAdapter) {
                                scope.launch {
                                    (recyclerView.adapter as LetterAdapter).trackVisibleViews(recyclerView)
                                }
                            }
                        }
                    }
                })

                binding.sectionRecyclerView.adapter = adapter
                sparseArray.put(position, adapter)
                sparseViewArray.put(position, binding.sectionRecyclerView)
            }
        }
    }
}