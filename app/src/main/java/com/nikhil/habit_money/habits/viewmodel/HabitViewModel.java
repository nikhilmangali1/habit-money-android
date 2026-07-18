package com.nikhil.habit_money.habits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nikhil.habit_money.habits.model.Category;
import com.nikhil.habit_money.habits.model.CreateCategoryRequest;
import com.nikhil.habit_money.habits.model.CreateHabitRequest;
import com.nikhil.habit_money.habits.model.Habit;
import com.nikhil.habit_money.habits.model.HabitLog;
import com.nikhil.habit_money.habits.model.HabitLogRequest;
import com.nikhil.habit_money.habits.model.HabitTemplate;
import com.nikhil.habit_money.habits.model.UpdateHabitRequest;
import com.nikhil.habit_money.habits.repository.HabitCallback;
import com.nikhil.habit_money.habits.repository.HabitRepository;

import java.util.List;

public class HabitViewModel extends ViewModel {

    private final HabitRepository repository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<List<Habit>> habits = new MutableLiveData<>(null);
    private final MutableLiveData<Habit> selectedHabit = new MutableLiveData<>(null);
    private final MutableLiveData<List<HabitLog>> recentLogs = new MutableLiveData<>(null);
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>(null);
    private final MutableLiveData<List<HabitTemplate>> templates = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>(null);

    public HabitViewModel(HabitRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<List<Habit>> getHabits() { return habits; }
    public LiveData<Habit> getSelectedHabit() { return selectedHabit; }
    public LiveData<List<HabitLog>> getRecentLogs() { return recentLogs; }
    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<HabitTemplate>> getTemplates() { return templates; }
    public LiveData<Boolean> getSaveSuccess() { return saveSuccess; }

    public void loadHabits() {
        loading.setValue(true);
        error.setValue(null);

        repository.getHabits(new HabitCallback<List<Habit>>() {
            @Override
            public void onSuccess(List<Habit> data) {
                loading.setValue(false);
                habits.setValue(data);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }

    public void loadHabit(String id) {
        loading.setValue(true);
        error.setValue(null);

        repository.getHabit(id, new HabitCallback<Habit>() {
            @Override
            public void onSuccess(Habit data) {
                loading.setValue(false);
                selectedHabit.setValue(data);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }

    public void loadRecentLogs(String habitId, int days) {
        String endDate = java.time.LocalDate.now().toString();
        String startDate = java.time.LocalDate.now().minusDays(days).toString();

        repository.getHabitLogs(habitId, startDate, endDate, new HabitCallback<List<HabitLog>>() {
            @Override
            public void onSuccess(List<HabitLog> data) {
                recentLogs.setValue(data);
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
            }
        });
    }

    public void loadCategories(String type) {
        repository.getCategories(type, new HabitCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                categories.setValue(data);
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
            }
        });
    }

    public void loadTemplates() {
        repository.getTemplates(new HabitCallback<List<HabitTemplate>>() {
            @Override
            public void onSuccess(List<HabitTemplate> data) {
                templates.setValue(data);
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
            }
        });
    }

    public void createHabit(CreateHabitRequest request) {
        loading.setValue(true);
        error.setValue(null);
        saveSuccess.setValue(null);

        repository.createHabit(request, new HabitCallback<Habit>() {
            @Override
            public void onSuccess(Habit data) {
                loading.setValue(false);
                saveSuccess.setValue(true);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }

    public void updateHabit(String id, UpdateHabitRequest request) {
        loading.setValue(true);
        error.setValue(null);
        saveSuccess.setValue(null);

        repository.updateHabit(id, request, new HabitCallback<Habit>() {
            @Override
            public void onSuccess(Habit data) {
                loading.setValue(false);
                saveSuccess.setValue(true);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }

    public void deleteHabit(String id) {
        loading.setValue(true);
        error.setValue(null);

        repository.deleteHabit(id, new HabitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                loading.setValue(false);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }

    public void toggleLog(String habitId, boolean completed) {
        loading.setValue(true);
        String today = java.time.LocalDate.now().toString();

        if (completed) {
            HabitLogRequest request = new HabitLogRequest(today, "COMPLETED", null);
            repository.logHabit(habitId, request, new HabitCallback<HabitLog>() {
                @Override
                public void onSuccess(HabitLog data) {
                    loadHabits();
                    loadHabit(habitId);
                }

                @Override
                public void onError(String message) {
                    loading.setValue(false);
                    error.setValue(message);
                }
            });
        } else {
            repository.getHabitLogs(habitId, today, today, new HabitCallback<List<HabitLog>>() {
                @Override
                public void onSuccess(List<HabitLog> logs) {
                    for (HabitLog log : logs) {
                        if (today.equals(log.getLogDate())) {
                            repository.deleteLog(habitId, log.getId(), new HabitCallback<Void>() {
                                @Override
                                public void onSuccess(Void data) {
                                    loadHabits();
                                    loadHabit(habitId);
                                }

                                @Override
                                public void onError(String message) {
                                    loading.setValue(false);
                                    error.setValue(message);
                                }
                            });
                            return;
                        }
                    }
                    loading.setValue(false);
                }

                @Override
                public void onError(String message) {
                    loading.setValue(false);
                    error.setValue(message);
                }
            });
        }
    }

    public void createCategory(String name, String type) {
        loading.setValue(true);
        error.setValue(null);

        repository.createCategory(new CreateCategoryRequest(name, type), new HabitCallback<Category>() {
            @Override
            public void onSuccess(Category data) {
                loading.setValue(false);
                loadCategories(type);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }

    public void deleteCategory(String id, String type) {
        loading.setValue(true);
        error.setValue(null);

        repository.deleteCategory(id, new HabitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                loading.setValue(false);
                loadCategories(type);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }

    public void refreshCategories(String type) {
        loadCategories(type);
    }

    public void clearError() {
        error.setValue(null);
    }

    public void clearSaveSuccess() {
        saveSuccess.setValue(null);
    }
}
