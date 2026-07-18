package com.nikhil.habit_money.habits.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nikhil.habit_money.habits.repository.HabitRepository;

public class HabitViewModelFactory implements ViewModelProvider.Factory {

    private final HabitRepository repository;

    public HabitViewModelFactory(HabitRepository repository) {
        this.repository = repository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HabitViewModel.class)) {
            return (T) new HabitViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
