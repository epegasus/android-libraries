package com.sohaib.collageview.demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohaib.collageview.demo.databinding.ItemShapeBinding
import com.sohaib.collageview.demo.model.CollageShapeItem

class CollageAdapter(
    private val onClick: (CollageShapeItem) -> Unit
) : ListAdapter<CollageShapeItem, CollageAdapter.CustomViewHolder>(Diff) {

    private var selected: CollageShapeItem? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemShapeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val item = getItem(position)
        // If nothing selected yet, treat last item as selected by default
        val isSelected = selected?.layoutType == item.layoutType ||
                (selected == null && position == currentList.lastIndex)

        holder.bind(item, isSelected, onClick) { clicked ->
            if (selected?.layoutType != clicked.layoutType) {
                val previous = selected
                selected = clicked
                previous?.let { prev ->
                    val prevIndex = currentList.indexOf(prev)
                    if (prevIndex != -1) notifyItemChanged(prevIndex)
                }
                notifyItemChanged(position)
            }
        }
    }

    class CustomViewHolder(private val binding: ItemShapeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: CollageShapeItem,
            selected: Boolean,
            onClick: (CollageShapeItem) -> Unit,
            onInternalClick: (CollageShapeItem) -> Unit
        ) {
            binding.ivShape.setImageResource(item.iconRes)
            binding.root.isChecked = selected
            binding.root.strokeWidth = if (selected) 3 else 1
            binding.root.setOnClickListener {
                onInternalClick(item)
                onClick(item)
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<CollageShapeItem>() {
        override fun areItemsTheSame(oldItem: CollageShapeItem, newItem: CollageShapeItem): Boolean =
            oldItem.layoutType == newItem.layoutType

        override fun areContentsTheSame(oldItem: CollageShapeItem, newItem: CollageShapeItem): Boolean =
            oldItem == newItem
    }
}