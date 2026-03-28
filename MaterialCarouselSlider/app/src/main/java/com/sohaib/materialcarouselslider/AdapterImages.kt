package com.sohaib.materialcarouselslider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.MaskableFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class AdapterImages : ListAdapter<Int, AdapterImages.CustomViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_carousel, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.itemView.apply {
            findViewById<ShapeableImageView>(R.id.sivPhoto).setImageResource(currentItem)
            findViewById<MaterialTextView>(R.id.mtvTitle).text = "Position # ${position + 1}"

            (this as MaskableFrameLayout).setOnMaskChangedListener { maskRect ->
                // Any custom motion to run when mask size changes
                findViewById<MaterialTextView>(R.id.mtvTitle).translationX = maskRect.left
                findViewById<MaterialTextView>(R.id.mtvTitle).setAlpha(lerp(1F, 0F, 0F, 80F, maskRect.left))
            }
        }
    }

    private fun lerp(start: Float, end: Float, startRange: Float, endRange: Float, value: Float): Float {
        return start + (end - start) * ((value - startRange) / (endRange - startRange))
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Int>() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }
        }
    }
}