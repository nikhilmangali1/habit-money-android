package com.nikhil.habit_money.habits.model;

public class CreateHabitRequest {
    private String templateId;
    private String title;
    private String description;
    private String categoryId;
    private String frequency;

    public CreateHabitRequest(String templateId, String title, String description,
                              String categoryId, String frequency) {
        this.templateId = templateId;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.frequency = frequency;
    }
}
