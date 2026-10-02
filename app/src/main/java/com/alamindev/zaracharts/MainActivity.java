package com.alamindev.zaracharts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etColour, etCuttingNo, etRatio, etSearchCutNo;
    private final List<LotInputModel> lotList = new ArrayList<>();
    private LotInputAdapter adapter;
    private DatabaseHelper dbHelper;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        dbHelper = new DatabaseHelper(this);

        etColour = findViewById(R.id.etColour);
        etCuttingNo = findViewById(R.id.etCuttingNo);
        etRatio = findViewById(R.id.etRatio);
        etSearchCutNo = findViewById(R.id.etSearchCutNo);

        RecyclerView rvInputLots = findViewById(R.id.rvInputLots);
        MaterialButton btnAddRow = findViewById(R.id.btnAddRow);
        btnSubmit = findViewById(R.id.btnSubmit);
        MaterialButton btnViewChart = findViewById(R.id.btnViewChart);

        adapter = new LotInputAdapter(lotList);
        rvInputLots.setLayoutManager(new LinearLayoutManager(this));
        rvInputLots.setAdapter(adapter);

        // Reset and set default initial row
        resetFormFields();

        btnAddRow.setOnClickListener(v -> {
            int lastEndBundle = getLastBundleNumberFromList();
            int nextStartBundle = lastEndBundle + 1;

            String autoBundleRange = nextStartBundle + "-";

            lotList.add(new LotInputModel("", "", 0, autoBundleRange));
            adapter.notifyItemInserted(lotList.size() - 1);
            rvInputLots.scrollToPosition(lotList.size() - 1);
        });

        btnSubmit.setOnClickListener(v -> saveDataToDB());
    }

    private int getLastBundleNumberFromList() {
        if (lotList.isEmpty()) {
            return 0;
        }

        LotInputModel lastItem = lotList.get(lotList.size() - 1);
        String lastBundleRange = lastItem.getBundleRange();

        if (lastBundleRange != null && lastBundleRange.contains("-")) {
            try {
                String[] parts = lastBundleRange.split("-");
                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    return Integer.parseInt(parts[1].trim());
                } else if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                    return Integer.parseInt(parts[0].trim());
                }
            } catch (Exception ignored) {
            }
        } else if (lastBundleRange != null && !lastBundleRange.trim().isEmpty()) {
            try {
                return Integer.parseInt(lastBundleRange.trim());
            } catch (Exception ignored) {
            }
        }
        return 0;
    }

    private void saveDataToDB() {
        String colour = etColour.getText().toString().trim();
        String cutNo = etCuttingNo.getText().toString().trim();
        String ratioStr = etRatio.getText().toString().trim();

        if (cutNo.isEmpty() || ratioStr.isEmpty()) {
            Toast.makeText(this, "Please Fill Cut No and Ratio!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2nd time click off korar jonno submit button disable
        btnSubmit.setEnabled(false);

        double ratio;
        try {
            ratio = Double.parseDouble(ratioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid Ratio value!", Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(true);
            return;
        }

        String defaultBuyer = "Zara Man";
        String defaultStyle = "1887/410";
        double defaultConsumptionKg = 0.120;

        double grandTotalPcs = 0;
        double grandTotalRibKg = 0;

        for (LotInputModel item : lotList) {
            double totalPcs = item.getLayQty() * ratio;
            double ribKg = (totalPcs / 12.0) * defaultConsumptionKg;
            grandTotalPcs += totalPcs;
            grandTotalRibKg += ribKg;
        }

        boolean chartSaved = dbHelper.insertChartData(defaultBuyer, defaultStyle, colour, cutNo, ratio, defaultConsumptionKg, grandTotalPcs, grandTotalRibKg);

        if (chartSaved) {
            for (LotInputModel item : lotList) {
                double totalPcs = item.getLayQty() * ratio;
                double ribKg = (totalPcs / 12.0) * defaultConsumptionKg;
                dbHelper.insertItemData(cutNo, item.getLotNo(), item.getShade(), item.getLayQty(), item.getBundleRange(), totalPcs, ribKg);
            }
            Toast.makeText(this, "Calculated & Saved Successfully!", Toast.LENGTH_SHORT).show();

            // Success hoile form reset ebong button enable kora
            resetFormFields();
        } else {
            Toast.makeText(this, "Error Saving Data!", Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(true); // Save error hoile button active kora
        }
    }

    // Input fields and list reset korar method
    private void resetFormFields() {
        if (etColour != null) etColour.setText("");
        if (etCuttingNo != null) etCuttingNo.setText("");
        if (etRatio != null) etRatio.setText("");
        if (etSearchCutNo != null) etSearchCutNo.setText("");

        lotList.clear();
        lotList.add(new LotInputModel("", "A", 0, ""));
        adapter.notifyDataSetChanged();

        if (btnSubmit != null) {
            btnSubmit.setEnabled(true);
        }
    }
}