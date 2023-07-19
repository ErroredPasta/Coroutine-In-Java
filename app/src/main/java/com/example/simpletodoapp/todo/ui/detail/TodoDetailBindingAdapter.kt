package com.example.simpletodoapp.todo.ui.detail

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("setTodoDetail")
fun TextInputEditText.setTodoDetail(state: TodoDetailState?) {
    if (state == null) return
    if (state is TodoDetailState.Success) setText(state.todoDetail.todo)
}

@BindingAdapter("setTodoDetailDescription")
fun TextInputEditText.setTodoDetailDescription(state: TodoDetailState?) {
    if (state == null) return
    if (state is TodoDetailState.Success) setText(state.todoDetail.description)
}