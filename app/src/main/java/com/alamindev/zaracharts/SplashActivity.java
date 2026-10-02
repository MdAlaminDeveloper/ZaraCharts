package com.alamindev.zaracharts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class SplashActivity extends AppCompatActivity {
    MaterialButton btnInputChart, btnViewChart;
    private long backPressedTime = 0;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // ২ সেকেন্ডের (২০০০ মিলিফিসেকেন্ড) মধ্যে আবার প্রেস করলে সম্পূর্ণ বের হয়ে যাবে
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    if (backToast != null) backToast.cancel();
                    finishAndRemoveTask();
                    finishAffinity(); // ব্যাকগ্রাউন্ডের সব Activity বন্ধ করবে
                    System.exit(0);   // অ্যাপ প্রসেস কিল করবে
                } else {
                    backToast = Toast.makeText(SplashActivity.this, "Press back again to exit", Toast.LENGTH_SHORT);
                    backToast.show();
                }
                backPressedTime = System.currentTimeMillis();
            }
        });




        MaterialButton btnInputChart = findViewById(R.id.btnInputChart);
        MaterialButton btnViewChart = findViewById(R.id.btnViewChart);

        // Input Chart Button -> MainActivity (Input Page)
        btnInputChart.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // View Chart Button -> SearchChartActivity (Search Page)
        btnViewChart.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, SearchChartActivity.class);
            startActivity(intent);
        });
    }
}