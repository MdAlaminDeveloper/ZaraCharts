package com.alamindev.zaracharts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SearchChartActivity extends AppCompatActivity {

    private TextInputEditText etSearchCutNo;
    private RecyclerView rvChartList;
    private DatabaseHelper dbHelper;
    private ChartAdapter adapter;
    private List<ChartModel> allChartsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_chart);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(SearchChartActivity.this, SplashActivity.class);
                // আগের সব এক্টিভিটি স্ট্যাক ক্লিয়ার করে ফ্রেশ SplashActivity ওপেন করবে
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // বর্তমান Activity বন্ধ করে দেবে
            }
        });

        dbHelper = new DatabaseHelper(this);
        etSearchCutNo = findViewById(R.id.etSearchCutNo);
        rvChartList = findViewById(R.id.rvChartList);

        rvChartList.setLayoutManager(new LinearLayoutManager(this));

        // DB থেকে সব চার্ট লোড
        allChartsList = dbHelper.getAllCharts();

        adapter = new ChartAdapter(allChartsList, chart -> {
            // লিস্টের যেকোনো চার্টে ক্লিক করলে Details page open হবে
            Intent intent = new Intent(SearchChartActivity.this, ViewChartActivity.class);
            intent.putExtra("CUTTING_NO", chart.getCutNo());
            startActivity(intent);
        });

        rvChartList.setAdapter(adapter);

        // রিয়েল-টাইম সার্চ ফিল্টার
        etSearchCutNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        List<ChartModel> filteredList = new ArrayList<>();
        for (ChartModel item : allChartsList) {
            if (item.getCutNo().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }
}