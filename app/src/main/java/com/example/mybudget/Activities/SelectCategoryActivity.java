package com.example.mybudget.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybudget.Database.DatabaseViewModel;
import com.example.mybudget.Models.Category;
import com.example.mybudget.R;

import java.util.LinkedList;
import java.util.List;

public class SelectCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private DatabaseViewModel db;
    private List<Category> categoriesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        db = ViewModelProviders.of(this).get(DatabaseViewModel.class);

        recyclerView = findViewById(R.id.category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new CategoryAdapter();

        db.getCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                categoriesList = categories;
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView categoryText;
        private ImageView categoryIcon;

        public CategoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_category, parent, false));

            itemView.setOnClickListener(this);

            categoryText = itemView.findViewById(R.id.category_text_view);
            categoryIcon = itemView.findViewById(R.id.category_image_view);
        }

        public void bind(Category category) {
            categoryText.setText(category.getName());
            categoryIcon.setImageResource(category.getIcon());
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra(AddRecordActivity.CATEGORY_ID_EXTRA, this.getAdapterPosition());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {

        @NonNull
        @Override
        public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new CategoryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
            holder.bind(categoriesList.get(position));
        }

        @Override
        public int getItemCount() {
            return categoriesList.size();
        }
    }
}
