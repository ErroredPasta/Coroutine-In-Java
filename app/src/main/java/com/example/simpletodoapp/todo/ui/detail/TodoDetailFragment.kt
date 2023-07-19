package com.example.simpletodoapp.todo.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.simpletodoapp.R
import com.example.simpletodoapp.databinding.FragmentTodoDetailBinding
import com.example.simpletodoapp.todo.domain.TodoException
import com.example.simpletodoapp.todo.ui.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TodoDetailFragment : Fragment() {
    private val viewModel by viewModels<TodoDetailViewModel>()
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentTodoDetailBinding.inflate(inflater, container, false).apply {
        viewModel = this@TodoDetailFragment.viewModel
        lifecycleOwner = viewLifecycleOwner
    }.also {
        viewModel.state
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                if (it is TodoDetailState.Error) handleError(cause = it.cause)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }.root

    private fun handleError(cause: Throwable) {
        if (cause is TodoException.NoTodoDetailFound) {
            showToast(getString(R.string.todo_detail_not_found))
            navController.navigateUp()
        } else {
            showToast(getString(R.string.unknown_error_while_getting_todo_detail))
            cause.printStackTrace()
        }
    }
}