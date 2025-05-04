package com.example.exchangemoney;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyConverter {

    private final OkHttpClient client;
    private final Context context;
    private final Handler mainHandler;

    public CurrencyConverter(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void convertCurrency(String fromCurrency, String toCurrency, double amount, final CurrencyConversionCallback callback) {
        String url = "https://v6.exchangerate-api.com/v6/cca8e572cd03d97fd28a7413/latest/" + fromCurrency;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("Lỗi khi gọi API: " + e.getMessage());
                callbackOnError(0, "Lỗi khi gọi API: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    showToast("Lỗi từ server: " + response.code());
                    callbackOnError(0, "Lỗi server: " + response.code());
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject rates = jsonObject.getJSONObject("conversion_rates");
                    double rate = rates.getDouble(toCurrency);
                    double result = amount * rate;

                    // Trả kết quả về MainActivity thông qua callback trên Main Thread
                    mainHandler.post(() -> callback.onConversionSuccess(result));

                    // Sau khi callback xong mới lưu vào database trên background thread
                    try (SQLiteHelper dbHelper = new SQLiteHelper(context)) {
                        dbHelper.insertConversion(fromCurrency, toCurrency, amount, result);
                    } catch (Exception e) {
                        showToast("Lỗi database: " + e.getMessage());
                    }

                } catch (JSONException | IOException e) {
                    showToast("Lỗi khi phân tích dữ liệu: " + e.getMessage());
                    callbackOnError(0, "Lỗi dữ liệu: " + e.getMessage());
                } catch (Exception e) {
                    showToast("Lỗi không xác định: " + e.getMessage());
                    callbackOnError(0, "Lỗi không xác định: " + e.getMessage());
                }
            }
        });
    }

    private void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

    private void callbackOnError(double result, String error) {
        String finalError = (error == null) ? "Lỗi không xác định" : error;
        mainHandler.post(() -> {
            // Gọi callback lỗi
            // Ví dụ: callback.onConversionSuccess(result, finalError);
        });
    }


    public interface CurrencyConversionCallback {
        void onConversionSuccess(double result);
    }
}
