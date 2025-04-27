package com.example.myapplication.restfulAPI.bai2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.api_bai2_acitivty_main);

        textView = findViewById(R.id.textView);

        // Gọi API khi khởi tạo
        new GetUsersTask().execute("https://jsonplaceholder.typicode.com/users");
    }

    private class GetUsersTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection connection = null;

            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
                return "Lỗi: " + e.getMessage();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            textView.setText(s);
        }
    }
}