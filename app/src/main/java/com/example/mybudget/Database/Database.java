package com.example.mybudget.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Insert;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mybudget.Models.Budget;
import com.example.mybudget.Models.Record;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {Record.class, Budget.class}, version = 1, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class Database extends RoomDatabase {

    public abstract DatabaseDao databaseDao();

    private static volatile Database Instance;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static Database getInstance(final Context context) {
        if (Instance == null) {
            synchronized (Database.class) {
                if (Instance == null)
                    Instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, "mybudget_database").allowMainThreadQueries().build();
            }
        }
        return Instance;
    }
}
