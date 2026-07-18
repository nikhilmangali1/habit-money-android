package com.nikhil.habit_money.habits.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhil.habit_money.R;
import com.nikhil.habit_money.core.network.RetrofitClient;
import com.nikhil.habit_money.core.util.TokenManager;
import com.nikhil.habit_money.habits.model.Category;
import com.nikhil.habit_money.habits.repository.HabitRepository;
import com.nikhil.habit_money.habits.viewmodel.HabitViewModel;
import com.nikhil.habit_money.habits.viewmodel.HabitViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class CategoryManageFragment extends Fragment {

    private HabitViewModel viewModel;
    private RecyclerView listView;
    private ProgressBar loadingSpinner;
    private TextView emptyText;
    private CategoryManageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_manage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.categoryList);
        loadingSpinner = view.findViewById(R.id.categoryLoadingSpinner);
        emptyText = view.findViewById(R.id.emptyText);

        listView.setLayoutManager(new LinearLayoutManager(requireContext()));

        TokenManager tokenManager = new TokenManager(requireContext());
        HabitRepository repository = new HabitRepository(RetrofitClient.getApiService(tokenManager), tokenManager);
        viewModel = new ViewModelProvider(this, new HabitViewModelFactory(repository)).get(HabitViewModel.class);

        view.findViewById(R.id.categoryToolbar).setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putBoolean("updated", true);
            getParentFragmentManager().setFragmentResult("categories_updated", result);
            NavHostFragment.findNavController(this).popBackStack();
        });

        view.findViewById(R.id.addCategoryButton).setOnClickListener(v -> showAddDialog());

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                adapter = new CategoryManageAdapter(categories, this::onDeleteCategory);
                listView.setAdapter(adapter);
                emptyText.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading ->
                loadingSpinner.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        viewModel.loadCategories("HABIT");

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Bundle result = new Bundle();
                        result.putBoolean("updated", true);
                        getParentFragmentManager().setFragmentResult("categories_updated", result);
                        NavHostFragment.findNavController(CategoryManageFragment.this).popBackStack();
                    }
                });
    }

    private void showAddDialog() {
        EditText input = new EditText(requireContext());
        input.setHint("Category name");

        new AlertDialog.Builder(requireContext())
                .setTitle("New Category")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        viewModel.createCategory(name, "HABIT");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void onDeleteCategory(Category category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Delete \"" + category.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) ->
                        viewModel.deleteCategory(category.getId(), "HABIT"))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static class CategoryManageAdapter extends RecyclerView.Adapter<CategoryManageAdapter.ViewHolder> {
        private final List<Category> categories;
        private final OnDeleteListener listener;

        interface OnDeleteListener {
            void onDelete(Category category);
        }

        CategoryManageAdapter(List<Category> categories, OnDeleteListener listener) {
            this.categories = categories;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Category category = categories.get(position);
            holder.text1.setText(category.getName());
            holder.text2.setVisibility(View.GONE);

            if (category.isPredefined()) {
                holder.itemView.setAlpha(0.5f);
                holder.itemView.setOnClickListener(null);
            } else {
                holder.itemView.setAlpha(1f);
                holder.itemView.setOnClickListener(v -> listener.onDelete(category));
            }
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView text1;
            final TextView text2;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}
