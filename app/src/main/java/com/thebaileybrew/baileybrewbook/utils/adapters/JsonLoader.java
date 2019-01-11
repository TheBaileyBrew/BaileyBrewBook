package com.thebaileybrew.baileybrewbook.utils.adapters;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

public class JsonLoader extends AsyncTask<String, Void, String> {
    private static final String TAG = JsonLoader.class.getSimpleName();


    @Override
    protected String doInBackground(String... strings) {
        if (strings.length <1 || strings[0] == null) {
            return null;
        }
        URL url = null;
        try {
            url = new URL(String.valueOf(strings[0]));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }
        String jsonResponse = "";
        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            Log.e(TAG, "makeHttpsRequest: Full URL is: " + String.valueOf(url) );
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(12000);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            //Check for successful connection
            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(TAG, "makeHttpsRequest: Error Code: " + urlConnection.getResponseCode() );
            }
        } catch (IOException i) {
            Log.e(TAG, "makeHttpsRequest: Could not retriece JSON details", i);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonResponse;
    }


    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
