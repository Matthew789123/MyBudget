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
import android.widget.RelativeLayout;
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

public class EditRecordActivity extends AbstractRecordActivity {

    private Record record;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        record = (Record) getIntent().getSerializableExtra(RECORD_EXTRA);
        categoryId = record.getCategoryId();

        String uriString = record.getPhotoUri();
        if (uriString != null) {
            photoUri = Uri.parse(record.getPhotoUri());
            photoView.setImageURI(photoUri);
        }

        categoryButton.setText(MainActivity.categories[categoryId].getName());
        categoryButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(MainActivity.categories[categoryId].getIcon()), null, getResources().getDrawable(R.drawable.ic_baseline_arrow_forward_ios_24), null);
        amountEditText.setText(Double.toString(record.getPrice()));
        noteEditText.setText(record.getNote());

        saveButton.setOnClickListener(v -> {
            if (amountEditText.getText().toString().equals("") || amountEditText.getText().toString().equals(".")) {
                Toast.makeText(getApplicationContext(), R.string.invalid_amount, Toast.LENGTH_SHORT).show();
                return;
            }
            DecimalFormat decimalFormatter = new DecimalFormat("0.00");
            String amountString = decimalFormatter.format(Double.parseDouble(amountEditText.getText().toString()));
            amountString = amountString.replace(',', '.');
            Double amount = Double.parseDouble(amountString);
            budget.setBudget(budget.getBudget() + record.getPrice());
            budget.setBudget(budget.getBudget() - amount);
            record.setCategory(categoryId);
            record.setPrice(amount);
            record.setNote(noteEditText.getText().toString());
            if (photoUri != null)
                record.setPhotoUri(photoUri.toString());
            db.updateRecord(record);
            db.updateBudget(budget);
            finish();
        });

        RelativeLayout relativeLayout = findViewById(R.id.record_relative_layout);
        ImageButton deleteRecordButton = new ImageButton(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.note_edit_text);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, R.id.record_relative_layout);
        layoutParams.setMargins(getResources().getDimensionPixelOffset(R.dimen.zero), getResources().getDimensionPixelOffset(R.dimen.medium_margin), getResources().getDimensionPixelOffset(R.dimen.small_margin), getResources().getDimensionPixelOffset(R.dimen.zero));
        deleteRecordButton.setLayoutParams(layoutParams);
        deleteRecordButton.setImageResource(R.drawable.ic_baseline_delete_24);
        relativeLayout.addView(deleteRecordButton);

        deleteRecordButton.setOnClickListener(v -> {
            budget.setBudget(budget.getBudget() + record.getPrice());
            db.updateBudget(budget);
            db.deleteRecord(record);
            if (photoUri != null) {
                File photoFile = new File(photoUri.getPath());
                photoFile.delete();
            }
            finish();
        });
    }
}
