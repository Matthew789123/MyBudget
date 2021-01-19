package com.example.mybudget.Models;

import android.graphics.drawable.Drawable;

import com.example.mybudget.R;

public class Category {

    private String name;
    private int icon;

    public Category(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() { return name; }

    public int getIcon() { return icon; }
}
