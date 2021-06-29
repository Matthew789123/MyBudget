package com.example.mybudget.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

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
import com.example.mybudget.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractRecordActivity extends AppCompatActivity {

    protected EditText amountEditText, noteEditText;
    protected Button saveButton, categoryButton;
    protected ImageView photoView, micAmount, micNote;
    protected ImageButton photoAddButton;
    protected int categoryId = -1;
    protected Budget budget;
    protected Uri photoUri = null;
    protected String photoPath;
    public static final String DATE_EXTRA = "date_extra", TYPE_EXTRA = "type_extra", CATEGORY_ID_EXTRA = "category_id_extra", RECORD_EXTRA = "record_extra";
    protected DatabaseViewModel db;
    private List<Category> categoriesList = new LinkedList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        categoryButton = findViewById(R.id.select_category_button);
        saveButton = findViewById(R.id.save_button);
        amountEditText = findViewById(R.id.amount_edit_text);
        noteEditText = findViewById(R.id.note_edit_text);
        photoView = findViewById(R.id.photo_image_view);
        photoAddButton = findViewById(R.id.photo_image_button);
        micAmount = findViewById(R.id.mic_amount);
        micNote = findViewById(R.id.mic_note);

        db = ViewModelProviders.of(this).get(DatabaseViewModel.class);

        db.getBudget().observe(this, new Observer<Budget>() {
            @Override
            public void onChanged(Budget myBudget) {
                budget = myBudget;
            }
        });

        db.getCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                categoriesList = categories;
            }
        });

        categoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SelectCategoryActivity.class);
            startActivityForResult(intent, 100);
        });

        photoAddButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(AbstractRecordActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 0);
                return;
            }
            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createPhoto();
            } catch (IOException ex) {
                return;
            }
            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intentCamera, 200);
            }
        });

        photoView.setOnClickListener(v -> {
            if (photoUri == null)
                return;
            Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
            intent.putExtra(PhotoActivity.PHOTO_EXTRA, photoUri.toString());
            startActivity(intent);
        });

        micAmount.setOnClickListener(v -> {
            startActivityForResult(createMicIntent(), 300);
        });

        micNote.setOnClickListener(v -> {
            startActivityForResult(createMicIntent(), 400);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            categoryId = data.getIntExtra(CATEGORY_ID_EXTRA, 0);
            categoryButton.setText(categoriesList.get(categoryId).getName());
            categoryButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(categoriesList.get(categoryId).getIcon()), null, getResources().getDrawable(R.drawable.ic_baseline_arrow_forward_ios_24), null);
        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            savePhoto();
            photoView.setImageURI(photoUri);
        } else if (requestCode == 300 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String res = result.get(0);

            try {
                res = res.replace(',', '.');
                Double.parseDouble(res);
            } catch (Exception ex) {
                return;
            }
            amountEditText.setText(res);
        } else if (requestCode == 400 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            noteEditText.setText(result.get(0));
        }
    }

    private File createPhoto() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        photoPath = image.getAbsolutePath();
        return image;
    }

    private void savePhoto() {
        if (photoUri != null) {
            File photoFile = new File(photoUri.getPath());
            photoFile.delete();
        }
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        photoUri = Uri.fromFile(f);
        mediaScanIntent.setData(photoUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Intent createMicIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.mic_prompt);
        return intent;
    }
}
