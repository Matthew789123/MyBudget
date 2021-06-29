package com.example.mybudget.Database;

import android.app.Application;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mybudget.Models.Budget;
import com.example.mybudget.Models.Category;
import com.example.mybudget.Models.Record;

import java.util.List;

public class DatabaseViewModel extends AndroidViewModel {

    private DatabaseDao databaseDao;
    private LiveData<List<Record>> records;
    private LiveData<Budget> budget;
    private LiveData<List<Category>> categories;

    public DatabaseViewModel(@NonNull Application application) {
        super(application);
        Database database = Database.getInstance(application);
        databaseDao = database.databaseDao();
        records = databaseDao.getAllRecords();
        budget = databaseDao.getBudget();
        categories = databaseDao.getCategories();
    }

    public LiveData<List<Record>> getAllRecords() {
        return records;
    }

    public void insertRecord(Record record) {
        Database.databaseWriteExecutor.execute(() -> {
            databaseDao.insertRecord(record);
        });
    }

    public void updateRecord(Record record) {
        Database.databaseWriteExecutor.execute(() -> {
            databaseDao.updateRecord(record);
        });
    }

    public void deleteRecord(Record record) {
        Database.databaseWriteExecutor.execute(() -> {
            databaseDao.deleteRecord(record);
        });
    }

    public void updateBudget(Budget budget) {
        Database.databaseWriteExecutor.execute(() -> {
            databaseDao.updateBudget(budget);
        });
    }

    public LiveData<Budget> getBudget() {
        return budget;
    }

    public Budget getRawBudget() { return databaseDao.getRawBudget(); }

    public void newBudget(Budget budget) { databaseDao.newBudget(budget); }

    public void newCategory(Category category) {
        Database.databaseWriteExecutor.execute(() -> {
            databaseDao.newCategory(category);
        });
    }
    
    public LiveData<List<Category>> getCategories() { return categories; }
}
