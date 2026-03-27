package com.sohaib.storyview.adapters

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.sohaib.storyview.dataClasses.MyStory
import com.sohaib.storyview.databinding.ItemStoryBinding
import com.sohaib.storyview.listeners.StoryCallback
import com.sohaib.storyview.utils.PaletteExtraction

/**
 * Created by Sohaib Ahmed on 04/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

class CustomViewPagerAdapter(private val imageList: List<MyStory>, private val itemClick: StoryCallback) : RecyclerView.Adapter<CustomViewPagerAdapter.ViewHolder>() {

    private var storiesStarted = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val item = imageList[position]

            // Configuring Description
            if (!item.description.isNullOrEmpty()) {
                itemBinding.mtvDescription.visibility = View.VISIBLE
                itemBinding.mtvDescription.text = item.description
                itemBinding.mtvDescription.setOnClickListener { itemClick.onDescriptionClickListener(adapterPosition) }
            }

            // Configuring Image
            Glide.with(itemBinding.root.context)
                .load(item.url)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                        itemClick.nextStory()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        if (resource != null) {
                            val paletteExtraction = PaletteExtraction(itemBinding.root, (resource as BitmapDrawable).bitmap)
                            paletteExtraction.execute()
                        }
                        if (!storiesStarted) {
                            storiesStarted = true
                            itemClick.startStories()
                        }
                        return false
                    }

                })
                .into(itemBinding.divImageItemStory)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ViewHolder(val itemBinding: ItemStoryBinding) : RecyclerView.ViewHolder(itemBinding.root)
}