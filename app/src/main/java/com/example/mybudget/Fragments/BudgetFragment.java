package com.example.mybudget.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mybudget.Database.DatabaseViewModel;
import com.example.mybudget.Models.Budget;
import com.example.mybudget.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    private EditText addBudget, subtractBudget, monthlyIncome;
    private TextView currentBudgetText;
    private ImageView micAdd, micSubtract, micMonthly;
    private DatabaseViewModel db;
    private Budget currentBudget;
    private DecimalFormat decimalFormatter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        addBudget = view.findViewById(R.id.add_budget_edit_text);
        subtractBudget = view.findViewById(R.id.subtract_budget_edit_text);
        monthlyIncome = view.findViewById(R.id.monthly_income_edit_text);
        currentBudgetText = view.findViewById(R.id.budget_value_text_view);
        micAdd = view.findViewById(R.id.mic_add);
        micSubtract = view.findViewById(R.id.mic_subtract);
        micMonthly = view.findViewById(R.id.mic_monthly);

        monthlyIncome.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && !(monthlyIncome.getText().toString().equals("") || monthlyIncome.getText().toString().equals("."))) {
                String amountString = decimalFormatter.format(Double.parseDouble(monthlyIncome.getText().toString()));
                amountString = amountString.replace(',', '.');
                Double amount = Double.parseDouble(amountString);
                currentBudget.setMonthlyIncome(amount);
                db.updateBudget(currentBudget);
                return true;
            }
            return false;
        });

        addBudget.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && !(addBudget.getText().toString().equals("") || addBudget.getText().toString().equals("."))) {
                String amountString = decimalFormatter.format(Double.parseDouble(addBudget.getText().toString()));
                amountString = amountString.replace(',', '.');
                Double amount = Double.parseDouble(amountString);
                currentBudget.setBudget(currentBudget.getBudget() + amount);
                db.updateBudget(currentBudget);
                addBudget.setText("");
                return true;
            }
            return false;
        });
        subtractBudget.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && !(subtractBudget.getText().toString().equals("") || subtractBudget.getText().toString().equals("."))) {
                String amountString = decimalFormatter.format(Double.parseDouble(subtractBudget.getText().toString()));
                amountString = amountString.replace(',', '.');
                Double amount = Double.parseDouble(amountString);
                currentBudget.setBudget(currentBudget.getBudget() - amount);
                db.updateBudget(currentBudget);
                subtractBudget.setText("");
                return true;
            }
            return false;
        });

        decimalFormatter = new DecimalFormat("0.00");

        db = ViewModelProviders.of(this).get(DatabaseViewModel.class);
        db.getBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget) {
                currentBudget = budget;
                currentBudgetText.setText(decimalFormatter.format(currentBudget.getBudget()));
                monthlyIncome.setText(decimalFormatter.format(currentBudget.getMonthlyIncome()));
            }
        });

        micAdd.setOnClickListener(v -> {
            startActivityForResult(createMicIntent(), 100);
        });

        micSubtract.setOnClickListener(v -> {
            startActivityForResult(createMicIntent(), 200);
        });

        micMonthly.setOnClickListener(v -> {
            startActivityForResult(createMicIntent(), 300);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String res = result.get(0);

            try {
                res = res.replace(',', '.');
                Double.parseDouble(res);
            } catch (Exception ex) {
                return;
            }

            if (requestCode == 100)
                addBudget.setText(res);
            else if (requestCode == 200)
                subtractBudget.setText(res);
            else if (requestCode == 300) {
                String amountString = decimalFormatter.format(Double.parseDouble(res));
                amountString = amountString.replace(',', '.');
                Double amount = Double.parseDouble(amountString);
                currentBudget.setMonthlyIncome(amount);
                db.updateBudget(currentBudget);
            }
        }
    }

    private Intent createMicIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.mic_prompt);
        return intent;
    }
}
