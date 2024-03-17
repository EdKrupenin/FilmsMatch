package com.example.filmsmatch.list.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.data.FilmDomain
import com.example.filmsmatch.R
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class MovieViewHolder(
    view: View,
) : RecyclerView.ViewHolder(view) {
    private val posterImageView: ImageView = itemView.findViewById(R.id.poster_film)
    private val titleTextView: TextView = itemView.findViewById(R.id.title_film)
    private val imdbRatingTextView: TextView = itemView.findViewById(R.id.imdb_rating_film)
    private val kinopoistRatingTextView: TextView =
        itemView.findViewById(R.id.kinopoist_rating_film)
    private val yearTextView: TextView = itemView.findViewById(R.id.year_film)
    private val genresTextView: TextView = itemView.findViewById(R.id.genres_film)
    val notTodayButton: MaterialButton = itemView.findViewById(R.id.not_today_button)
    val justRightButton: MaterialButton = itemView.findViewById(R.id.just_right_button)
    val infoButton: MaterialButton = itemView.findViewById(R.id.infoButton)
    fun bind(model: FilmDomain) {

        // Устанавливаем постер
        Picasso.get().load(model.posterUrl).into(posterImageView)

        // Устанавливаем название фильма
        titleTextView.text = model.nameRu ?: model.nameEn ?: model.nameOriginal

        // Устанавливаем рейтинг IMDb, если он есть
        if (model.ratingImdb != null) {
            imdbRatingTextView.text = model.ratingImdb.toString()
            imdbRatingTextView.visibility = View.VISIBLE
        } else {
            imdbRatingTextView.visibility = View.GONE
        }

        // Устанавливаем рейтинг Кинопоиска, если он есть
        if (model.ratingKinopoisk != null) {
            kinopoistRatingTextView.text = model.ratingKinopoisk.toString()
            kinopoistRatingTextView.visibility = View.VISIBLE
        } else {
            kinopoistRatingTextView.visibility = View.GONE
        }

        // Устанавливаем год выпуска
        yearTextView.text = model.year.toString()

        // Устанавливаем жанры
        genresTextView.text = model.genres.joinToString(separator = " • ", transform = { it })
    }
}