package com.example.mybudget.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mybudget.BuildConfig;
import com.example.mybudget.Database.DatabaseViewModel;
import com.example.mybudget.Models.Budget;
import com.example.mybudget.Models.Category;
import com.example.mybudget.Models.Record;
import com.example.mybudget.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddRecordActivity extends AbstractRecordActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveButton.setOnClickListener(v -> {
            if (amountEditText.getText().toString().equals("") || amountEditText.getText().toString().equals(".")) {
                Toast.makeText(getApplicationContext(), R.string.invalid_amount, Toast.LENGTH_SHORT).show();
                return;
            }
            if (categoryId == -1) {
                Toast.makeText(getApplicationContext(), R.string.invalid_category, Toast.LENGTH_SHORT).show();
                return;
            }
           DecimalFormat decimalFormatter = new DecimalFormat("0.00");
            String amountString = decimalFormatter.format(Double.parseDouble(amountEditText.getText().toString()));
            amountString = amountString.replace(',', '.');
           Double amount = Double.parseDouble(amountString);
           Date date = (Date)getIntent().getSerializableExtra(DATE_EXTRA);
           int type = getIntent().getIntExtra(TYPE_EXTRA, 0);
           Record record = new Record(categoryId, amount, date, type, noteEditText.getText().toString());
           if (photoUri != null)
               record.setPhotoUri(photoUri.toString());
           db.insertRecord(record);
           budget.setBudget(budget.getBudget() - amount);
           db.updateBudget(budget);
           finish();
        });
    }
}
