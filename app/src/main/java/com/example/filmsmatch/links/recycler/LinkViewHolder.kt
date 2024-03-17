package com.example.filmsmatch.links.recycler


import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.data.FilmLinkDomain
import com.example.filmsmatch.databinding.LinkItemBinding

class LinkViewHolder(private val binding: LinkItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(link: FilmLinkDomain) {
        binding.watchHereButton.text = link.platformName
        binding.watchHereButton.setOnClickListener {
            val url = link.platformLink
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(itemView.context.packageManager) != null) {
                itemView.context.startActivity(intent)
            } else {
                Toast.makeText(
                    itemView.context,
                    "No browser found to open the link",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
