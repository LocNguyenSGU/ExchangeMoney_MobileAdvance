package com.example.myapplication.laptrinhmang.bai6;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {
    private TextView tvMessages;
    private EditText etMessage;
    private Button btnSend;
    private boolean isServer = false; // Biến kiểm tra xem có phải đang là Server hay không

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bai6_activity_main);

        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Kiểm tra xem có phải đang chạy Server hay Client
        if (isServer) {
            // Khởi tạo và chạy ServerThread
            new ServerThread(tvMessages).start();
        }

        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString();
            if (!message.isEmpty()) {
                // Nếu là Client, gửi tin nhắn qua TCP
                if (!isServer) {
                    new ClientThread("192.168.1.115", message, tvMessages).start(); // Địa chỉ IP của Server
                }
                etMessage.setText("");  // Xóa nội dung trong EditText sau khi gửi
            }
        });
    }
}

