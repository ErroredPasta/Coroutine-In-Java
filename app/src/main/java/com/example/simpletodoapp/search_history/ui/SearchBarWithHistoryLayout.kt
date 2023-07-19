package com.example.simpletodoapp.search_history.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import com.example.simpletodoapp.R
import com.example.simpletodoapp.databinding.SearchBarBinding
import com.example.simpletodoapp.search_history.domain.SearchHistory
import com.example.simpletodoapp.search_history.ui.mapper.toSearchHistoryUiState

class SearchBarWithHistoryLayout : FrameLayout {
    // region constructors =========================================================================
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
    ) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )
    // endregion constructors ======================================================================

    private var onSearchHistoryClick: ((SearchHistory) -> Unit)? = null
    private var onSearchHistoryDeleteClick: ((SearchHistory) -> Unit)? = null

    private val adapter = SearchHistoryAdapter()

    private val searchHistorySection: SearchHistorySection by lazy {
        SearchHistorySection(root = findViewById<ConstraintLayout>(R.id.search_history_section)).apply {
            emptySpace.setOnClickListener {
                searchBarBinding.exitSearchMode()
            }

            searchHistoryRecyclerView.apply {
                adapter = this@SearchBarWithHistoryLayout.adapter
            }
        }
    }

    private val searchBarBinding = SearchBarBinding
        .inflate(LayoutInflater.from(context), this, true)
        .setListenersForSearchBar()

    private val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    fun setListeners(
        onSearchClick: (SearchHistory) -> Unit,
        onSearchHistoryClick: (SearchHistory) -> Unit,
        onSearchHistoryDeleteClick: (SearchHistory) -> Unit,
    ) {
        searchBarBinding.searchBarSearchButton.setOnClickListener {
            onSearchClick(searchBarBinding.searchBarEditText.text.toString())
            searchBarBinding.exitSearchMode()
        }

        this.onSearchHistoryClick = { searchHistory ->
            searchBarBinding.searchBarEditText.setText(searchHistory)
            onSearchHistoryClick(searchHistory)
            searchBarBinding.exitSearchMode()
        }

        this.onSearchHistoryDeleteClick = onSearchHistoryDeleteClick
    }

    fun submitList(searchHistoryList: List<SearchHistory>) {
        searchHistorySection.isSearchHistoryEmpty = searchHistoryList.isEmpty()

        val uiStateList = searchHistoryList.map { searchHistory ->
            searchHistory.toSearchHistoryUiState(
                onClick = { onSearchHistoryClick?.invoke(searchHistory) },
                onDeleteClick = { onSearchHistoryDeleteClick?.invoke(searchHistory) }
            )
        }

        adapter.submitList(uiStateList)
    }

    private fun SearchBarBinding.setListenersForSearchBar() = apply {
        searchBarEditText.setOnFocusChangeListener { _, hasFocus ->
            searchHistorySection.root.isVisible = hasFocus
            searchBarClearButton.isVisible = hasFocus
        }

        searchBarEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBarSearchButton.performClick()
                true
            } else {
                false
            }
        }

        searchBarClearButton.setOnClickListener {
            searchBarEditText.text.clear()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val searchBar = searchBarBinding.root
        val searchHistorySection = searchHistorySection.root

        val parentLeft = left + paddingLeft
        val parentRight = right - paddingRight
        var parentTop = top + paddingTop
        val parentBottom = bottom - paddingBottom

        // place search bar first
        val searchBarLayoutParams = searchBar.frameLayoutParams
        searchBar.layout(
            parentLeft + marginLeft,
            parentTop + searchBarLayoutParams.topMargin,
            parentLeft + searchBar.measuredWidth - searchBarLayoutParams.rightMargin,
            parentTop + searchBar.measuredHeight - searchBarLayoutParams.bottomMargin
        )

        // adjust parent top
        parentTop += searchBar.measuredHeight

        if (searchHistorySection.isVisible) {
            val layoutParams = searchHistorySection.frameLayoutParams

            val width = searchHistorySection.measuredWidth
            val height = searchHistorySection.measuredHeight

            searchHistorySection.layout(
                parentLeft + layoutParams.leftMargin,
                parentTop + layoutParams.topMargin,
                parentLeft + width - layoutParams.rightMargin,
                parentTop + height - layoutParams.bottomMargin,
            )
        }


        for (child in children) {
            // already placed search bar
            // search history section should be the last one to place
            if (child == searchBar || child == searchHistorySection) continue
            if (!child.isVisible) continue

            child.layoutChild(parentLeft, parentTop, parentRight, parentBottom)
        }

    }

    @SuppressLint("RtlHardcoded")
    private fun View.layoutChild(
        parentLeft: Int,
        parentTop: Int,
        parentRight: Int,
        parentBottom: Int,
    ) {
        val layoutParams = frameLayoutParams

        val width = measuredWidth
        val height = measuredHeight

        val gravity = if (layoutParams.gravity == -1) {
            Gravity.TOP or Gravity.START // default gravity
        } else {
            layoutParams.gravity
        }

        val layoutDirection = layoutDirection
        val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)
        val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK

        val childLeft = when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
            Gravity.CENTER_HORIZONTAL -> (parentRight - parentLeft - width) / 2 +
                    layoutParams.leftMargin - layoutParams.rightMargin

            Gravity.RIGHT -> parentRight - width - layoutParams.rightMargin
            Gravity.LEFT -> parentLeft + layoutParams.leftMargin
            else -> parentLeft + layoutParams.leftMargin
        }

        val childTop = when (verticalGravity) {
            Gravity.TOP -> parentTop + layoutParams.topMargin
            Gravity.CENTER_VERTICAL -> (parentBottom - parentTop - height) / 2 +
                    layoutParams.topMargin - layoutParams.bottomMargin

            Gravity.BOTTOM -> parentBottom - height - layoutParams.bottomMargin
            else -> parentTop + layoutParams.topMargin
        }

        this.layout(childLeft, childTop, childLeft + width, childTop + height)
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent?): Boolean {
        if (searchBarBinding.searchBarEditText.hasFocus() && event?.keyCode == KeyEvent.KEYCODE_BACK) {
            searchBarBinding.exitSearchMode()
            return true
        }

        return super.dispatchKeyEventPreIme(event)
    }

    private fun SearchBarBinding.exitSearchMode() = with(searchBarEditText) {
        clearFocus()
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private val View.frameLayoutParams: LayoutParams get() = layoutParams as LayoutParams
}
