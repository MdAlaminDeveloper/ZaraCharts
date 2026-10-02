package com.alamindev.zaracharts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RibCalculator.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CHART = "lay_chart";
    public static final String COL_CHART_ID = "id";
    public static final String COL_BUYER = "buyer";
    public static final String COL_STYLE = "style";
    public static final String COL_COLOUR = "colour";
    public static final String COL_CUT_NO = "cutting_no";
    public static final String COL_RATIO = "ratio";
    public static final String COL_CONSUMPTION = "consumption";
    public static final String COL_GRAND_PCS = "grand_pcs";
    public static final String COL_GRAND_RIB = "grand_rib";

    public static final String TABLE_ITEMS = "chart_items";
    public static final String COL_ITEM_ID = "item_id";
    public static final String COL_FOREIGN_CUT_NO = "cut_no_ref";
    public static final String COL_LOT_NO = "lot_no";
    public static final String COL_SHADE = "shade";
    public static final String COL_LAY_QTY = "lay_qty";
    public static final String COL_BUNDLE_RANGE = "bundle_range";
    public static final String COL_TOTAL_PCS = "total_pcs";
    public static final String COL_RIB_KG = "rib_kg";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createChartTable = "CREATE TABLE " + TABLE_CHART + " (" +
                COL_CHART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BUYER + " TEXT, " +
                COL_STYLE + " TEXT, " +
                COL_COLOUR + " TEXT, " +
                COL_CUT_NO + " TEXT UNIQUE, " +
                COL_RATIO + " REAL, " +
                COL_CONSUMPTION + " REAL, " +
                COL_GRAND_PCS + " REAL, " +
                COL_GRAND_RIB + " REAL)";

        String createItemsTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COL_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FOREIGN_CUT_NO + " TEXT, " +
                COL_LOT_NO + " TEXT, " +
                COL_SHADE + " TEXT, " +
                COL_LAY_QTY + " REAL, " +
                COL_BUNDLE_RANGE + " TEXT, " +
                COL_TOTAL_PCS + " REAL, " +
                COL_RIB_KG + " REAL)";

        db.execSQL(createChartTable);
        db.execSQL(createItemsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public boolean insertChartData(String buyer, String style, String colour, String cutNo, double ratio, double consumption, double grandPcs, double grandRib) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BUYER, buyer);
        values.put(COL_STYLE, style);
        values.put(COL_COLOUR, colour);
        values.put(COL_CUT_NO, cutNo);
        values.put(COL_RATIO, ratio);
        values.put(COL_CONSUMPTION, consumption);
        values.put(COL_GRAND_PCS, grandPcs);
        values.put(COL_GRAND_RIB, grandRib);

        long result = db.insertWithOnConflict(TABLE_CHART, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    // সংশোধন করা getAllCharts মেথড
    public List<ChartModel> getAllCharts() {
        List<ChartModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHART, null);

        if (cursor.moveToFirst()) {
            do {
                String cutNo = cursor.getString(cursor.getColumnIndexOrThrow(COL_CUT_NO));
                String colour = cursor.getString(cursor.getColumnIndexOrThrow(COL_COLOUR));
                double ratio = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RATIO));
                double grandTotalPcs = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_GRAND_PCS));

                list.add(new ChartModel(cutNo, colour, ratio, grandTotalPcs));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean insertItemData(String cutNo, String lotNo, String shade, double layQty, String bundleRange, double totalPcs, double ribKg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FOREIGN_CUT_NO, cutNo);
        values.put(COL_LOT_NO, lotNo);
        values.put(COL_SHADE, shade);
        values.put(COL_LAY_QTY, layQty);
        values.put(COL_BUNDLE_RANGE, bundleRange);
        values.put(COL_TOTAL_PCS, totalPcs);
        values.put(COL_RIB_KG, ribKg);

        long result = db.insert(TABLE_ITEMS, null, values);
        return result != -1;
    }

    public Cursor getChartByCutNo(String cutNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CHART + " WHERE " + COL_CUT_NO + " = ?", new String[]{cutNo});
    }

    public Cursor getItemsByCutNo(String cutNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COL_FOREIGN_CUT_NO + " = ?", new String[]{cutNo});
    }

    public boolean deleteChartByCutNo(String cutNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COL_FOREIGN_CUT_NO + " = ?", new String[]{cutNo});
        int deleted = db.delete(TABLE_CHART, COL_CUT_NO + " = ?", new String[]{cutNo});
        return deleted > 0;
    }
}