package com.example.myapplication.laptrinhmang.bai3;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "ClientActivity";
    private TextView chatBox;
    private EditText messageInput;
    private Button sendButton;
    private Handler handler = new Handler();
    private PrintWriter out;
    private Socket socket; // Thêm biến socket để giữ kết nối

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatBox = findViewById(R.id.chatBox);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        new Thread(new ClientThread()).start();
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (message.isEmpty()) {
            Log.w(TAG, "Message is empty, cannot send");
            return;
        }

        new Thread(() -> {
            try {
                if (out != null) {
                    Log.d(TAG, "Sending message: " + message);
                    out.println(message);
                    handler.post(() -> chatBox.append("\nClient: " + message));
                    handler.post(() -> messageInput.setText(""));
                } else {
                    Log.e(TAG, "PrintWriter is null, cannot send message");
                    // Thử khởi tạo lại kết nối nếu out là null
                    handler.post(() -> {
                        chatBox.append("\nError: Connection lost. Reconnecting...");
                        new Thread(new ClientThread()).start(); // Thử kết nối lại
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to send message: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                Log.d(TAG, "Connecting to Server at 192.168.1.100:12345");
                socket = new Socket("192.168.31.23", 12345); // Thay IP bằng IP thực tế của Server
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                    String message = in.readLine();
                    if (message == null || message.equalsIgnoreCase("exit")) {
                        Log.d(TAG, "Connection closed or 'exit' received");
                        break;
                    }
                    final String finalMessage = message;
                    handler.post(() -> chatBox.append("\nServer: " + finalMessage));
                }
                socket.close();
            } catch (Exception e) {
                Log.e(TAG, "Client error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng socket khi activity bị hủy
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                Log.e(TAG, "Error closing socket: " + e.getMessage());
            }
        }
    }
}