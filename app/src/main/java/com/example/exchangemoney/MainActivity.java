package com.example.exchangemoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private CurrencyService currencyService;
    private CurrencyConverter currencyConverter;
    private Spinner spinnerFromCurrency, spinnerToCurrency;

    private Button buttonConvert , btnConvert , btnHistory , btnRate, btnDiagram;
    private TextView textViewResult;

    private  EditText editTextAmount;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConvert = findViewById(R.id.btnConvert);
        btnHistory = findViewById(R.id.btnHistory);
        btnRate = findViewById(R.id.btnExchangeRate);
        btnDiagram = findViewById(R.id.btnDiagram);

        // Liên kết các thành phần UI
        spinnerFromCurrency = findViewById(R.id.spinnerFromCurrency);
        spinnerToCurrency = findViewById(R.id.spinnerToCurrency);
        editTextAmount = findViewById(R.id.editTextAmount);
        buttonConvert = findViewById(R.id.buttonConvert);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new android.text.method.ScrollingMovementMethod());

        currencyConverter = new CurrencyConverter(this);

        currencyService = new CurrencyService(this);

        currencyService.setupCurrencySpinners(spinnerFromCurrency, spinnerToCurrency);

        buttonConvert.setOnClickListener(v -> convertCurrency());

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang HistoryConvertActivity khi nhấn nút History
                Intent intent = new Intent(MainActivity.this, HistoryConvertActivity.class);
                startActivityForResult(intent , 100);
            }
        });

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang HistoryConvertActivity khi nhấn nút History
                Intent intent = new Intent(MainActivity.this, ExchangeRateActivity.class);
                startActivityForResult(intent , 200);
            }
        });

        btnDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang HistoryConvertActivity khi nhấn nút History
                Intent intent = new Intent(MainActivity.this, DiagramActivity.class);
                startActivityForResult(intent , 200);
            }
        });


    }

    private void convertCurrency() {
        if (spinnerFromCurrency.getAdapter() == null || spinnerToCurrency.getAdapter() == null) {
            Toast.makeText(this, "Danh sách tiền tệ chưa sẵn sàng", Toast.LENGTH_SHORT).show();
            return;
        }

        String fromCurrency = spinnerFromCurrency.getSelectedItem().toString().split(" - ")[0];
        String toCurrency = spinnerToCurrency.getSelectedItem().toString().split(" - ")[0];
        String amountStr = editTextAmount.getText().toString() ;

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        currencyConverter.convertCurrency(fromCurrency, toCurrency, amount, new CurrencyConverter.CurrencyConversionCallback() {
            @Override
            public void onConversionSuccess(double result) {
                // Hiển thị kết quả
                HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScrollView);
                String rateText = String.format("%s %s = %.4f %s", amountStr, fromCurrency, result, toCurrency);
                SpannableString spannableString = new SpannableString(rateText);
                textViewResult.setText(spannableString);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                int result = data.getIntExtra("result_code", -1); // -1 là mặc định nếu không có dữ liệu
                // Xử lý con số nhận được
                if(result == 200) {
                    Intent intent = new Intent(MainActivity.this, ExchangeRateActivity.class);
                    startActivityForResult(intent , 200);
                }
            }
        }

        if (requestCode == 200 && resultCode == RESULT_OK) {
            if (data != null) {
                int result = data.getIntExtra("result_code", -1); // -1 là mặc định nếu không có dữ liệu
                // Xử lý con số nhận được
                if(result == 100) {
                    Intent intent = new Intent(MainActivity.this, HistoryConvertActivity.class);
                    startActivityForResult(intent , 100);
                }
            }
        }
    }

}