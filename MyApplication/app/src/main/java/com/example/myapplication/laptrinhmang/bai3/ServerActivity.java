package com.example.myapplication.laptrinhmang.bai3;
import static android.content.ContentValues.TAG;

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
import java.net.ServerSocket;
import java.net.Socket;

public class ServerActivity extends AppCompatActivity {
    private TextView chatBox;
    private EditText messageInput;
    private Button sendButton;
    private Handler handler = new Handler();
    private PrintWriter out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatBox = findViewById(R.id.chatBox);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        new Thread(new ServerThread()).start();
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
                    handler.post(() -> chatBox.append("\nServer: " + message));
                    handler.post(() -> messageInput.setText(""));
                } else {
                    Log.e(TAG, "PrintWriter is null, cannot send message");
                    // Thử khởi tạo lại kết nối nếu out là null
                    handler.post(() -> {
                        chatBox.append("\nError: Connection lost. Reconnecting...");
                        new Thread(new ServerThread()).start(); // Thử kết nối lại
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to send message: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    class ServerThread implements Runnable {
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(12345);
                Socket client = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);

                while (true) {
                    String message = in.readLine();
                    if (message == null || message.equalsIgnoreCase("exit")) {
                        break;
                    }
                    final String finalMessage = message;
                    handler.post(() -> chatBox.append("\nClient: " + finalMessage));
                }
                client.close();
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}