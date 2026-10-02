package com.alamindev.zaracharts;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public class ViewChartActivity extends AppCompatActivity {

    private TextView tvBuyerStyle, tvColourCutNo, tvGrandTotal;
    private LinearLayout llItemContainer;
    private DatabaseHelper dbHelper;
    private String cuttingNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chart);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ViewChartActivity.this, SplashActivity.class);
                // আগের সব এক্টিভিটি স্ট্যাক ক্লিয়ার করে ফ্রেশ SplashActivity ওপেন করবে
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // বর্তমান Activity বন্ধ করে দেবে
            }
        });


        dbHelper = new DatabaseHelper(this);

        // Safe Intent Data Handling
        if (getIntent() != null && getIntent().hasExtra("CUTTING_NO")) {
            cuttingNo = getIntent().getStringExtra("CUTTING_NO");
        }

        tvBuyerStyle = findViewById(R.id.tvBuyerStyle);
        tvColourCutNo = findViewById(R.id.tvColourCutNo);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        llItemContainer = findViewById(R.id.llItemContainer);

        MaterialButton btnInputOk = findViewById(R.id.btnInputOk);
        MaterialButton btnCancel = findViewById(R.id.btnCancel);

        if (cuttingNo != null && !cuttingNo.trim().isEmpty()) {
            loadChartDetails();
        } else {
            Toast.makeText(this, "Cutting Number Not Found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnCancel.setOnClickListener(v -> finish());

        btnInputOk.setOnClickListener(v -> {
            boolean isDeleted = dbHelper.deleteChartByCutNo(cuttingNo);
            if (isDeleted) {
                Toast.makeText(this, "Record Confirmed & Deleted!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to delete record!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChartDetails() {
        Cursor masterCursor = null;
        Cursor itemsCursor = null;

        try {
            masterCursor = dbHelper.getChartByCutNo(cuttingNo);
            if (masterCursor != null && masterCursor.moveToFirst()) {

                int buyerIdx = masterCursor.getColumnIndex(DatabaseHelper.COL_BUYER);
                int styleIdx = masterCursor.getColumnIndex(DatabaseHelper.COL_STYLE);
                int colourIdx = masterCursor.getColumnIndex(DatabaseHelper.COL_COLOUR);
                int grandPcsIdx = masterCursor.getColumnIndex(DatabaseHelper.COL_GRAND_PCS);
                int grandRibIdx = masterCursor.getColumnIndex(DatabaseHelper.COL_GRAND_RIB);

                String buyer = (buyerIdx != -1) ? masterCursor.getString(buyerIdx) : "";
                String style = (styleIdx != -1) ? masterCursor.getString(styleIdx) : "";
                String colour = (colourIdx != -1) ? masterCursor.getString(colourIdx) : "";
                double grandPcs = (grandPcsIdx != -1) ? masterCursor.getDouble(grandPcsIdx) : 0;
                double grandRib = (grandRibIdx != -1) ? masterCursor.getDouble(grandRibIdx) : 0;

                tvBuyerStyle.setText(String.format("Buyer: %s | Style: %s", buyer, style));
                tvColourCutNo.setText(String.format("Colour: %s | Cut No: %s", colour, cuttingNo));
                tvGrandTotal.setText(String.format(Locale.US, "Grand Total: %.0f Pcs | Rib Needed: %.3f Kg", grandPcs, grandRib));
            } else {
                Toast.makeText(this, "No data found for Cut No: " + cuttingNo, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Items Details Rendering
            llItemContainer.removeAllViews();

            itemsCursor = dbHelper.getItemsByCutNo(cuttingNo);

            if (itemsCursor != null && itemsCursor.moveToFirst()) {
                int lotIdx = itemsCursor.getColumnIndex(DatabaseHelper.COL_LOT_NO);
                int shadeIdx = itemsCursor.getColumnIndex(DatabaseHelper.COL_SHADE);
                int bundleIdx = itemsCursor.getColumnIndex(DatabaseHelper.COL_BUNDLE_RANGE);
                int totalPcsIdx = itemsCursor.getColumnIndex(DatabaseHelper.COL_TOTAL_PCS);
                int ribKgIdx = itemsCursor.getColumnIndex(DatabaseHelper.COL_RIB_KG);
                int layIdx = itemsCursor.getColumnIndex("lay_qty"); // আপনার ডাটাবেজের Lay কলাম নাম

                do {
                    String lotNo = (lotIdx != -1) ? itemsCursor.getString(lotIdx) : "";
                    String shade = (shadeIdx != -1) ? itemsCursor.getString(shadeIdx) : "";
                    String bundleRange = (bundleIdx != -1) ? itemsCursor.getString(bundleIdx) : "";
                    double totalPcs = (totalPcsIdx != -1) ? itemsCursor.getDouble(totalPcsIdx) : 0;
                    double ribKg = (ribKgIdx != -1) ? itemsCursor.getDouble(ribKgIdx) : 0;
                    int layInput = (layIdx != -1) ? itemsCursor.getInt(layIdx) : 0;

                    // --- ১. মোট বান্ডিল হিসাব (যেমন: ১-৯ = ৯টি, ১৪-১৫ = ২টি) ---
                    int totalBundles = 1;
                    if (bundleRange.contains("-")) {
                        try {
                            String[] parts = bundleRange.split("-");
                            int start = Integer.parseInt(parts[0].trim());
                            int end = Integer.parseInt(parts[1].trim());
                            totalBundles = Math.abs(end - start) + 1;
                        } catch (Exception e) {
                            totalBundles = 1;
                        }
                    }

                    // --- ২. কার্ড ভিউ তৈরি ---
                    MaterialCardView card = new MaterialCardView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    // প্রথম এবং সব কার্ডের জন্য ওপরের স্পেস ঠিক রাখা
                    params.setMargins(5, 16, 5, 20);
                    card.setLayoutParams(params);
                    card.setRadius(10f);
                    card.setCardElevation(5f);
                    card.setStrokeColor(Color.parseColor("#E2E8F0"));
                    card.setStrokeWidth(5);
                    card.setCardBackgroundColor(Color.WHITE);

                    // --- ৩. টেক্সট সাজানো ও শেড লাল (Red) মার্ক করা ---
                    TextView tv = new TextView(this);

                    String line1 = String.format("📌 Lot: %s | Shade: %s | Bundle: %s (%d Lay)\n", lotNo, shade, bundleRange, layInput);
                    String line2 = String.format("📦 Total Bundle: %d %s\n", totalBundles, (totalBundles > 1 ? "Bundles" : "Bundle"));
                    String line3 = String.format(Locale.US, "👉 Total: %.0f Pcs | Rib Needed: %.3f Kg", totalPcs, ribKg);

                    String fullText = line1 + line2 + line3;
                    SpannableStringBuilder spannable = new SpannableStringBuilder(fullText);

                    // Shade টেক্সটকে লাল করার লজিক
                    String shadeTarget = "Shade: " + shade;
                    int shadeStart = fullText.indexOf(shadeTarget);
                    if (shadeStart != -1) {
                        int colorStart = shadeStart + "Shade: ".length();
                        int colorEnd = colorStart + shade.length();
                        // লাল কালার এবং বোল্ড
                        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#DC2626")), colorStart, colorEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new StyleSpan(Typeface.BOLD), colorStart, colorEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    tv.setText(spannable);
                    tv.setTextSize(16f);
                    tv.setPadding(36, 36, 36, 36); // চমৎকার উচ্চতার জন্য প্যাডিং
                    tv.setLineSpacing(12f, 1.15f);
                    tv.setTextColor(Color.parseColor("#1E293B"));

                    card.addView(tv);
                    llItemContainer.addView(card);

                } while (itemsCursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading data!", Toast.LENGTH_SHORT).show();
        } finally {
            if (masterCursor != null) masterCursor.close();
            if (itemsCursor != null) itemsCursor.close();
        }
    }
}