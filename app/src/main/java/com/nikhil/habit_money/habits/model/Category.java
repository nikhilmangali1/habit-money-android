package com.nikhil.habit_money.habits.model;

public class Category {
    private String id;
    private String name;
    private String type;
    private String icon;
    private boolean predefined;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getIcon() { return icon; }
    public boolean isPredefined() { return predefined; }
}
