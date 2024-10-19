package com.services.provider.ui.main.home_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.services.provider.data.prefs.MyPref
import com.services.provider.databinding.ItemCategoryBinding

class CategoryAdapter(private val pref: MyPref) :
    ListAdapter<String, CategoryAdapter.ItemViewHolder>(DiffUtils) {

    var onItemClick: ((String) -> Unit)? = null

    inner class ItemViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(skilledService: String) {
            with(binding) {
                tvCategoryName.text = skilledService
            }
        }

        init {

            binding.root.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object DiffUtils : DiffUtil.ItemCallback<String>() {
    override fun areContentsTheSame(
        oldItem: String, newItem: String
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: String, newItem: String
    ): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}
