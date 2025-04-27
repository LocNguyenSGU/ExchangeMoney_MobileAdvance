package com.example.exchangemoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryConvertActivity extends AppCompatActivity {

    private SQLiteHelper dbHelper;

    private ListView listView;
    private SimpleCursorAdapter adapter;

    private Button btnConvert ,  btnExchange;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_convert);



        // Khởi tạo SQLiteHelper và ListView
        dbHelper = new SQLiteHelper(this);
        listView = findViewById(R.id.historyListView);

        // Lấy dữ liệu từ cơ sở dữ liệu
        Cursor cursor = dbHelper.getAllConversions();

        // Đặt Adapter để hiển thị dữ liệu
        String[] fromColumns = {SQLiteHelper.COLUMN_FROM_CURRENCY, SQLiteHelper.COLUMN_TO_CURRENCY, SQLiteHelper.COLUMN_AMOUNT, SQLiteHelper.COLUMN_RESULT};
        int[] toViews = {R.id.conversionTextView};  // Chỉ sử dụng 1 TextView để hiển thị thông tin

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.history_item,  // Layout cho một dòng của ListView
                cursor,
                fromColumns,
                toViews,
                0
        ) {
            @Override
            public void bindView(View view, android.content.Context context, Cursor cursor) {
                super.bindView(view, context, cursor);

                @SuppressLint("Range") String fromCurrency = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_FROM_CURRENCY));
                @SuppressLint("Range") String toCurrency = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_TO_CURRENCY));
                @SuppressLint("Range") String amount = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_AMOUNT));
                @SuppressLint("Range") String result = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_RESULT));  // lấy kiểu double
                @SuppressLint("Range") String datetime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_DATE_TIME));  // lấy kiểu double

//                // Format kết quả để không bị dạng e+06
//                java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#,###.##");
//                String formattedResult = decimalFormat.format(result);
//                String formattedAmount = decimalFormat.format(amount);

                TextView conversionTextView = view.findViewById(R.id.conversionTextView);
                conversionTextView.setMovementMethod(new android.text.method.ScrollingMovementMethod());
                String conversionText =datetime + "\n"  + amount + " " + fromCurrency + " = " + result + " " + toCurrency;
                String htmlText="<span style=\"font-size : 16sp;\">" +  datetime + "</span><br>" +
                                "<span style=\"font-size : 20sp;\"><b>"  + amount + " " + fromCurrency + " = " + result + " " + toCurrency + "<b></span>";
                conversionTextView.setText(android.text.Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
                
            }

        };

        listView.setAdapter(adapter);

        // Khởi tạo nút btnConvert
        btnConvert = findViewById(R.id.btnConvert);

        // Thiết lập sự kiện khi nhấn vào btnConvert
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại MainActivity khi nhấn nút btnConvert
                Intent intent = new Intent(HistoryConvertActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Kết thúc HistoryConvertActivity để không giữ lại trong back stack
            }
        });
        btnExchange = findViewById(R.id.btnExchangeRate);
        btnExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result_code", 200); // 123 là con số bạn muốn gửi về
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        Button btnDiagram = findViewById(R.id.btnDiagram);
        btnDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang HistoryConvertActivity khi nhấn nút History
                Intent intent = new Intent(HistoryConvertActivity.this, DiagramActivity.class);
                startActivityForResult(intent , 200);
            }
        });

    }
}
