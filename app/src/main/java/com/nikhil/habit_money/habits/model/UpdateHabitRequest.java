package com.nikhil.habit_money.habits.model;

public class UpdateHabitRequest {
    private String title;
    private String description;
    private String categoryId;
    private String frequency;
    private String status;

    public UpdateHabitRequest(String title, String description, String categoryId,
                              String frequency, String status) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.frequency = frequency;
        this.status = status;
    }
}
