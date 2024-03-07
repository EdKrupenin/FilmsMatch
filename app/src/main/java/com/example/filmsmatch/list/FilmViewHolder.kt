package com.example.filmsmatch.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.data.MovieDomain
import com.example.filmsmatch.R
import com.squareup.picasso.Picasso

class MovieViewHolder(
    view: View,
    private val onItemActionSwiped: (Int) -> Unit,
) : RecyclerView.ViewHolder(view) {
    fun bind(model: MovieDomain) {
        Picasso.get()
            .load(model.posterUrl)
            .into(itemView.findViewById<ImageView>(R.id.film_poster))
        /*itemView.setOnClickListener{
            onItemActionSwiped.invoke(absoluteAdapterPosition)
        }*/
        itemView.findViewById<TextView>(R.id.title_text).apply {
            text = model.nameRu
        }
        itemView.findViewById<TextView>(R.id.rating_year_text).apply {
            text = model.ratingImdb.toString()
        }
    }
}