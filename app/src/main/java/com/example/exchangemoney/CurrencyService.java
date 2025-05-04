package com.example.exchangemoney;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyService {

    private final OkHttpClient client;
    private final Context context;
    private final Handler mainHandler;
    private final String apiKey = "cca8e572cd03d97fd28a7413"; // API Key của bạn
    private final String url = "https://v6.exchangerate-api.com/v6/cca8e572cd03d97fd28a7413/latest/USD";

    public CurrencyService(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // Phương thức để tự động fetch dữ liệu và setup cho cả 2 spinner
    public void setupCurrencySpinners(AutoCompleteTextView autoCompleteFrom, AutoCompleteTextView autoCompleteTo) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("Lỗi khi lấy danh sách tiền tệ: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    showToast("Lỗi từ server: " + response.code());
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject rates = jsonObject.getJSONObject("conversion_rates");

                    List<CurrencyModel> currencyModelList = new ArrayList<>();
                    Iterator<String> keys = rates.keys();
                    while (keys.hasNext()) {
                        String code = keys.next();
                        String displayName;
                        try {
                            Currency currency = Currency.getInstance(code);
                            displayName = currency.getDisplayName(Locale.US);
                        } catch (IllegalArgumentException e) {
                            displayName = code;
                        }
                        currencyModelList.add(new CurrencyModel(code, displayName));
                    }

                    // Mở danh sách tự động khi không có nội dung nhập
                    autoCompleteFrom.setThreshold(0);  // Đặt ngưỡng cho phép tìm kiếm (0 nghĩa là tìm kiếm ngay lập tức)
                    autoCompleteFrom.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            if (charSequence.length() == 0) {  // Khi không có văn bản nào
                                autoCompleteFrom.showDropDown(); // Mở danh sách tự động
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {}
                    });

                    autoCompleteTo.setThreshold(0);  // Đặt ngưỡng cho phép tìm kiếm (0 nghĩa là tìm kiếm ngay lập tức)
                    autoCompleteTo.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            if (charSequence.length() == 0) {  // Khi không có văn bản nào
                                autoCompleteTo.showDropDown(); // Mở danh sách tự động
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {}
                    });

                    // Sắp xếp theo mã tiền tệ
                    Collections.sort(currencyModelList, (c1, c2) -> c1.getCode().compareTo(c2.getCode()));

                    // Update giao diện trên main thread
                    mainHandler.post(() -> {
                        CustomAdapter adapter = new CustomAdapter(context, currencyModelList);
                        autoCompleteFrom.setAdapter(adapter);
                        autoCompleteTo.setAdapter(adapter);
                    });

                } catch (Exception e) {
                    showToast("Lỗi khi phân tích danh sách tiền tệ");
                }
            }
        });
    }

    private void showToast(final String message) {
        mainHandler.post(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }
}
