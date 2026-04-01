package com.sohaib.appusagehistory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohaib.appusagehistory.databinding.ItemUsageStatsBinding

class UsageStatsAdapter : ListAdapter<String, UsageStatsAdapter.CustomViewHolder>(customDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemUsageStatsBinding.inflate(layoutInflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.binding.root.text = currentItem
    }

    class CustomViewHolder(val binding: ItemUsageStatsBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val customDiffUtils = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        }
    }
}