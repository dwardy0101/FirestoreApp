package com.example.firestore.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.firestore.databinding.CommonItemBinding
import com.example.firestore.ui.model.MergeModel

class MergeAdapter() : RecyclerView.Adapter<MergeAdapter.MergeViewHolder>() {

    private var items: MutableList<MergeModel> = mutableListOf()

    fun setData(mixed: List<MergeModel>) {
        this.items = mixed.toMutableList()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MergeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MergeViewHolder(CommonItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: MergeViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size

    inner class MergeViewHolder(
        private val binding: CommonItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
//            binding.itemText.text = items[position].text
            setColor(items[position].status)
        }

        fun setColor(status: Int) {
            binding.container.setBackgroundColor(
                when(status) {
//                    1 -> Color.GREEN
//                    2 -> Color.RED
                    3 -> "#F7CFD8".toColorInt()
                    else -> Color.GRAY
                }
            )
        }
    }
}