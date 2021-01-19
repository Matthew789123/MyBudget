package com.example.mybudget.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybudget.R;

public class PhotoActivity extends AppCompatActivity {

    public static final String PHOTO_EXTRA = "photo_extra";
    private ImageView photoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photoView = findViewById(R.id.photo_image_view);
        photoView.setImageURI(Uri.parse(getIntent().getStringExtra(PHOTO_EXTRA)));
    }
}
