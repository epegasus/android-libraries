package com.sohaib.downloadmanager.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sohaib.downloadmanager.R
import com.sohaib.downloadmanager.data.entities.DownloadEntity

/**
 * Created by: Sohaib Ahmed
 * Date: 3/7/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class AdapterDownloads(private val downloads: List<DownloadEntity>) : RecyclerView.Adapter<AdapterDownloads.DownloadViewHolder>() {
    inner class DownloadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(download: DownloadEntity) {
            itemView.findViewById<TextView>(R.id.title).text = download.title
            itemView.findViewById<TextView>(R.id.status).text = download.status
            itemView.findViewById<TextView>(R.id.progress).text = "${download.progress}%"
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_download, parent, false)
        return DownloadViewHolder(view)
    }
    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bind(downloads[position])
    }
    override fun getItemCount() = downloads.size
}