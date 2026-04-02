package com.sohaib.nestedrecyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohaib.nestedrecyclerview.databinding.ListItemMovieBinding
import com.sohaib.nestedrecyclerview.interfaces.OnMovieItemClickListener
import com.sohaib.nestedrecyclerview.models.Movie

class AdapterMovies(private val onMovieItemClickListener: OnMovieItemClickListener) : ListAdapter<Movie, AdapterMovies.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemMovieBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            mtvTitleListItemMovie.text = item.title
            sivImageListItemMovie.setImageResource(item.imageId)

            clContainer.setOnClickListener { onMovieItemClickListener.onItemClick(item) }
        }
    }

    class ViewHolder(val binding: ListItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
        }
    }
}