package com.example.mybudget.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.AlarmManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mybudget.Database.DatabaseViewModel;
import com.example.mybudget.Models.Budget;
import com.example.mybudget.Models.Category;
import com.example.mybudget.R;
import com.example.mybudget.Receivers.AlarmReceiver;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private static DatabaseViewModel db;

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
        db = ViewModelProviders.of(this).get(DatabaseViewModel.class);
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

            ComponentName receiver = new ComponentName(getApplicationContext(), AlarmReceiver.class);
            PackageManager pm = getApplicationContext().getPackageManager();
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1) % 12, 1, 0, 0, 0);

            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

            alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public static void month_passed() {
        Budget budget = db.getRawBudget();
        budget.setBudget(budget.getBudget() + budget.getMonthlyIncome());
        db.updateBudget(budget);
    }
}