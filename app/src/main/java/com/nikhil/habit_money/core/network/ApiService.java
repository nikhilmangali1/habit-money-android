package com.nikhil.habit_money.core.network;

import com.nikhil.habit_money.auth.model.AuthResponse;
import com.nikhil.habit_money.auth.model.LoginRequest;
import com.nikhil.habit_money.auth.model.LogoutRequest;
import com.nikhil.habit_money.auth.model.RefreshTokenRequest;
import com.nikhil.habit_money.auth.model.RegisterRequest;
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
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // --- Auth ---

    @POST("api/v1/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("api/v1/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/v1/auth/refresh")
    Call<AuthResponse> refreshToken(@Body RefreshTokenRequest request);

    @POST("api/v1/auth/logout")
    Call<Void> logout(@Body LogoutRequest request);

    // --- Habits ---

    @GET("api/v1/habits")
    Call<List<Habit>> getHabits();

    @POST("api/v1/habits")
    Call<Habit> createHabit(@Body CreateHabitRequest request);

    @GET("api/v1/habits/{id}")
    Call<Habit> getHabit(@Path("id") String id);

    @PUT("api/v1/habits/{id}")
    Call<Habit> updateHabit(@Path("id") String id, @Body UpdateHabitRequest request);

    @DELETE("api/v1/habits/{id}")
    Call<Void> deleteHabit(@Path("id") String id);

    // --- Habit Logs ---

    @POST("api/v1/habits/{id}/log")
    Call<HabitLog> logHabit(@Path("id") String id, @Body HabitLogRequest request);

    @GET("api/v1/habits/{id}/logs")
    Call<List<HabitLog>> getHabitLogs(
            @Path("id") String id,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @DELETE("api/v1/habits/{id}/log/{logId}")
    Call<Void> deleteLog(@Path("id") String id, @Path("logId") String logId);

    // --- Summaries ---

    @GET("api/v1/habits/summary/daily")
    Call<HabitSummary> getDailySummary();

    @GET("api/v1/habits/summary/weekly")
    Call<HabitSummary> getWeeklySummary();

    @GET("api/v1/habits/summary/monthly")
    Call<HabitSummary> getMonthlySummary();

    @GET("api/v1/habits/summary/10day")
    Call<HabitSummary> get10DaySummary();

    // --- Templates ---

    @GET("api/v1/habit-templates")
    Call<List<HabitTemplate>> getTemplates();

    @GET("api/v1/habit-templates/{id}")
    Call<HabitTemplate> getTemplate(@Path("id") String id);

    // --- Categories ---

    @GET("api/v1/categories")
    Call<List<Category>> getCategories(@Query("type") String type);

    @POST("api/v1/categories")
    Call<Category> createCategory(@Body CreateCategoryRequest request);

    @DELETE("api/v1/categories/{id}")
    Call<Void> deleteCategory(@Path("id") String id);
}
