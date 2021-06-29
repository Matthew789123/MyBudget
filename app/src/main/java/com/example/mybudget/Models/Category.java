package com.example.mybudget.Models;

import android.graphics.drawable.Drawable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.mybudget.R;

@Entity(tableName = "category")
public class Category {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int icon;

    public Category(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getIcon() { return icon; }

    public void setIcon(int icon) { this.icon = icon; }
}
