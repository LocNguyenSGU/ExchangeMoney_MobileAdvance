package com.example.myapplication.laptrinhmang;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.myapplication.R;

import java.io.IOException;

public class Bai7 extends AppCompatActivity {

    EditText etServer;
    Button btnPing;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bai7_activity_main);

        etServer = findViewById(R.id.etServer);
        btnPing = findViewById(R.id.btnPing);
        tvResult = findViewById(R.id.tvResult);

        btnPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String server = etServer.getText().toString().trim();
                if (server.isEmpty()) {
                    Toast.makeText(Bai7.this, "Vui lòng nhập địa chỉ server!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thực hiện ping trong luồng mới
                new Thread(() -> {
                    try {
                        long startTime = System.currentTimeMillis();
                        Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 " + server);
                        int resultCode = process.waitFor();
                        long endTime = System.currentTimeMillis();
                        long delay = endTime - startTime;

                        runOnUiThread(() -> {
                            if (resultCode == 0) {
                                tvResult.setText("Ping thành công!\nThời gian phản hồi: " + delay + " ms");
                            } else {
                                tvResult.setText("Ping thất bại!");
                            }
                        });
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> tvResult.setText("Lỗi khi ping: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }
}
