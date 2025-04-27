package com.example.exchangemoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;



public class ExchangeRateActivity extends AppCompatActivity {

    private Spinner currencyFrom ;
    private ListView listViewRates;
    private CurrencyService currencyService;
    private ArrayAdapter<String> listAdapter;
    private Button btnConvert , btnHistory, btnDiagram;
    private ProgressBar progressBar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rate);


        btnConvert =  findViewById(R.id.btnConvert);
        // Thiết lập sự kiện khi nhấn vào btnConvert
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại MainActivity khi nhấn nút btnConvert
                Intent intent = new Intent(ExchangeRateActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Kết thúc HistoryConvertActivity để không giữ lại trong back stack
            }
        });

        btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result_code", 100); // 123 là con số bạn muốn gửi về
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        btnDiagram = findViewById(R.id.btnDiagram);
        btnDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang HistoryConvertActivity khi nhấn nút History
                Intent intent = new Intent(ExchangeRateActivity.this, DiagramActivity.class);
                startActivityForResult(intent , 200);
            }
        });

        currencyFrom = findViewById(R.id.spinnerFromCurrency);
        listViewRates = findViewById(R.id.listViewRates);
        progressBar = findViewById(R.id.progressBar);

        currencyService = new CurrencyService(this);
        currencyService.setupCurrencySpinners(currencyFrom, null); // chỉ setup spinner from

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listViewRates.setAdapter(listAdapter);

        currencyFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCurrency = parent.getSelectedItem().toString().split(" - ")[0];
                loadExchangeRates(selectedCurrency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadExchangeRates(String fromCurrency) {
        // Hiển thị ProgressBar khi bắt đầu tải dữ liệu
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                String urlString = "https://v6.exchangerate-api.com/v6/70850be6085375c0622897c1/latest/" + fromCurrency;
                URL url = new URL(urlString);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject rates = jsonObject.getJSONObject("conversion_rates");

                ArrayList<String> rateList = new ArrayList<>();

                for (int i = 0; i < rates.names().length(); i++) {
                    String currencyCode = rates.names().getString(i);
                    double rate = rates.getDouble(currencyCode);
                    DecimalFormat df = new DecimalFormat("#,###.##########");
                    String rateFormatted = df.format(rate);
                    rateList.add(currencyCode + " : " + rateFormatted);

                }

                runOnUiThread(() -> {
                    listAdapter.clear();
                    listAdapter.addAll(rateList);
                    listAdapter.notifyDataSetChanged();


                    // Ẩn ProgressBar khi tải xong
                    progressBar.setVisibility(View.GONE);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ExchangeRateActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    // Ẩn ProgressBar khi tải xong
                    progressBar.setVisibility(View.GONE);
                });

            }
        }).start();
    }
}
