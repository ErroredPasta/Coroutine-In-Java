package com.example.simpletodoapp.todo.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.simpletodoapp.R
import com.example.simpletodoapp.databinding.FragmentTodoListBinding
import com.example.simpletodoapp.todo.ui.mapper.toTodo
import com.example.simpletodoapp.todo.ui.mapper.toTodoUiState
import com.example.simpletodoapp.todo.ui.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TodoListFragment : Fragment() {
    private val viewModel by viewModels<TodoListViewModel>()
    private val adapter by lazy { TodoAdapter() }
    private var recyclerView: RecyclerView? = null
    private val navController by lazy { findNavController() }

    private val itemTouchHelper by lazy {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.layoutPosition

                viewModel.deleteTodo(todo = adapter.currentList[position].toTodo())
                showToast(getString(R.string.todo_deleted))
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentTodoListBinding.inflate(inflater, container, false).apply {
        todoRecyclerView.adapter = adapter
        todoRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
        itemTouchHelper.attachToRecyclerView(todoRecyclerView)

        navigateTodoAddButton.setOnClickListener {
            navController.navigate(R.id.action_todoListFragment_to_todoInsertFragment)
        }

        root.setListeners(
            onSearchClick = viewModel::setSearchKeyword,
            onSearchHistoryClick = viewModel::setSearchKeyword,
            onSearchHistoryDeleteClick = viewModel::deleteSearchHistory
        )
    }.also { binding ->
        recyclerView = binding.todoRecyclerView

        viewModel.todos.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach { todos ->
            val todoUiStates = todos.map { todo ->
                todo.toTodoUiState(onClick = {
                    navigateToDetailScreen(todoId = todo.id)
                })
            }

            adapter.submitList(todoUiStates)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.searchHistories.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach {
            binding.root.submitList(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }.root

    override fun onDestroy() {
        super.onDestroy()
        recyclerView?.adapter = null
        recyclerView = null
    }

    private fun navigateToDetailScreen(todoId: Long) {
        navController.navigate(
            TodoListFragmentDirections.actionTodoListFragmentToTodoDetailFragment(todoId = todoId)
        )
    }
}