package com.nikhil.habit_money.habits.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import com.nikhil.habit_money.R;
import com.nikhil.habit_money.core.network.RetrofitClient;
import com.nikhil.habit_money.core.util.TokenManager;
import com.nikhil.habit_money.habits.model.Habit;
import com.nikhil.habit_money.habits.repository.HabitRepository;
import com.nikhil.habit_money.habits.viewmodel.HabitViewModel;
import com.nikhil.habit_money.habits.viewmodel.HabitViewModelFactory;

public class HabitsListFragment extends Fragment implements HabitAdapter.OnHabitClickListener {

    private HabitViewModel viewModel;
    private HabitAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar loadingSpinner;
    private FloatingActionButton addFab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habits_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.habitList);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        addFab = view.findViewById(R.id.addHabitFab);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        TokenManager tokenManager = new TokenManager(requireContext());
        HabitRepository repository = new HabitRepository(RetrofitClient.getApiService(tokenManager), tokenManager);
        HabitViewModelFactory factory = new HabitViewModelFactory(repository);
        viewModel = new ViewModelProvider(requireActivity(), factory).get(HabitViewModel.class);

        adapter = new HabitAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        addFab.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_habits_list_to_create));

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingSpinner.setVisibility(isLoading && adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        viewModel.getHabits().observe(getViewLifecycleOwner(), habits -> {
            if (habits != null) {
                adapter = new HabitAdapter(habits, this);
                recyclerView.setAdapter(adapter);
            }
        });

        viewModel.loadHabits();
    }

    @Override
    public void onHabitClick(Habit habit) {
        Bundle args = new Bundle();
        args.putString("habitId", habit.getId());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_habits_list_to_detail, args);
    }

    @Override
    public void onCheckChanged(Habit habit, boolean isChecked) {
        viewModel.toggleLog(habit.getId(), isChecked);
    }
}
