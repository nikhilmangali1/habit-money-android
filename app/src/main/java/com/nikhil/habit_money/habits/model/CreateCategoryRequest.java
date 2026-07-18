package com.nikhil.habit_money.habits.model;

public class CreateCategoryRequest {
    private String name;
    private String type;
    private String icon;

    public CreateCategoryRequest(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
