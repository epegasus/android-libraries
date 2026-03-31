package com.sohaib.installedapps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohaib.installedapps.dataClasses.App
import com.sohaib.installedapps.databinding.ItemAppBinding

class AdapterApps : ListAdapter<App, AdapterApps.CustomViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAppBinding.inflate(layoutInflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.apply {
            binding.mtvAppName.text = currentItem.appName
            binding.mtvPackageName.text = currentItem.packageName
            binding.ifvIcon.setImageDrawable(currentItem.icon)
        }
    }

    class CustomViewHolder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<App>() {
            override fun areItemsTheSame(oldItem: App, newItem: App): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: App, newItem: App): Boolean = oldItem == newItem
        }
    }
}