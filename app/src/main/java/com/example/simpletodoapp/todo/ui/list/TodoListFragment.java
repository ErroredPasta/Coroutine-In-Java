package com.example.simpletodoapp.todo.ui.list;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleCoroutineScope;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simpletodoapp.R;
import com.example.simpletodoapp.databinding.FragmentTodoListBinding;
import com.example.simpletodoapp.todo.domain.Todo;
import com.example.simpletodoapp.todo.ui.mapper.JavaTodoMapper;
import com.example.simpletodoapp.util.JavaFlowHelper;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

@AndroidEntryPoint
public class TodoListFragment extends Fragment {

    private FragmentTodoListBinding binding = null;
    private TodoListViewModel viewModel;
    private final TodoAdapter adapter = new TodoAdapter();
    private NavController navController;

    private final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(
                        @NonNull RecyclerView recyclerView,
                        @NonNull RecyclerView.ViewHolder viewHolder,
                        @NonNull RecyclerView.ViewHolder target
                ) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getLayoutPosition();

                    TodoUiState todoUiState = adapter.getCurrentList().get(position);
                    Todo todo = new Todo(todoUiState.getId(), todoUiState.getTodo(), todoUiState.getDescription());

                    viewModel.deleteTodo(todo);
                    Toast.makeText(requireContext(), getString(R.string.todo_deleted), Toast.LENGTH_SHORT).show();
                }
            }
    );


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTodoListBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(TodoListViewModel.class);
        navController = NavHostFragment.findNavController(this);

        setupRecyclerView();

        binding.navigateTodoAddButton.setOnClickListener(
                view -> navController.navigate(R.id.action_todoListFragment_to_todoInsertFragment)
        );

        binding.getRoot().setListeners(
                (keyword) -> {
                    viewModel.setSearchKeyword(keyword);
                    return Unit.INSTANCE;
                },
                (keyword) -> {
                    viewModel.setSearchKeyword(keyword);
                    return Unit.INSTANCE;
                },
                (keyword) -> {
                    viewModel.deleteSearchHistory(keyword);
                    return Unit.INSTANCE;
                }
        );

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();

        collectTodoFlow(viewLifecycleOwner);
        collectSearchHistoryFlow(viewLifecycleOwner);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.todoRecyclerView;
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), VERTICAL));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    private void collectTodoFlow(LifecycleOwner lifecycleOwner) {
        LifecycleCoroutineScope scope = JavaFlowHelper.getLifecycleScopeFromOwner(lifecycleOwner);

        Function1<Todo, Unit> mapper = todo -> {
            Bundle args = new Bundle();
            args.putLong("todo_id", todo.getId());

            navController.navigate(
                    R.id.action_todoListFragment_to_todoDetailFragment,
                    args
            );
            return Unit.INSTANCE;
        };

        JavaFlowHelper.collectWithLifecycleWithJava(
                scope, lifecycleOwner.getLifecycle(), viewModel.getTodos(), (todos) -> {
                    List<TodoUiState> uiStateList =  JavaTodoMapper.convertToTodoUiStateList(todos, mapper);
                    adapter.submitList(uiStateList);
                    return Unit.INSTANCE;
                }
        );
    }


    private void collectSearchHistoryFlow(LifecycleOwner lifecycleOwner) {
        LifecycleCoroutineScope scope = JavaFlowHelper.getLifecycleScopeFromOwner(lifecycleOwner);

        JavaFlowHelper.collectWithLifecycleWithJava(
                scope, lifecycleOwner.getLifecycle(), viewModel.getSearchHistories(), (searchHistoryList) -> {
                    binding.getRoot().submitList(searchHistoryList);
                    return Unit.INSTANCE;
                }
        );
    }
}
