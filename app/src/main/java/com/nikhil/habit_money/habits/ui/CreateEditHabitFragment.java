package com.nikhil.habit_money.habits.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.nikhil.habit_money.R;
import com.nikhil.habit_money.core.network.RetrofitClient;
import com.nikhil.habit_money.core.util.TokenManager;
import com.nikhil.habit_money.habits.model.Category;
import com.nikhil.habit_money.habits.model.CreateHabitRequest;
import com.nikhil.habit_money.habits.model.Habit;
import com.nikhil.habit_money.habits.model.HabitTemplate;
import com.nikhil.habit_money.habits.model.UpdateHabitRequest;
import com.nikhil.habit_money.habits.repository.HabitRepository;
import com.nikhil.habit_money.habits.viewmodel.HabitViewModel;
import com.nikhil.habit_money.habits.viewmodel.HabitViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class CreateEditHabitFragment extends Fragment {

    private static final String ARG_HABIT_ID = "habitId";

    private HabitViewModel viewModel;

    private AutoCompleteTextView templateDropdown;
    private TextInputEditText titleInput;
    private TextInputEditText descriptionInput;
    private AutoCompleteTextView categoryDropdown;
    private AutoCompleteTextView frequencyDropdown;
    private Button saveButton;
    private Button manageCategoriesButton;
    private ProgressBar loadingSpinner;
    private TextView formTitle;
    private ImageButton backButton;

    private String habitId;
    private final List<Category> categories = new ArrayList<>();
    private final List<HabitTemplate> templates = new ArrayList<>();
    private String selectedCategoryId;
    private String selectedTemplateId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_edit_habit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        habitId = getArguments() != null ? getArguments().getString(ARG_HABIT_ID) : null;

        backButton = view.findViewById(R.id.formBackButton);
        formTitle = view.findViewById(R.id.formTitle);
        templateDropdown = view.findViewById(R.id.templateDropdown);
        titleInput = view.findViewById(R.id.titleInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        categoryDropdown = view.findViewById(R.id.categoryDropdown);
        frequencyDropdown = view.findViewById(R.id.frequencyDropdown);
        saveButton = view.findViewById(R.id.saveButton);
        manageCategoriesButton = view.findViewById(R.id.manageCategoriesButton);
        loadingSpinner = view.findViewById(R.id.formLoadingSpinner);

        TokenManager tokenManager = new TokenManager(requireContext());
        HabitRepository repository = new HabitRepository(RetrofitClient.getApiService(tokenManager), tokenManager);
        HabitViewModelFactory factory = new HabitViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(HabitViewModel.class);

        if (habitId != null) {
            formTitle.setText("Edit Habit");
            saveButton.setText("Update");
            viewModel.loadHabit(habitId);
        } else {
            formTitle.setText("New Habit");
        }

        viewModel.loadCategories("HABIT");
        viewModel.loadTemplates();

        getParentFragmentManager().setFragmentResultListener("categories_updated", this, (requestKey, result) ->
                viewModel.refreshCategories("HABIT"));

        setupFrequencyDropdown();

        backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        manageCategoriesButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_create_edit_to_category_manage));

        templateDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            HabitTemplate template = templates.get(position);
            selectedTemplateId = template.getId();
            titleInput.setText(template.getTitle());
            descriptionInput.setText(template.getDescription());
            if (template.getCategory() != null) {
                selectCategory(template.getCategory().getId());
            }
            frequencyDropdown.setText(template.getFrequency(), false);
        });

        categoryDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            selectedCategoryId = categories.get(position).getId();
        });

        saveButton.setOnClickListener(v -> validateAndSave());

        viewModel.getCategories().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                categories.clear();
                categories.addAll(data);
                updateCategoryDropdown();
            }
        });

        viewModel.getTemplates().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                templates.clear();
                templates.addAll(data);
                updateTemplateDropdown();
            }
        });

        viewModel.getSelectedHabit().observe(getViewLifecycleOwner(), habit -> {
            if (habit != null && habitId != null) {
                populateForEdit(habit);
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(),
                        habitId != null ? "Habit updated" : "Habit created", Toast.LENGTH_SHORT).show();
                viewModel.clearSaveSuccess();
                NavHostFragment.findNavController(this).popBackStack();
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
    }

    private void setupFrequencyDropdown() {
        String[] frequencies = {"DAILY", "WEEKLY", "MONTHLY"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, frequencies);
        frequencyDropdown.setAdapter(adapter);
    }

    private void updateTemplateDropdown() {
        List<String> names = new ArrayList<>();
        for (HabitTemplate template : templates) {
            names.add(template.getTitle());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, names);
        templateDropdown.setAdapter(adapter);
    }

    private void updateCategoryDropdown() {
        List<String> names = new ArrayList<>();
        for (Category cat : categories) {
            names.add(cat.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, names);
        categoryDropdown.setAdapter(adapter);
    }

    private void selectCategory(String categoryId) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(categoryId)) {
                categoryDropdown.setText(categories.get(i).getName(), false);
                selectedCategoryId = categoryId;
                return;
            }
        }
    }

    private void populateForEdit(Habit habit) {
        titleInput.setText(habit.getTitle());
        descriptionInput.setText(habit.getDescription());
        frequencyDropdown.setText(habit.getFrequency(), false);
        frequencyDropdown.setEnabled(true);
        if (habit.getCategory() != null) {
            selectCategory(habit.getCategory().getId());
        }
    }

    private void validateAndSave() {
        String title = titleInput.getText() != null ? titleInput.getText().toString().trim() : "";
        String description = descriptionInput.getText() != null ? descriptionInput.getText().toString().trim() : "";
        String frequency = frequencyDropdown.getText() != null ? frequencyDropdown.getText().toString().trim() : "";

        if (title.isEmpty() && selectedTemplateId == null) {
            titleInput.setError("Title is required");
            return;
        }
        if (selectedCategoryId == null) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (frequency.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a frequency", Toast.LENGTH_SHORT).show();
            return;
        }

        if (habitId != null) {
            Habit current = viewModel.getSelectedHabit().getValue();
            String status = current != null ? current.getStatus() : "ACTIVE";
            viewModel.updateHabit(habitId,
                    new UpdateHabitRequest(title, description, selectedCategoryId, frequency, status));
        } else {
            viewModel.createHabit(new CreateHabitRequest(
                    selectedTemplateId, title, description, selectedCategoryId, frequency));
        }
    }
}
