package com.example.myapplication.restfulAPI.bai4;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText edtCity;
    private TextView txtWeather;
    private Button btnGetWeather;

    // Thay thế API_KEY bằng khóa API thực tế của bạn
    private static final String API_KEY = "37c6ba63ded1fc4c85bbb65eb2846d0d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.api_bai4_activity_main);

        edtCity = findViewById(R.id.edtCity);
        txtWeather = findViewById(R.id.txtWeather);
        btnGetWeather = findViewById(R.id.btnGetWeather);

        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtCity.getText().toString();
                new GetWeatherTask().execute(city);
            }
        });
    }

    private class GetWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=" + API_KEY ;
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                return "Lỗi: " + e.getMessage();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                String cityName = jsonObject.getString("name");
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");

                txtWeather.setText("Thành phố: " + cityName + "\nNhiệt độ: " + temperature + "°C");
            } catch (Exception e) {
                txtWeather.setText("Không lấy được dữ liệu!");
            }
        }
    }
}
