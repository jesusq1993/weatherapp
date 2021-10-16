package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.weatherapp.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    String result = "";

    public void getWeather(View view) {

        DownloadTask task = new DownloadTask();

        try {
            String encodedCityName = URLEncoder.encode(activityMainBinding.editTextLocation.getText().toString(),"UTF-8");

            task.execute("https://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=f8a4dbbd33f337ab3a73c76dedbf1f58").get();

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(activityMainBinding.editTextLocation.getWindowToken(),0);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not get weather :(",Toast.LENGTH_SHORT).show();

        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            try{
                String result = "";
                URL url = new URL(urls[0]);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data!=-1){
                    char current = (char) data;
                    result+=current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e){
                e.printStackTrace();

                //Toast.makeText(getApplicationContext(), "Could not get weather :(",Toast.LENGTH_SHORT).show();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather Content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";

                for(int i = 0;i<arr.length();i++){


                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main"); //Log.i("main",jsonPart.getString("main"));
                    String desc = jsonPart.getString("description"); //Log.i("desc",jsonPart.getString("description"));
                    if(!main.equals("") && !desc.equals("")) {
                        message+=main+": "+desc+"\r\n";
                    }
                }

                if(!message.equals("")){
                    activityMainBinding.textViewWeatherDetails.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(), "Could not get weather :(",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Could not find weather :(", Toast.LENGTH_SHORT).show();
                e.printStackTrace();

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);


    }
}