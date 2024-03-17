package com.example.filmsmatch.links.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.data.FilmLinkDomain
import com.example.filmsmatch.databinding.LinkItemBinding

class LinksAdapter : ListAdapter<FilmLinkDomain, LinkViewHolder>(LinkDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val binding =
            LinkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val link = getItem(position)
        holder.bind(link)
    }
}

class LinkDiffCallback : DiffUtil.ItemCallback<FilmLinkDomain>() {
    override fun areItemsTheSame(oldItem: FilmLinkDomain, newItem: FilmLinkDomain): Boolean {
        return oldItem.platformLink == newItem.platformLink
    }

    override fun areContentsTheSame(oldItem: FilmLinkDomain, newItem: FilmLinkDomain): Boolean {
        return oldItem == newItem
    }
}
