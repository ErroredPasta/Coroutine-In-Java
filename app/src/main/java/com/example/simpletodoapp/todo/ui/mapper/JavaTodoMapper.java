package com.example.simpletodoapp.todo.ui.mapper;

import com.example.simpletodoapp.todo.domain.Todo;
import com.example.simpletodoapp.todo.ui.list.TodoUiState;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class JavaTodoMapper {
    public static Todo convertToTodo(TodoUiState todoUiState) {
        return new Todo(
                todoUiState.getId(),
                todoUiState.getTodo(),
                todoUiState.getDescription()
        );
    }

    public static TodoUiState convertToTodoUiState(Todo todo, Function0<Unit> onClick) {
        return new TodoUiState(
                todo.getId(),
                todo.getTodo(),
                todo.getDescription(),
                onClick
        );
    }

    public static List<TodoUiState> convertToTodoUiStateList(List<Todo> todoList, Function1<Todo, Unit> onClick) {
        ArrayList<TodoUiState> result = new ArrayList<>();

        for (Todo todo : todoList) {
            result.add(convertToTodoUiState(todo, () -> {
                onClick.invoke(todo);
                return Unit.INSTANCE;
            }));
        }

        return result;
    }
}
