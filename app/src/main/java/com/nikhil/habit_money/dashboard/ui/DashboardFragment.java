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
import com.nikhil.habit_money.core.util.TokenManager;

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

        TextView welcomeText = view.findViewById(R.id.welcomeText);
        View logoutButton = view.findViewById(R.id.logoutButton);
        MaterialCardView habitsCard = view.findViewById(R.id.habitsCard);
        MaterialCardView financesCard = view.findViewById(R.id.financesCard);
        MaterialCardView calendarCard = view.findViewById(R.id.calendarCard);

        String firstName = tokenManager.getFirstName();
        welcomeText.setText("Welcome" + (firstName != null ? ", " + firstName : "") + "!");

        logoutButton.setOnClickListener(v -> {
            tokenManager.clearAll();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_dashboard_to_login);
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
