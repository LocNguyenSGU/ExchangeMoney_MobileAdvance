package com.example.myapplication.laptrinhmang.bai6;

import android.app.Activity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class ServerThread extends Thread {
    private ServerSocket serverSocket;
    private TextView tvMessages;

    public ServerThread(TextView tvMessages) {
        this.tvMessages = tvMessages;
        try {
            serverSocket = new ServerSocket(12345); // Lắng nghe ở cổng 12345
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Socket clientSocket = serverSocket.accept(); // Chờ Client kết nối
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String messageFromClient;
            while ((messageFromClient = in.readLine()) != null) {
                // Hiển thị tin nhắn nhận được trong TextView
                final String finalMessage = "Client: " + messageFromClient;
                ((Activity) tvMessages.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.append(finalMessage + "\n");
                    }
                });

                // Gửi phản hồi lại cho Client
                out.println("Server: " + messageFromClient);
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



