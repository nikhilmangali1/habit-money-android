package com.nikhil.habit_money.auth.ui;

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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nikhil.habit_money.R;
import com.nikhil.habit_money.auth.repository.AuthRepository;
import com.nikhil.habit_money.auth.viewmodel.AuthViewModel;
import com.nikhil.habit_money.auth.viewmodel.AuthViewModelFactory;
import com.nikhil.habit_money.core.network.RetrofitClient;
import com.nikhil.habit_money.core.util.TokenManager;

public class LoginFragment extends Fragment {

    private AuthViewModel viewModel;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private ProgressBar loadingSpinner;
    private View registerLink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        registerLink = view.findViewById(R.id.registerLink);

        TokenManager tokenManager = new TokenManager(requireContext());
        AuthRepository repository = new AuthRepository(RetrofitClient.getApiService(tokenManager), tokenManager);
        AuthViewModelFactory factory = new AuthViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);

        loginButton.setOnClickListener(v -> attemptLogin());
        registerLink.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_login_to_register));

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingSpinner.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            loginButton.setEnabled(!isLoading);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getAuthResult().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                String name = response.getFirstName() != null ? response.getFirstName() : "";
                Toast.makeText(requireContext(), "Welcome" + (name.isEmpty() ? "" : ", " + name) + "!", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to DashboardFragment once created
            }
        });
    }

    private void attemptLogin() {
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            return;
        }

        viewModel.login(email, password);
    }
}
