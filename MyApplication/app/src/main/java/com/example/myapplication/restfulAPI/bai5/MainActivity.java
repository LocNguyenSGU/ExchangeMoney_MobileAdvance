package com.example.myapplication.restfulAPI.bai5;

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
    private EditText editCurrency;
    private TextView txtResult;
    private Button btnConvert;
    private static final String API_KEY = "70850be6085375c0622897c1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.api_bai5_activity_main);

        editCurrency = findViewById(R.id.editCurrency);
        txtResult = findViewById(R.id.txtResult);
        btnConvert = findViewById(R.id.btnConvert);

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currency = editCurrency.getText().toString().toUpperCase();
                new ConvertCurrencyTask().execute(currency);
            }
        });
    }

    public class ConvertCurrencyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                connection.disconnect();
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject rates = jsonObject.getJSONObject("conversion_rates");
                double rate = rates.getDouble(editCurrency.getText().toString().toUpperCase());
                txtResult.setText("1 USD = " + rate + " " + editCurrency.getText().toString().toUpperCase());
            } catch (Exception e) {
                txtResult.setText("Unable to convert currency");
            }
        }
    }
}



