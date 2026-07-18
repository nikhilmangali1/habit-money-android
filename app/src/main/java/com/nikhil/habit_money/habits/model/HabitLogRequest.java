package com.nikhil.habit_money.habits.model;

public class HabitLogRequest {
    private String logDate;
    private String status;
    private String note;

    public HabitLogRequest(String logDate, String status, String note) {
        this.logDate = logDate;
        this.status = status;
        this.note = note;
    }
}
