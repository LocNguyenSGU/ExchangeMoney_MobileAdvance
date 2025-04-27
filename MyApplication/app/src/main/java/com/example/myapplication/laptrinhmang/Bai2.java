package com.example.myapplication.laptrinhmang;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Bai2 extends AppCompatActivity {

    private TextView textViewCount;
    private ProgressBar progressBar;
    private Button buttonStart, buttonStop;
    private Thread countThread;
    private boolean isCounting = false;

    private Handler handler = new Handler(); // Handler để update UI từ Thread

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bai2);

        textViewCount = findViewById(R.id.textViewCount);
        progressBar = findViewById(R.id.progressBar);
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCounting) return; // tránh tạo thread trùng

                isCounting = true;
                progressBar.setVisibility(View.VISIBLE);

                countThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 1; i <= 10; i++) {
                            if (!isCounting) break;

                            final int current = i;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    textViewCount.setText(String.valueOf(current));
                                }
                            });

                            try {
                                Thread.sleep(1000); // Delay 1 giây
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                if (isCounting)
                                    textViewCount.setText("Hoàn thành!");
                                isCounting = false;
                            }
                        });
                    }
                });

                countThread.start();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCounting = false;
                progressBar.setVisibility(View.GONE);
                textViewCount.setText("Đã dừng!");
            }
        });
    }
}
