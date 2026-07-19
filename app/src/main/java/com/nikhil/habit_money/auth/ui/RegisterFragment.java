package com.nikhil.habit_money.auth.ui;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nikhil.habit_money.BuildConfig;
import com.nikhil.habit_money.R;
import com.nikhil.habit_money.auth.repository.AuthRepository;
import com.nikhil.habit_money.auth.viewmodel.AuthViewModel;
import com.nikhil.habit_money.auth.viewmodel.AuthViewModelFactory;
import com.nikhil.habit_money.core.network.RetrofitClient;
import com.nikhil.habit_money.core.util.TokenManager;

import java.util.concurrent.Executor;

public class RegisterFragment extends Fragment {

    private AuthViewModel viewModel;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton registerButton;
    private MaterialButton googleSignInButton;
    private ProgressBar loadingSpinner;
    private View loginLink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstNameInput = view.findViewById(R.id.firstNameInput);
        lastNameInput = view.findViewById(R.id.lastNameInput);
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        registerButton = view.findViewById(R.id.registerButton);
        googleSignInButton = view.findViewById(R.id.googleSignInButton);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        loginLink = view.findViewById(R.id.loginLink);

        TokenManager tokenManager = new TokenManager(requireContext());
        AuthRepository repository = new AuthRepository(RetrofitClient.getApiService(tokenManager), tokenManager);
        AuthViewModelFactory factory = new AuthViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);

        registerButton.setOnClickListener(v -> attemptRegister());
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
        loginLink.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_register_to_login));

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingSpinner.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            registerButton.setEnabled(!isLoading);
            googleSignInButton.setEnabled(!isLoading);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getAuthResult().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_register_to_dashboard);
            }
        });
    }

    private void attemptRegister() {
        String firstName = firstNameInput.getText() != null ? firstNameInput.getText().toString().trim() : "";
        String lastName = lastNameInput.getText() != null ? lastNameInput.getText().toString().trim() : "";
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

        if (firstName.isEmpty()) {
            firstNameInput.setError("First name is required");
            return;
        }
        if (lastName.isEmpty()) {
            lastNameInput.setError("Last name is required");
            return;
        }
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            return;
        }

        viewModel.register(firstName, lastName, email, password);
    }

    private void signInWithGoogle() {
        CredentialManager credentialManager = CredentialManager.create(requireContext());

        GetGoogleIdOption option = new GetGoogleIdOption.Builder()
                .setServerClientId(BuildConfig.GOOGLE_SERVER_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build();

        Executor mainExecutor = requireActivity().getMainExecutor();

        credentialManager.getCredentialAsync(
                requireActivity(),
                request,
                new CancellationSignal(),
                mainExecutor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        GoogleIdTokenCredential credential = GoogleIdTokenCredential
                                .createFrom(result.getCredential().getData());
                        String idToken = credential.getIdToken();
                        viewModel.googleLogin(idToken);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        if (e instanceof NoCredentialException) {
                            return;
                        }
                        Toast.makeText(requireContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
