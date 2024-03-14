package com.example.filmsmatch.list.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.data.MovieDomain
import com.example.filmsmatch.R
import com.example.filmsmatch.details.FilmDetailBottomSheetFragment

class FilmListAdapter(private val fragmentManager: FragmentActivity) :
    ListAdapter<MovieDomain, MovieViewHolder>(FilmDiffCallback()) {
    // Обработчик клика на элемент списка
    var onNotTodayClick: ((MovieDomain) -> Unit)? = null
    var onJustRightClick: ((MovieDomain) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // Создаем ViewHolder для элемента списка
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.films_item_list, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        // Привязываем данные к ViewHolder
        holder.bind(getItem(position))
        holder.notTodayButton.setOnClickListener {
            onNotTodayClick?.invoke(getItem(position))
        }
        holder.justRightButton.setOnClickListener {
            onJustRightClick?.invoke(getItem(position))
        }
        holder.infoButton.setOnClickListener {
            showBottomSheetForItem(position)
        }
    }

    // Метод для удаления элемента из списка
    fun removeItem(position: Int, direction: Boolean) {
        if (direction) onJustRightClick?.invoke(getItem(position))
        else onNotTodayClick?.invoke(getItem(position))
        // Создаем копию текущего списка и удаляем из нее элемент
        val newList = currentList.toMutableList().apply {
            removeAt(position)
        }
        // Обновляем список
        submitList(newList)

    }

    private fun showBottomSheetForItem(position: Int) {
        val kinopoiskId = getItem(position).kinopoiskId
        val filmDetailBottomSheetFragment = FilmDetailBottomSheetFragment.newInstance(kinopoiskId)
        filmDetailBottomSheetFragment.show(
            fragmentManager.supportFragmentManager,
            filmDetailBottomSheetFragment.tag
        )
    }
}


class FilmDiffCallback : DiffUtil.ItemCallback<MovieDomain>() {
    override fun areItemsTheSame(oldItem: MovieDomain, newItem: MovieDomain): Boolean {
        // Сравниваем идентификаторы фильмов
        return oldItem.kinopoiskId == newItem.kinopoiskId
    }

    override fun areContentsTheSame(oldItem: MovieDomain, newItem: MovieDomain): Boolean {
        // Проверяем, имеют ли фильмы одинаковое содержимое
        return oldItem == newItem
    }
}