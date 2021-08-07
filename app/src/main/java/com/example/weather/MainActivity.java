package com.example.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView editText, resultWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.city);
        resultWeather = findViewById(R.id.resultWeather);
    }

    public void getWeather(View view) {

        try {
            DownloadTask task = new DownloadTask();

            String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=" + BuildConfig.MY_API_KEY);

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Could not get weather :(", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), "Could not get weather :(", Toast.LENGTH_SHORT).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                JSONArray array = new JSONArray(weatherInfo);

                String message = "";

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonPart = array.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")) {
                        message += main + ": " + description + "\r\n";
                    } else {
//                        Toast.makeText(getApplicationContext(), "Could not get weather :(", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!message.equals("")) {
                    resultWeather.setText(message);
                } else {
//                    Toast.makeText(getApplicationContext(), "Could not get weather :(", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), "Could not get weather :(", Toast.LENGTH_SHORT).show();
            }

        }
    }
}