package com.nikhil.habit_money.habits.repository;

public interface HabitCallback<T> {
    void onSuccess(T data);
    void onError(String message);
}
