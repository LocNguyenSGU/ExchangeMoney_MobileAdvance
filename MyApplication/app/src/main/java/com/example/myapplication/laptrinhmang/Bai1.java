package com.example.myapplication.laptrinhmang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Bai1 extends AppCompatActivity {

    private TextView textViewResult;
    private Button btnCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bai1);

        textViewResult = findViewById(R.id.textViewResult);
        btnCheck = findViewById(R.id.btnCheck);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    textViewResult.setText("Có kết nối mạng. Đang kiểm tra truy cập Internet...");
                    new CheckInternetTask().execute();
                } else {
                    textViewResult.setText("Không có kết nối mạng.");
                }
            }
        });
    }

    // Kiểm tra xem có kết nối mạng không (Wi-Fi hoặc dữ liệu di động)
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    // AsyncTask để kiểm tra truy cập Internet thực sự
    private class CheckInternetTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection)
                        (new URL("https://www.google.com").openConnection());
                urlConnection.setRequestProperty("User-Agent", "Test");
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.setConnectTimeout(1500);
                urlConnection.connect();
                return (urlConnection.getResponseCode() == 200);
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                textViewResult.setText("Đã truy cập được Internet (Google).");
            } else {
                textViewResult.setText("Không thể truy cập Internet.");
            }
        }
    }
}

