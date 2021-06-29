package com.example.mybudget.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.mybudget.Database.DatabaseViewModel;
import com.example.mybudget.Models.Budget;
import com.example.mybudget.Models.Category;
import com.example.mybudget.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    public static Category[] categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_records)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //getApplicationContext().deleteDatabase("mybudget_database");
        DatabaseViewModel db = ViewModelProviders.of(this).get(DatabaseViewModel.class);
        if (db.getRawBudget() == null) {
            db.newBudget(new Budget());
            db.newCategory(new Category(getResources().getString(R.string.food), R.drawable.food));
            db.newCategory(new Category(getResources().getString(R.string.travel), R.drawable.travel));
            db.newCategory(new Category(getResources().getString(R.string.living), R.drawable.living));
            db.newCategory(new Category(getResources().getString(R.string.car), R.drawable.car));
            db.newCategory(new Category(getResources().getString(R.string.clothing), R.drawable.clothing));
            db.newCategory(new Category(getResources().getString(R.string.entertainment), R.drawable.entertainment));
            db.newCategory(new Category(getResources().getString(R.string.fee), R.drawable.fee));
            db.newCategory(new Category(getResources().getString(R.string.investment), R.drawable.investment));
            db.newCategory(new Category(getResources().getString(R.string.others), R.drawable.others));
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}