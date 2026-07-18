package com.nikhil.habit_money.habits.model;

public class HabitSummary {
    private String period;
    private int totalCompleted;
    private int totalMissed;
    private int totalSkipped;
    private double completionRate;
    private int currentStreak;
    private int bestStreak;

    public String getPeriod() { return period; }
    public int getTotalCompleted() { return totalCompleted; }
    public int getTotalMissed() { return totalMissed; }
    public int getTotalSkipped() { return totalSkipped; }
    public double getCompletionRate() { return completionRate; }
    public int getCurrentStreak() { return currentStreak; }
    public int getBestStreak() { return bestStreak; }
}
