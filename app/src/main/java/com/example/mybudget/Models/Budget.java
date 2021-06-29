package com.example.mybudget.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "budget")
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double budget = 0;

    private double monthlyIncome = 0;

    public double getBudget() { return budget; }

    public void setBudget(double budget) { this.budget = budget; }

    public double getMonthlyIncome() { return monthlyIncome; }

    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }
}
