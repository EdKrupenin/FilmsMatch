package com.example.filmsmatch.links.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.data.MovieLinkDomain
import com.example.filmsmatch.databinding.FilmLinkItemBinding

class LinksAdapter : ListAdapter<MovieLinkDomain, LinkViewHolder>(LinkDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val binding =
            FilmLinkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val link = getItem(position)
        holder.bind(link)
    }
}

class LinkDiffCallback : DiffUtil.ItemCallback<MovieLinkDomain>() {
    override fun areItemsTheSame(oldItem: MovieLinkDomain, newItem: MovieLinkDomain): Boolean {
        return oldItem.platformLink == newItem.platformLink
    }

    override fun areContentsTheSame(oldItem: MovieLinkDomain, newItem: MovieLinkDomain): Boolean {
        return oldItem == newItem
    }
}
