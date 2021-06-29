package com.example.mybudget.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybudget.Activities.AddRecordActivity;
import com.example.mybudget.Activities.EditRecordActivity;
import com.example.mybudget.Activities.MainActivity;
import com.example.mybudget.Database.DatabaseViewModel;
import com.example.mybudget.Models.Budget;
import com.example.mybudget.Models.Category;
import com.example.mybudget.Models.Record;
import com.example.mybudget.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RecordsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private RecordAdapter adapter;
    private DatabaseViewModel db;
    private Date date;
    private TextView dateText, budgetText;
    private ImageButton leftArrow, rightArrow;
    private SimpleDateFormat formatter, monthFormatter, yearFormatter;
    private Calendar calendar;
    private List<Record> recordsList;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private int type = Record.Day;
    private FloatingActionButton addFloatingButton;
    private DecimalFormat decimalFormatter;
    private List<Category> categoriesList = new LinkedList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);
        setHasOptionsMenu(true);

        calendar = Calendar.getInstance();
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        monthFormatter = new SimpleDateFormat("MM/yyyy");
        yearFormatter = new SimpleDateFormat("yyyy");
        date = getDateWithoutTime(new Date(), formatter);

        dateText = view.findViewById(R.id.date_text_view);
        setDateText();
        budgetText = view.findViewById(R.id.budget_text_view);

        recyclerView = view.findViewById(R.id.record_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecordAdapter();
        recyclerView.setAdapter(adapter);

        db = ViewModelProviders.of(getActivity()).get(DatabaseViewModel.class);

        decimalFormatter = new DecimalFormat("0.00");

        db.getBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget) {
                budgetText.setText(getResources().getString(R.string.budget) + " " + getResources().getString(R.string.currency) + decimalFormatter.format(budget.getBudget()));
            }
        });

        db.getAllRecords().observe(getViewLifecycleOwner(), new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {
                recordsList = records;
                adapter.setRecords(records);
            }
        });

        db.getCategories().observe(getViewLifecycleOwner(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                categoriesList = categories;
            }
        });

        leftArrow = view.findViewById(R.id.left_arrow_image_view);
        rightArrow = view.findViewById(R.id.right_arrow_image_view);

        leftArrow.setOnClickListener(v -> {
            calendar.setTime(date);
            subtractDate(calendar);
            date = calendar.getTime();
            setDateText();
            adapter.setRecords(recordsList);
        });

        rightArrow.setOnClickListener(v -> {
            calendar.setTime(date);
            addDate(calendar);
            date = calendar.getTime();
            setDateText();
            adapter.setRecords(recordsList);
        });

        addFloatingButton = view.findViewById(R.id.add_floating_button);
        addFloatingButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddRecordActivity.class);
            intent.putExtra(AddRecordActivity.DATE_EXTRA, date);
            intent.putExtra(AddRecordActivity.TYPE_EXTRA, type);
            startActivity(intent);
        });

        return view;
    }

    private Date getDateWithoutTime(Date date, SimpleDateFormat formatter) {
        try {
            return formatter.parse(formatter.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private class RecordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView categoryImage;
        private TextView categoryText, dateText, priceText;
        private Record record;

        public RecordHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_record, parent, false));

            itemView.setOnClickListener(this);

            categoryImage = itemView.findViewById(R.id.category_image_view);
            categoryText = itemView.findViewById(R.id.category_text_view);
            dateText = itemView.findViewById(R.id.date_text_view);
            priceText = itemView.findViewById(R.id.price_text_view);
        }

        public void bind(Record record) {
            categoryText.setText(categoriesList.get(record.getCategoryId()).getName());
            dateText.setText(formatter.format(record.getDate()));
            priceText.setText(getResources().getString(R.string.currency) + record.getPrice());
            categoryImage.setImageResource(categoriesList.get(record.getCategoryId()).getIcon());
            this.record = record;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), EditRecordActivity.class);
            intent.putExtra(EditRecordActivity.RECORD_EXTRA, record);
            startActivity(intent);
        }
    }

    private class RecordAdapter extends RecyclerView.Adapter<RecordHolder> {

        private List<Record> records = new LinkedList<>();

        @NonNull
        @Override
        public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            return new RecordHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordHolder holder, int position) {
            Record record = records.get(position);
            holder.bind(record);
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        public void setRecords(List<Record> records) {
            clear();
            switch (type) {
                case Record.Day:
                    for (Record record : records) {
                        if (getDateWithoutTime(record.getDate(), formatter).equals(getDateWithoutTime(date, formatter)) && record.getType() == Record.Day)
                            this.records.add(record);
                    }
                    break;
                    case Record.Month:
                        for (Record record : records) {
                            if (getDateWithoutTime(record.getDate(), monthFormatter).equals(getDateWithoutTime(date, monthFormatter)) && record.getType() == Record.Month)
                                this.records.add(record);
                        }
                        break;
                        case Record.Year:
                            for (Record record : records) {
                                if (getDateWithoutTime(record.getDate(), yearFormatter).equals(getDateWithoutTime(date, yearFormatter)) && record.getType() == Record.Year)
                                    this.records.add(record);
                            }
                            break;
            }

            notifyDataSetChanged();
        }

        private void clear() {
            records = new LinkedList<>();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_records, menu);
        MenuItem item = menu.findItem(R.id.date_spinner);
        spinner = (Spinner) item.getActionView();

         spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinnerDates, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        type = position;
        setDateText();
        adapter.setRecords(recordsList);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setDateText() {
        switch (type) {
            case Record.Day:
                dateText.setText(formatter.format(date));
                break;
            case Record.Month:
                dateText.setText(monthFormatter.format(date));
                break;
            case Record.Year:
                dateText.setText(yearFormatter.format(date));
                break;
        }
    }

    private void addDate(Calendar calendar) {
        switch (type) {
            case Record.Day:
                calendar.add(Calendar.DATE, 1);
                break;
                case Record.Month:
                    calendar.add(Calendar.MONTH, 1);
                    break;
                    case Record.Year:
                        calendar.add(Calendar.YEAR, 1);
                        break;
        }
    }

    private void subtractDate(Calendar calendar) {
        switch (type) {
            case Record.Day:
                calendar.add(Calendar.DATE, -1);
                break;
            case Record.Month:
                calendar.add(Calendar.MONTH, -1);
                break;
            case Record.Year:
                calendar.add(Calendar.YEAR, -1);
                break;
        }
    }
}
