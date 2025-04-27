package com.example.myapplication.laptrinhmang.bai6;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class ServerMain extends AppCompatActivity {
    private TextView tvMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bai6_activity_server);

        tvMessages = findViewById(R.id.tvMessages);

        // Bắt đầu Server thread để lắng nghe và nhận tin nhắn
        new ServerThread(tvMessages).start();
    }
}

