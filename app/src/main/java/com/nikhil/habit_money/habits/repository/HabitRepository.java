package com.nikhil.habit_money.habits.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nikhil.habit_money.core.network.ApiService;
import com.nikhil.habit_money.core.util.TokenManager;
import com.nikhil.habit_money.habits.model.Category;
import com.nikhil.habit_money.habits.model.CreateCategoryRequest;
import com.nikhil.habit_money.habits.model.CreateHabitRequest;
import com.nikhil.habit_money.habits.model.Habit;
import com.nikhil.habit_money.habits.model.HabitLog;
import com.nikhil.habit_money.habits.model.HabitLogRequest;
import com.nikhil.habit_money.habits.model.HabitSummary;
import com.nikhil.habit_money.habits.model.HabitTemplate;
import com.nikhil.habit_money.habits.model.UpdateHabitRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HabitRepository {

    private final ApiService apiService;
    private final TokenManager tokenManager;

    public HabitRepository(ApiService apiService, TokenManager tokenManager) {
        this.apiService = apiService;
        this.tokenManager = tokenManager;
    }

    // --- Habits ---

    public void getHabits(HabitCallback<List<Habit>> callback) {
        apiService.getHabits().enqueue(new Callback<List<Habit>>() {
            @Override
            public void onResponse(Call<List<Habit>> call, Response<List<Habit>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<Habit>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createHabit(CreateHabitRequest request, HabitCallback<Habit> callback) {
        apiService.createHabit(request).enqueue(new Callback<Habit>() {
            @Override
            public void onResponse(Call<Habit> call, Response<Habit> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Habit> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getHabit(String id, HabitCallback<Habit> callback) {
        apiService.getHabit(id).enqueue(new Callback<Habit>() {
            @Override
            public void onResponse(Call<Habit> call, Response<Habit> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Habit> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void updateHabit(String id, UpdateHabitRequest request, HabitCallback<Habit> callback) {
        apiService.updateHabit(id, request).enqueue(new Callback<Habit>() {
            @Override
            public void onResponse(Call<Habit> call, Response<Habit> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Habit> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void deleteHabit(String id, HabitCallback<Void> callback) {
        apiService.deleteHabit(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // --- Habit Logs ---

    public void logHabit(String habitId, HabitLogRequest request, HabitCallback<HabitLog> callback) {
        apiService.logHabit(habitId, request).enqueue(new Callback<HabitLog>() {
            @Override
            public void onResponse(Call<HabitLog> call, Response<HabitLog> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<HabitLog> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getHabitLogs(String habitId, String startDate, String endDate,
                             HabitCallback<List<HabitLog>> callback) {
        apiService.getHabitLogs(habitId, startDate, endDate).enqueue(new Callback<List<HabitLog>>() {
            @Override
            public void onResponse(Call<List<HabitLog>> call, Response<List<HabitLog>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<HabitLog>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void deleteLog(String habitId, String logId, HabitCallback<Void> callback) {
        apiService.deleteLog(habitId, logId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // --- Summaries ---

    public void getWeeklySummary(HabitCallback<HabitSummary> callback) {
        apiService.getWeeklySummary().enqueue(new Callback<HabitSummary>() {
            @Override
            public void onResponse(Call<HabitSummary> call, Response<HabitSummary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<HabitSummary> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getMonthlySummary(HabitCallback<HabitSummary> callback) {
        apiService.getMonthlySummary().enqueue(new Callback<HabitSummary>() {
            @Override
            public void onResponse(Call<HabitSummary> call, Response<HabitSummary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<HabitSummary> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void get10DaySummary(HabitCallback<HabitSummary> callback) {
        apiService.get10DaySummary().enqueue(new Callback<HabitSummary>() {
            @Override
            public void onResponse(Call<HabitSummary> call, Response<HabitSummary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<HabitSummary> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // --- Templates ---

    public void getTemplates(HabitCallback<List<HabitTemplate>> callback) {
        apiService.getTemplates().enqueue(new Callback<List<HabitTemplate>>() {
            @Override
            public void onResponse(Call<List<HabitTemplate>> call, Response<List<HabitTemplate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<HabitTemplate>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getTemplate(String id, HabitCallback<HabitTemplate> callback) {
        apiService.getTemplate(id).enqueue(new Callback<HabitTemplate>() {
            @Override
            public void onResponse(Call<HabitTemplate> call, Response<HabitTemplate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<HabitTemplate> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // --- Categories ---

    public void getCategories(String type, HabitCallback<List<Category>> callback) {
        apiService.getCategories(type).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createCategory(CreateCategoryRequest request, HabitCallback<Category> callback) {
        apiService.createCategory(request).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void deleteCategory(String id, HabitCallback<Void> callback) {
        apiService.deleteCategory(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // --- Shared ---

    private String extractErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                JsonObject json = new Gson().fromJson(errorJson, JsonObject.class);
                if (json != null && json.has("message")) {
                    return json.get("message").getAsString();
                }
            }
        } catch (Exception ignored) {}
        return "Something went wrong";
    }
}
