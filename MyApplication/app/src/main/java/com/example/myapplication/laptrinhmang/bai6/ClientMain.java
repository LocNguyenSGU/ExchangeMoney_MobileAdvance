package com.example.myapplication.laptrinhmang.bai6;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class ClientMain extends AppCompatActivity {
    private EditText etMessage;
    private TextView tvMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bai6_activity_client);

        etMessage = findViewById(R.id.etMessage);
        tvMessages = findViewById(R.id.tvMessages);
        Button sendButton = findViewById(R.id.btnSend);

        sendButton.setOnClickListener(v -> {
            String serverIp = "192.168.1.115"; // Địa chỉ IP của thiết bị Server
            String message = etMessage.getText().toString();

            // Bắt đầu Client thread để gửi tin nhắn
            new ClientThread(serverIp, message, tvMessages).start();
        });
    }
}
