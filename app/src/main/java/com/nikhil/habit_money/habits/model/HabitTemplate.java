package com.nikhil.habit_money.habits.model;

public class HabitTemplate {
    private String id;
    private String title;
    private String description;
    private Category category;
    private String frequency;
    private String icon;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public String getFrequency() { return frequency; }
    public String getIcon() { return icon; }
}
