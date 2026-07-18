package com.nikhil.habit_money.auth.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nikhil.habit_money.auth.repository.AuthRepository;

public class AuthViewModelFactory implements ViewModelProvider.Factory {

    private final AuthRepository repository;

    public AuthViewModelFactory(AuthRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            return (T) new AuthViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
