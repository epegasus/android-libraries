package com.sohaib.nestedrecyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohaib.nestedrecyclerview.databinding.ListItemCategoryBinding
import com.sohaib.nestedrecyclerview.dataProviders.DpMovies
import com.sohaib.nestedrecyclerview.interfaces.OnCategoryItemClickListener
import com.sohaib.nestedrecyclerview.interfaces.OnMovieItemClickListener
import com.sohaib.nestedrecyclerview.models.Category

class AdapterCategories(private val onCategoryItemClickListener: OnCategoryItemClickListener, private val onMovieItemClickListener: OnMovieItemClickListener) : ListAdapter<Category, AdapterCategories.ViewHolder>(diffUtil) {

    private val dpMovies by lazy { DpMovies() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemCategoryBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        val adapterMovie = AdapterMovies(onMovieItemClickListener)

        holder.binding.apply {
            tvTitleListItemCategory.text = currentItem.title
            rvMoviesListItemCategories.adapter = adapterMovie
            adapterMovie.submitList(dpMovies.getMovieList(currentItem.id))

            clContainer.setOnClickListener { onCategoryItemClickListener.onReadMoreClick(currentItem) }
        }
    }

    class ViewHolder(val binding: ListItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean = oldItem == newItem
        }
    }
}