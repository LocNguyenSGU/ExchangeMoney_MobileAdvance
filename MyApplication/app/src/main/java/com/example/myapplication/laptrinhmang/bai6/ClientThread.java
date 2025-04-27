package com.example.myapplication.laptrinhmang.bai6;

import android.app.Activity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class ClientThread extends Thread {
    private String serverIp;
    private String message;
    private TextView tvMessages;

    public ClientThread(String serverIp, String message, TextView tvMessages) {
        this.serverIp = serverIp;
        this.message = message;
        this.tvMessages = tvMessages;
    }

    @Override
    public void run() {
        try {
            // Kết nối đến Server qua IP và cổng
            Socket socket = new Socket(serverIp, 12345); // Địa chỉ IP và cổng của Server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Gửi tin nhắn đến Server
            out.println(message);

            // Nhận phản hồi từ Server
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                final String finalMessage = "Server: " + serverMessage;
                // Cập nhật TextView từ luồng UI
                ((Activity) tvMessages.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.append(finalMessage + "\n");
                    }
                });
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Nếu có lỗi, hiển thị lỗi trong TextView
            ((Activity) tvMessages.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessages.append("Error: " + e.getMessage() + "\n");
                }
            });
        }
    }
}



