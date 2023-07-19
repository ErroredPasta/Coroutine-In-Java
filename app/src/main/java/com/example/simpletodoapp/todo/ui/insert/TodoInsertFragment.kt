package com.example.simpletodoapp.todo.ui.insert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.simpletodoapp.R
import com.example.simpletodoapp.databinding.FragmentTodoInsertBinding
import com.example.simpletodoapp.todo.domain.TodoDetail
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoInsertFragment : Fragment() {
    private val navController by lazy { findNavController() }
    private val viewModel by viewModels<TodoInsertViewModel>()
    private var showTodoError: ((CharSequence?) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentTodoInsertBinding.inflate(inflater, container, false).apply {
        todoAddButton.setOnClickListener {
            insertTodo(
                todo = todoInputLayout.inputText,
                description = todoDescriptionInputLayout.inputText
            )
        }
    }.also { binding ->
        showTodoError = binding.todoInputLayout::setError
    }.root

    private fun insertTodo(todo: String, description: String) {
        viewModel.insertTodo(
            todoDetail = TodoDetail(
                todo = todo,
                description = description.ifBlank { null }
            ),
            onSuccess = { navController.navigateUp() },
            onError = { showTodoError?.invoke(getString(R.string.todo_must_not_be_blank)) }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        showTodoError = null
    }

    private val TextInputLayout.inputText: String
        get() = this.editText?.text?.toString() ?: ""
}