package com.example.firestore.ui.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firestore.databinding.MainItemBinding
import com.example.firestore.ui.model.LetterModel
import com.example.firestore.ui.model.MergeModel
import com.example.firestore.ui.model.NumberModel

class MainAdapter(
    val letters: List<LetterModel>,
    val numbers: List<NumberModel>,
    val mixed: List<MergeModel>
) : RecyclerView.Adapter<MainAdapter.ItemViewHolder>() {

    val sparseArray = SparseArray<RecyclerView.Adapter<*>>()

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
                    MergeAdapter().also {
                        it.setData(mixed)
                        it.notifyDataSetChanged()
                    }
                } else if (position % 2 == 0 && position != itemCount) {
                    NumberAdapter().also {
                        it.setData(numbers)
                        it.notifyDataSetChanged()
                    }
                } else {
                    LetterAdapter().also {
                        it.setData(letters)
                        it.notifyDataSetChanged()
                    }
                }
                sparseArray.put(position, adapter)
            }
        }
    }
}