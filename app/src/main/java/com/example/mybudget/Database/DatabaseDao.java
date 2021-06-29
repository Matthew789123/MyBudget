package com.example.mybudget.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mybudget.Models.Budget;
import com.example.mybudget.Models.Category;
import com.example.mybudget.Models.Record;

import java.util.List;

@Dao
public interface DatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertRecord(Record record);

    @Query("Select * From record")
    public LiveData<List<Record>> getAllRecords();

    @Update
    public void updateRecord(Record record);

    @Delete
    public void deleteRecord(Record record);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void newBudget(Budget budget);

    @Update
    public void updateBudget(Budget budget);

    @Query("Select * From budget")
    public LiveData<Budget> getBudget();

    @Query("Select * From budget")
    public Budget getRawBudget();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void newCategory(Category category);

    @Query("Select * From category")
    public LiveData<List<Category>> getCategories();
}
