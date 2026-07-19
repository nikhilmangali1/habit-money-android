package com.nikhil.habit_money.dashboard.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.card.MaterialCardView;
import com.nikhil.habit_money.R;
import com.nikhil.habit_money.auth.model.AuthResponse;
import com.nikhil.habit_money.auth.repository.AuthCallback;
import com.nikhil.habit_money.auth.repository.AuthRepository;
import com.nikhil.habit_money.core.network.RetrofitClient;
import com.nikhil.habit_money.core.util.TokenManager;
import com.nikhil.habit_money.habits.model.Habit;
import com.nikhil.habit_money.habits.model.HabitSummary;
import com.nikhil.habit_money.habits.repository.HabitCallback;
import com.nikhil.habit_money.habits.repository.HabitRepository;

import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TokenManager tokenManager = new TokenManager(requireContext());
        AuthRepository authRepository = new AuthRepository(
                RetrofitClient.getApiService(tokenManager), tokenManager);
        HabitRepository habitRepository = new HabitRepository(
                RetrofitClient.getApiService(tokenManager), tokenManager);

        TextView welcomeText = view.findViewById(R.id.welcomeText);
        View logoutButton = view.findViewById(R.id.logoutButton);
        MaterialCardView habitsCard = view.findViewById(R.id.habitsCard);
        MaterialCardView financesCard = view.findViewById(R.id.financesCard);
        MaterialCardView calendarCard = view.findViewById(R.id.calendarCard);

        TextView todayRate = view.findViewById(R.id.todayRate);
        TextView weekRate = view.findViewById(R.id.weekRate);
        TextView monthRate = view.findViewById(R.id.monthRate);
        TextView habitsStatus = view.findViewById(R.id.habitsStatus);

        String firstName = tokenManager.getFirstName();
        welcomeText.setText("Welcome" + (firstName != null ? ", " + firstName : "") + "!");

        // Load habits count
        habitRepository.getHabits(new HabitCallback<List<Habit>>() {
            @Override
            public void onSuccess(List<Habit> data) {
                habitsStatus.setText(data.size() + " active habits");
            }

            @Override
            public void onError(String message) {
                habitsStatus.setText("0 active habits");
            }
        });

        // Load daily summary
        habitRepository.getDailySummary(new HabitCallback<HabitSummary>() {
            @Override
            public void onSuccess(HabitSummary data) {
                todayRate.setText(String.format(Locale.getDefault(), "%.1f%%",
                        data.getCompletionRate()));
            }

            @Override
            public void onError(String message) {
                todayRate.setText("-");
            }
        });

        // Load weekly summary
        habitRepository.getWeeklySummary(new HabitCallback<HabitSummary>() {
            @Override
            public void onSuccess(HabitSummary data) {
                weekRate.setText(String.format(Locale.getDefault(), "%.1f%%",
                        data.getCompletionRate()));
            }

            @Override
            public void onError(String message) {
                weekRate.setText("-");
            }
        });

        // Load monthly summary
        habitRepository.getMonthlySummary(new HabitCallback<HabitSummary>() {
            @Override
            public void onSuccess(HabitSummary data) {
                monthRate.setText(String.format(Locale.getDefault(), "%.1f%%",
                        data.getCompletionRate()));
            }

            @Override
            public void onError(String message) {
                monthRate.setText("-");
            }
        });

        logoutButton.setOnClickListener(v -> {
            authRepository.logout(new AuthCallback() {
                @Override
                public void onSuccess(AuthResponse response) {
                    NavHostFragment.findNavController(DashboardFragment.this)
                            .navigate(R.id.action_dashboard_to_login);
                }

                @Override
                public void onError(String message) {
                    tokenManager.clearAll();
                    NavHostFragment.findNavController(DashboardFragment.this)
                            .navigate(R.id.action_dashboard_to_login);
                }
            });
        });

        habitsCard.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.habits_list_fragment));

        View.OnClickListener comingSoon = v ->
                Toast.makeText(requireContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
        financesCard.setOnClickListener(comingSoon);
        calendarCard.setOnClickListener(comingSoon);
    }
}
