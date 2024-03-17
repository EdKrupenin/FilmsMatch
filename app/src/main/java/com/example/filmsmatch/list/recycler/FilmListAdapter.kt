package com.example.filmsmatch.list.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.data.FilmDomain
import com.example.filmsmatch.R
import com.example.filmsmatch.details.FilmDetailBottomSheetFragment
import com.example.filmsmatch.links.FilmLinksDialogFragment

class FilmListAdapter(private val fragmentManager: FragmentActivity) :
    ListAdapter<FilmDomain, MovieViewHolder>(FilmDiffCallback()) {
    // Обработчик клика на элемент списка
    var onNotTodayClick: ((FilmDomain) -> Unit)? = null
    var onJustRightClick: ((FilmDomain) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // Создаем ViewHolder для элемента списка
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.film_item, parent, false)
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
            showLinksDialogForItem(position)
        }
        holder.infoButton.setOnClickListener {
            showBottomSheetForItem(position)
        }
    }

    // Метод для удаления элемента из списка
    fun showLinksDialogOrRemoveItem(position: Int, direction: Boolean) {
        if (direction) {
            onJustRightClick?.invoke(getItem(position))
            showLinksDialogForItem(position)

        } else {
            onNotTodayClick?.invoke(getItem(position))
            removeItemAtPosition(position)
        }
    }

    private fun removeItemAtPosition(position: Int) {
        val newList = currentList.toMutableList().apply {
            removeAt(position)
        }
        submitList(newList)
    }

    private fun showLinksDialogForItem(position: Int) {
        val kinopoiskId = getItem(position).kinopoiskId
        val filmLinksFragment = FilmLinksDialogFragment.newInstance(kinopoiskId)
        filmLinksFragment.show(
            fragmentManager.supportFragmentManager,
            filmLinksFragment.tag
        )
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


class FilmDiffCallback : DiffUtil.ItemCallback<FilmDomain>() {
    override fun areItemsTheSame(oldItem: FilmDomain, newItem: FilmDomain): Boolean {
        // Сравниваем идентификаторы фильмов
        return oldItem.kinopoiskId == newItem.kinopoiskId
    }

    override fun areContentsTheSame(oldItem: FilmDomain, newItem: FilmDomain): Boolean {
        // Проверяем, имеют ли фильмы одинаковое содержимое
        return oldItem == newItem
    }
}