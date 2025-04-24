package com.example.firestore.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.firestore.databinding.CommonItemBinding
import com.example.firestore.ui.model.MergeModel
import com.example.firestore.ui.model.NumberModel

class NumberAdapter() : RecyclerView.Adapter<NumberAdapter.NumberViewHolder>() {

    private var items: MutableList<NumberModel> = mutableListOf()

    fun setData(numbers: List<NumberModel>) {
        this.items = numbers.toMutableList()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NumberViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NumberViewHolder(CommonItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: NumberViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size

    inner class NumberViewHolder(
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
                    2 -> "#578FCA".toColorInt()
                    else -> Color.GRAY
                }
            )
        }
    }
}