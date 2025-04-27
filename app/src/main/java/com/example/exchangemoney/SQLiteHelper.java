package com.example.exchangemoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "currency_conversion.db";
    private static final int DATABASE_VERSION = 2; // Tăng version để SQLite gọi onUpgrade()

    public static final String TABLE_CONVERSION = "conversions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FROM_CURRENCY = "from_currency";
    public static final String COLUMN_TO_CURRENCY = "to_currency";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_RESULT = "result";
    public static final String COLUMN_DATE_TIME = "date_time"; // <-- Thêm cột mới

    private final SQLiteDatabase database;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CONVERSION + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FROM_CURRENCY + " TEXT, "
                + COLUMN_TO_CURRENCY + " TEXT, "
                + COLUMN_AMOUNT + " TEXT, "
                + COLUMN_RESULT + " TEXT, "
                + COLUMN_DATE_TIME + " TEXT)"; // <-- Thêm cột ngày tháng
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nếu cần, có thể viết lệnh ALTER TABLE thay vì DROP TABLE
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSION);
        onCreate(db);
    }

    // Thêm dữ liệu vào bảng
    public void insertConversion(String fromCurrency, String toCurrency, double amount, double result) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FROM_CURRENCY, fromCurrency);
        values.put(COLUMN_TO_CURRENCY, toCurrency);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_RESULT, result);

        // Lấy ngày giờ hiện tại
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put(COLUMN_DATE_TIME, currentDateTime); // <-- Ghi ngày giờ vào

        database.insert(TABLE_CONVERSION, null, values);
    }

    public Cursor getAllConversions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COLUMN_ID + " AS _id, "
                + COLUMN_FROM_CURRENCY + ", "
                + COLUMN_TO_CURRENCY + ", "
                + COLUMN_AMOUNT + ", "
                + COLUMN_RESULT + ", "
                + COLUMN_DATE_TIME // <-- Lấy thêm cột ngày giờ
                + " FROM " + TABLE_CONVERSION, null);
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
