package com.example.exchangemoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


public class ExchangeRateActivity extends AppCompatActivity {

    private AutoCompleteTextView currencyFrom ;
    private ListView listViewRates;
    private CurrencyService currencyService;
    private ArrayAdapter<String> listAdapter;
    private Button btnConvert , btnHistory, btnDiagram ,  btnShowChart;
    private ProgressBar progressBar;

    private LineChart lineChart;

    private ArrayList<String> latestRatesList = new ArrayList<>();
    private JSONObject latestRatesJSON = new JSONObject();

    private boolean isLoading = true ;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRatesRunnable;
    private Thread updateThread ;
    private boolean running = true ;

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


        currencyFrom = findViewById(R.id.spinnerFromCurrency);
        listViewRates = findViewById(R.id.listViewRates);
        progressBar = findViewById(R.id.progressBar);

        currencyService = new CurrencyService(this);
        currencyService.setupCurrencySpinners(currencyFrom, currencyFrom); // chỉ setup spinner from

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, latestRatesList);
        listViewRates.setAdapter(listAdapter);

//        currencyFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String selectedCurrency = currencyFrom.getText().toString().split(" - ")[0];
//                loadExchangeRates(selectedCurrency);
//            }
//        });

        currencyFrom.setThreshold(0);  // Đặt ngưỡng cho phép tìm kiếm (0 nghĩa là tìm kiếm ngay lập tức)
        currencyFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() == 0) {  // Khi không có văn bản nào
                    currencyFrom.showDropDown(); // Mở danh sách tự động
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String selectedCurrency = editable.toString().split("-")[0];
                loadExchangeRates(selectedCurrency);
            }
        });


        lineChart = findViewById(R.id.lineChart);
        btnShowChart = findViewById(R.id.btnShowChart);
        btnShowChart.setOnClickListener(v -> {
            if (latestRatesJSON.length() > 0) {
                // Đảm bảo rằng các dữ liệu biểu đồ không bị xóa và vẽ lại đúng cách
                drawLineChart(latestRatesJSON , latestRatesList);
                lineChart.setVisibility(lineChart.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                listViewRates.setVisibility(listViewRates.getVisibility() == View.GONE ?  View.VISIBLE : View.GONE);
            } else {
                Toast.makeText(this, "Chưa có dữ liệu để vẽ!", Toast.LENGTH_SHORT).show();
            }
        });


        startUpdatingRates();
    }

    private void loadExchangeRates(String fromCurrency) {
        // Hiển thị ProgressBar khi bắt đầu tải dữ liệu
//        progressBar.setVisibility(View.VISIBLE);
        isLoading = true;
        new Thread(() -> {
            try {
                String urlString = "https://v6.exchangerate-api.com/v6/cca8e572cd03d97fd28a7413/latest/" + fromCurrency;
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

                latestRatesList.clear();
                latestRatesJSON = rates;

                ArrayList<String> rateList = new ArrayList<>();

                for (int i = 0; i < rates.names().length(); i++) {
                    String currencyCode = rates.names().getString(i);
                    double rate = rates.getDouble(currencyCode);
                    DecimalFormat df = new DecimalFormat("#,###.##########");
                    String rateFormatted = df.format(rate);
                    rateList.add(currencyCode + " : " + rateFormatted);
                    latestRatesList.add(currencyCode + " : " + rateFormatted);

                }

                runOnUiThread(() -> {
                    listAdapter.clear();
                    listAdapter.addAll(rateList);
                    listAdapter.notifyDataSetChanged();

                    // Ẩn ProgressBar khi tải xong
                    progressBar.setVisibility(View.GONE);
                    drawLineChart(latestRatesJSON , latestRatesList);
                    listAdapter.notifyDataSetChanged();
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

    private void drawLineChart(JSONObject rates, ArrayList<String> orderedCurrencies) {
        System.out.println("CAP NHAT TI GIA MOI NHAT");

        if (!isLoading) return;
        try {
            isLoading = false;
            ArrayList<Entry> entries = new ArrayList<>();
            final ArrayList<String> labels = new ArrayList<>();

            for (int i = 0; i < orderedCurrencies.size(); i++) {
                String currency = orderedCurrencies.get(i).split(":")[0].trim();  // Đảm bảo split chính xác
                if (rates.has(currency)) {
                    double rate = rates.getDouble(currency);

                    // Định dạng tỷ giá với 5 chữ số sau dấu phẩy
                    DecimalFormat df = new DecimalFormat("#.#####");
                    String formattedRate = df.format(rate); // Tỷ giá đã được định dạng

                    // Chuyển đổi tỷ giá thành float để vẽ lên biểu đồ
                    float formattedRateFloat = Float.parseFloat(formattedRate);

                    entries.add(new Entry(i, formattedRateFloat));  // Thêm vào entries
                    labels.add(currency);  // Thêm vào labels
                }
            }

            // Khởi tạo LineDataSet chỉ một lần
            if (entries.size() > 0) {
                LineDataSet dataSet = new LineDataSet(entries, "Tỷ giá tiền tệ");
                dataSet.setColor(getResources().getColor(R.color.purple_500));
                dataSet.setValueTextColor(getResources().getColor(R.color.black));
                dataSet.setCircleColor(getResources().getColor(R.color.purple_700));
                dataSet.setLineWidth(2f);
                dataSet.setCircleRadius(4f);
                dataSet.setValueTextSize(10f);

                // Cấu hình ValueFormatter để hiển thị số thập phân
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        // Định dạng số thập phân với 5 chữ số
                        DecimalFormat df = new DecimalFormat("#.#####");
                        return df.format(value);
                    }
                });

                MyMakerView markerView = new MyMakerView(this, labels);
                markerView.setChartView(lineChart); // rất quan trọng
                lineChart.setMarker(markerView);


                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.invalidate();  // Đảm bảo rằng dữ liệu được cập nhật và vẽ lại

                // Cập nhật XAxis
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setLabelRotationAngle(-45);

                // Cấu hình các thuộc tính khác của biểu đồ
                lineChart.setDragEnabled(true);
                lineChart.setScaleEnabled(false);
                lineChart.setScaleXEnabled(false);
                lineChart.setScaleYEnabled(false);
                lineChart.setPinchZoom(false);

                // Cập nhật mức zoom nếu cần
                if (lineChart.getScaleX() != 39.2f) {
                    lineChart.zoomToCenter(39.2f, 1f);
                }

                Description description = new Description();
                description.setText("Biểu đồ tỷ giá");
                lineChart.setDescription(description);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi vẽ biểu đồ", Toast.LENGTH_SHORT).show();
        }
    }


    private void startUpdatingRates() {
        updateRatesRunnable = new Runnable() {
            @Override
            public void run() {

                System.out.println("BAT DAU CAP NHAT");
            }
        };
        handler.post(updateRatesRunnable);  // Bắt đầu cập nhật tỷ giá

        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                while (running) {
                    if(currencyFrom.getText() != null){
                        String selectedCurrency = currencyFrom.getText().toString().split(" - ")[0];
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // --> Code thay đổi giao diện nằm ở đây
//                                loadExchangeRates(selectedCurrency);
                            }
                        });
                    }
                    try {
                        Thread.sleep(5000); // Nghỉ 1 giây
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break; // Nếu bị gián đoạn thì thoát khỏi vòng lặp
                    }
                }
            }
        };

        updateThread = new Thread(updateRunnable);
        updateThread.start();
    }

    private void stopUpdateThread() {
        running = false; // Đặt cờ dừng
        updateThread.interrupt(); // Nếu đang ngủ thì đánh thức nó dậy để thoát nhanh hơn
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopUpdateThread();
    }
}
