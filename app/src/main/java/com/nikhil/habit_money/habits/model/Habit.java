package com.nikhil.habit_money.habits.model;

public class Habit {
    private String id;
    private String title;
    private String description;
    private Category category;
    private String frequency;
    private String status;
    private String startDate;
    private String endDate;
    private int streakCount;
    private int bestStreak;
    private int totalCompletions;
    private int totalMissed;
    private boolean completedToday;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public String getFrequency() { return frequency; }
    public String getStatus() { return status; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getStreakCount() { return streakCount; }
    public int getBestStreak() { return bestStreak; }
    public int getTotalCompletions() { return totalCompletions; }
    public int getTotalMissed() { return totalMissed; }
    public boolean isCompletedToday() { return completedToday; }
}
