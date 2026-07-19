package com.nikhil.habit_money.auth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nikhil.habit_money.auth.model.AuthResponse;
import com.nikhil.habit_money.auth.repository.AuthCallback;
import com.nikhil.habit_money.auth.repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<AuthResponse> authResult = new MutableLiveData<>(null);

    public AuthViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<AuthResponse> getAuthResult() { return authResult; }

    public void login(String email, String password) {
        loading.setValue(true);
        error.setValue(null);
        authResult.setValue(null);

        repository.login(email, password, new AuthCallback() {
            @Override
            public void onSuccess(AuthResponse response) {
                loading.setValue(false);
                authResult.setValue(response);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }

    public void register(String firstName, String lastName, String email, String password) {
        loading.setValue(true);
        error.setValue(null);
        authResult.setValue(null);

        repository.register(firstName, lastName, email, password, new AuthCallback() {
            @Override
            public void onSuccess(AuthResponse response) {
                loading.setValue(false);
                authResult.setValue(response);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }

    public void googleLogin(String idToken) {
        loading.setValue(true);
        error.setValue(null);
        authResult.setValue(null);

        repository.googleLogin(idToken, new AuthCallback() {
            @Override
            public void onSuccess(AuthResponse response) {
                loading.setValue(false);
                authResult.setValue(response);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }

    public void clearError() {
        error.setValue(null);
    }
}
