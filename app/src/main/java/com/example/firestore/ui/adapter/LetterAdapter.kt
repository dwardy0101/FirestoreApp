package com.example.firestore.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firestore.databinding.CommonItemBinding
import com.example.firestore.ui.model.NumberModel
import androidx.core.graphics.toColorInt
import com.example.firestore.ui.model.LetterModel

class LetterAdapter() : RecyclerView.Adapter<LetterAdapter.LetterViewHolder>() {

    private var items: MutableList<LetterModel> = mutableListOf()

    fun setData(letters: List<LetterModel>) {
        this.items = letters.toMutableList()
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

    inner class LetterViewHolder(
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
                    1 -> "#BBD8A3".toColorInt()
                    else -> Color.GRAY
                }
            )
        }
    }
}