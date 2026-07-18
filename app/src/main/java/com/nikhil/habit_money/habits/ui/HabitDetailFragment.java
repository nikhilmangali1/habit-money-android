package com.nikhil.habit_money.habits.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhil.habit_money.R;
import com.nikhil.habit_money.core.network.RetrofitClient;
import com.nikhil.habit_money.core.util.TokenManager;
import com.nikhil.habit_money.habits.model.Habit;
import com.nikhil.habit_money.habits.repository.HabitCallback;
import com.nikhil.habit_money.habits.repository.HabitRepository;
import com.nikhil.habit_money.habits.viewmodel.HabitViewModel;
import com.nikhil.habit_money.habits.viewmodel.HabitViewModelFactory;

import java.util.List;

public class HabitDetailFragment extends Fragment {

    private static final String ARG_HABIT_ID = "habitId";

    private HabitViewModel viewModel;
    private HabitRepository habitRepository;
    private HabitLogAdapter logAdapter;

    private TextView titleText;
    private TextView descriptionText;
    private TextView categoryText;
    private TextView statusText;
    private TextView frequencyText;
    private TextView streakValue;
    private TextView bestStreakValue;
    private TextView totalValue;
    private TextView missedValue;
    private ProgressBar loadingSpinner;
    private ViewGroup logSection;
    private Button editButton;
    private Button deleteButton;
    private ImageButton backButton;

    private String habitId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habit_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        habitId = getArguments() != null ? getArguments().getString(ARG_HABIT_ID) : null;
        if (habitId == null) {
            Toast.makeText(getContext(), "Habit not found", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        titleText = view.findViewById(R.id.habitDetailTitle);
        descriptionText = view.findViewById(R.id.habitDetailDescription);
        categoryText = view.findViewById(R.id.habitDetailCategory);
        statusText = view.findViewById(R.id.habitDetailStatus);
        frequencyText = view.findViewById(R.id.habitDetailFrequency);
        streakValue = view.findViewById(R.id.streakValue);
        bestStreakValue = view.findViewById(R.id.bestStreakValue);
        totalValue = view.findViewById(R.id.totalValue);
        missedValue = view.findViewById(R.id.missedValue);
        loadingSpinner = view.findViewById(R.id.detailLoadingSpinner);
        logSection = view.findViewById(R.id.logSection);
        editButton = view.findViewById(R.id.editHabitButton);
        deleteButton = view.findViewById(R.id.deleteHabitButton);
        backButton = view.findViewById(R.id.detailBackButton);

        RecyclerView logsList = view.findViewById(R.id.habitLogsList);
        logsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        logAdapter = new HabitLogAdapter();
        logsList.setAdapter(logAdapter);

        TokenManager tokenManager = new TokenManager(requireContext());
        habitRepository = new HabitRepository(RetrofitClient.getApiService(tokenManager), tokenManager);
        HabitViewModelFactory factory = new HabitViewModelFactory(habitRepository);
        viewModel = new ViewModelProvider(requireActivity(), factory).get(HabitViewModel.class);

        backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        editButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("habitId", habitId);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_habit_detail_to_edit, args);
        });

        deleteButton.setOnClickListener(v -> confirmDelete());

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading ->
                loadingSpinner.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        viewModel.getSelectedHabit().observe(getViewLifecycleOwner(), habit -> {
            if (habit != null) {
                bindHabit(habit);
            }
        });

        viewModel.getRecentLogs().observe(getViewLifecycleOwner(), logs -> {
            if (logs != null) {
                logAdapter.setLogs(logs);
                logSection.setVisibility(logs.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });

        viewModel.loadHabit(habitId);
        viewModel.loadRecentLogs(habitId, 30);
    }

    private void bindHabit(Habit habit) {
        titleText.setText(habit.getTitle());
        descriptionText.setText(habit.getDescription() != null ? habit.getDescription() : "");
        categoryText.setText(habit.getCategory() != null ? habit.getCategory().getName() : "Uncategorized");
        statusText.setText(habit.getStatus());
        frequencyText.setText(habit.getFrequency());
        streakValue.setText(String.valueOf(habit.getStreakCount()));
        bestStreakValue.setText(String.valueOf(habit.getBestStreak()));
        totalValue.setText(String.valueOf(habit.getTotalCompletions()));
        missedValue.setText(String.valueOf(habit.getTotalMissed()));
    }

    private void refresh() {
        viewModel.loadHabit(habitId);
        viewModel.loadRecentLogs(habitId, 30);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete this habit? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteHabit(habitId);
                    NavHostFragment.findNavController(this).popBackStack();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}
