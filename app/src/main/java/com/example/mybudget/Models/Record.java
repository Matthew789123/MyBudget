package com.example.mybudget.Models;

import android.net.Uri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "record")
public class Record implements Serializable {

    public static final int Day = 0, Month = 1, Year = 2;

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String note;
    private double price;
    private Date date;
    private int type, categoryId;
    private String photoUri;

    public Record(int categoryId, double price, Date date, int type, String note) {
        this.categoryId = categoryId;
        this.price = price;
        this.date = date;
        this.type = type;
        this.note = note;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategory(int categoryId) { this.categoryId = categoryId; }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) { this.price = price; }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) { this.date = date; }

    public int getType() { return type; }

    public void setType(int type) { this.type = type; }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    public String getPhotoUri() { return photoUri; }

    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
}
