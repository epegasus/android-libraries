package com.sohaib.imagefilters.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohaib.imagefilters.databinding.ItemFilterBinding

/**
 * @Author: SOHAIB AHMED
 * @Date: 9/15/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

class AdapterFilters(private val callback: (position: Int) -> Unit) : ListAdapter<Triple<Int, String, String>, AdapterFilters.ViewHolderFilters>(DiffUtilAdapter()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFilters {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFilterBinding.inflate(inflater, parent, false)
        return ViewHolderFilters(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderFilters, position: Int) {
        getItem(position).let { item ->
            holder.binding.sivImageItemFilter.setImageResource(item.first)
            holder.binding.mtvTitleItemFilter.text = item.second
            holder.binding.root.setOnClickListener { callback.invoke(position) }
        }
    }

    inner class ViewHolderFilters(val binding: ItemFilterBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffUtilAdapter : DiffUtil.ItemCallback<Triple<Int, String, String>>() {
        override fun areItemsTheSame(oldItem: Triple<Int, String, String>, newItem: Triple<Int, String, String>): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(oldItem: Triple<Int, String, String>, newItem: Triple<Int, String, String>): Boolean {
            return oldItem == newItem
        }
    }
}