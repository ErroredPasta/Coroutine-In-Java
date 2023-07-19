package com.example.simpletodoapp.search_history.ui

import android.view.View
import android.widget.Space
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.simpletodoapp.R

@Suppress("MemberVisibilityCanBePrivate")
class SearchHistorySection(
    private val root: View,
) : ViewBinding {
    override fun getRoot(): View = root

    val searchHistoryEmptyText: TextView = root.findViewById(R.id.search_history_empty_text)
    val searchHistoryRecyclerView: RecyclerView =
        root.findViewById(R.id.search_history_recycler_view)

    val emptySpace: View = root.findViewById(R.id.search_history_empty_space)

    var isSearchHistoryEmpty = true
        set(isEmpty) {
            if (field == isEmpty) return

            searchHistoryEmptyText.isVisible = isEmpty
            searchHistoryRecyclerView.isVisible = !isEmpty

            field = isEmpty
        }
}