package com.example.myapplication.laptrinhmang;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Bai5 extends AppCompatActivity {

    private Button btnCheckSpeed;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bai5_acitivity_main);

        btnCheckSpeed = findViewById(R.id.btnCheckSpeed);
        txtResult = findViewById(R.id.txtResult);

        btnCheckSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternetSpeed();
            }
        });
    }

    private void checkInternetSpeed() {
        txtResult.setText("Đang kiểm tra...");

        new Thread(() -> {
            try {
                URL url = new URL("http://speedtest.tele2.net/1MB.zip");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000); // timeout sau 10 giây
                connection.setReadTimeout(15000);
                connection.setUseCaches(false);

                long startTime = SystemClock.elapsedRealtime();

                InputStream input = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int totalBytes = 0;
                int bytesRead;

                while ((bytesRead = input.read(buffer)) != -1) {
                    totalBytes += bytesRead;
                }

                long endTime = SystemClock.elapsedRealtime();
                long timeTakenMillis = endTime - startTime;

                double timeTakenSeconds = timeTakenMillis / 1000.0;
                double speedKbps = (totalBytes / 1024.0) / timeTakenSeconds;

                String result = String.format("Tốc độ mạng: %.2f KB/s", speedKbps);

                runOnUiThread(() -> txtResult.setText(result));

                input.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace(); // in ra Logcat
                String errorMessage = "Lỗi: " + e.getClass().getSimpleName() + " - " + e.getMessage();
                runOnUiThread(() -> txtResult.setText(errorMessage));
            }
        }).start();
    }

}

