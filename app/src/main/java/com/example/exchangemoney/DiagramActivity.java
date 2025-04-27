package com.example.exchangemoney;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class DiagramActivity extends AppCompatActivity {

    private Spinner spinnerFromCurrency, spinnerToCurrency;
    private Button buttonShowChart;
    private ProgressBar progressBar;
    private LineChart lineChart;
    private CurrencyService currencyService;
    private CurrencyConverter currencyConverter;
    private Button getBtnConvert , btnConvert, btnHistory, btnRate, btnDiagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram);

        // Liên kết các thành phần UI
        spinnerFromCurrency = findViewById(R.id.spinnerFromCurrency);
        spinnerToCurrency = findViewById(R.id.spinnerToCurrency);
        buttonShowChart = findViewById(R.id.buttonShowChart);
        progressBar = findViewById(R.id.progressBar);
        lineChart = findViewById(R.id.lineChart);

        btnConvert = findViewById(R.id.btnConvert);
        btnHistory = findViewById(R.id.btnHistory);
        btnRate = findViewById(R.id.btnExchangeRate);
        btnDiagram = findViewById(R.id.btnDiagram);


        // Liên kết các thành phần UI
        spinnerFromCurrency = findViewById(R.id.spinnerFromCurrency);
        spinnerToCurrency = findViewById(R.id.spinnerToCurrency);

        // Tải danh sách tiền tệ
        loadCurrencies();

        // Cấu hình sự kiện cho nút "Hiển thị biểu đồ"
        buttonShowChart.setOnClickListener(v -> showChart());

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang HistoryConvertActivity khi nhấn nút History
                Intent intent = new Intent(DiagramActivity.this, HistoryConvertActivity.class);
                startActivityForResult(intent , 100);
            }
        });

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang HistoryConvertActivity khi nhấn nút History
                Intent intent = new Intent(DiagramActivity.this, ExchangeRateActivity.class);
                startActivityForResult(intent , 200);
            }
        });

        btnDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang HistoryConvertActivity khi nhấn nút History
                Intent intent = new Intent(DiagramActivity.this, DiagramActivity.class);
                startActivityForResult(intent , 200);
            }
        });

        btnConvert =  findViewById(R.id.btnConvert);
        // Thiết lập sự kiện khi nhấn vào btnConvert
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại MainActivity khi nhấn nút btnConvert
                Intent intent = new Intent(DiagramActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Kết thúc HistoryConvertActivity để không giữ lại trong back stack
            }
        });

    }

    private void loadCurrencies() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Tạo URL để lấy danh sách tiền tệ
                    URL url = new URL("https://api.frankfurter.app/currencies");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    // Đọc dữ liệu trả về
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON
                    JSONObject jsonObject = new JSONObject(response.toString());
                    Iterator<String> keys = jsonObject.keys();

                    ArrayList<String> currencyList = new ArrayList<>();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        currencyList.add(key);  // Thêm mã tiền tệ vào danh sách
                    }

                    // Cập nhật UI trong thread chính
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(DiagramActivity.this,
                                    android.R.layout.simple_spinner_item, currencyList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerFromCurrency.setAdapter(adapter);
                            spinnerToCurrency.setAdapter(adapter);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DiagramActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void showChart() {
        // Lấy giá trị từ spinner
        String fromCurrency = spinnerFromCurrency.getSelectedItem().toString().split(" - ")[0];
        String toCurrency = spinnerToCurrency.getSelectedItem().toString().split(" - ")[0];

        // Bạn có thể cho người dùng chọn ngày, tạm thời fix từ 2024-04-01 tới 2024-04-10
        String startDate = "2024-04-01";
        String endDate = "2024-04-10";

        progressBar.setVisibility(View.VISIBLE);

        // Trước khi tải lại dữ liệu mới, cần reset lại trạng thái UI
        runOnUiThread(() -> {
            findViewById(R.id.lineChart).setVisibility(View.VISIBLE);  // Hiển thị lại biểu đồ
            findViewById(R.id.noDataLayout).setVisibility(View.GONE);  // Ẩn thông báo không có dữ liệu
        });

        new Thread(() -> {
            try {
                // Tạo URL cho API
                String urlString = "https://api.frankfurter.app/" + startDate + ".." + endDate + "?from=" + fromCurrency + "&to=" + toCurrency;
                Log.d("API Request", "URL: " + urlString);  // Log URL để kiểm tra

                URL url = new URL(urlString);

                // Kết nối API và đọc dữ liệu
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Log phản hồi từ API
                Log.d("API Response", response.toString());

                // Parse dữ liệu JSON
                JSONObject jsonObject = new JSONObject(response.toString());

                // Kiểm tra thông báo lỗi trong phản hồi JSON
                if (jsonObject.has("message") && jsonObject.getString("message").equals("not found")) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);  // Ẩn ProgressBar
                        // Hiển thị giao diện thông báo không có dữ liệu
                        findViewById(R.id.lineChart).setVisibility(View.GONE);  // Ẩn biểu đồ
                        findViewById(R.id.noDataLayout).setVisibility(View.VISIBLE);  // Hiển thị thông báo không có dữ liệu
                    });
                    return;  // Nếu không có dữ liệu, không vẽ biểu đồ nữa
                }

                // Lấy dữ liệu tỷ giá nếu không có lỗi
                JSONObject rates = jsonObject.getJSONObject("rates");
                ArrayList<Entry> entries = new ArrayList<>();
                ArrayList<String> dateLabels = new ArrayList<>();
                int index = 0;

                // Duyệt qua từng ngày trong rates và thêm dữ liệu vào entries và dateLabels
                for (Iterator<String> it = rates.keys(); it.hasNext(); ) {
                    String date = it.next();
                    double rate = rates.getJSONObject(date).getDouble(toCurrency);

                    entries.add(new Entry(index, (float) rate));  // Thêm Entry vào biểu đồ
                    dateLabels.add(date);  // Thêm ngày vào dateLabels
                    index++;
                }

                // Kiểm tra nếu không có dữ liệu trong entries
                if (entries.isEmpty()) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);  // Ẩn ProgressBar
                        // Hiển thị giao diện thông báo không có dữ liệu
                        findViewById(R.id.lineChart).setVisibility(View.GONE);  // Ẩn biểu đồ
                        findViewById(R.id.noDataLayout).setVisibility(View.VISIBLE);  // Hiển thị thông báo không có dữ liệu
                    });
                    return;  // Nếu không có dữ liệu, không vẽ biểu đồ nữa
                }

                // Cập nhật UI trong main thread
                runOnUiThread(() -> {
                    LineDataSet dataSet = new LineDataSet(entries, fromCurrency + " -> " + toCurrency);
                    dataSet.setLineWidth(2f);
                    dataSet.setCircleRadius(4f);
                    dataSet.setDrawValues(false);

                    LineData lineData = new LineData(dataSet);
                    lineChart.setData(lineData);

                    lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            // Làm tròn giá trị float thành int
                            int xIndex = (int) Math.round(value);  // Chuyển float thành int sau khi làm tròn
                            if (xIndex >= 0 && xIndex < dateLabels.size()) {
                                return dateLabels.get(xIndex);  // Trả về ngày tương ứng với chỉ số
                            } else {
                                return "";  // Trả về chuỗi rỗng nếu chỉ số không hợp lệ
                            }
                        }
                    });

                    lineChart.getXAxis().setGranularity(1f);  // Đảm bảo granularity bằng 1
                    lineChart.getXAxis().setLabelRotationAngle(-45);  // Xoay nhãn để dễ đọc

                    lineChart.invalidate();  // Cập nhật lại biểu đồ
                    progressBar.setVisibility(View.GONE);  // Ẩn ProgressBar khi hoàn tất
                });

            } catch (Exception e) {
                Log.e("Error", "Lỗi tải dữ liệu biểu đồ", e);  // Log lỗi chi tiết
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(DiagramActivity.this, "Lỗi tải dữ liệu biểu đồ", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);  // Ẩn ProgressBar nếu có lỗi
                    findViewById(R.id.lineChart).setVisibility(View.GONE);  // Ẩn biểu đồ
                    findViewById(R.id.noDataLayout).setVisibility(View.VISIBLE);  // Hiển thị thông báo không có dữ liệu
                });
            }
        }).start();
    }

}