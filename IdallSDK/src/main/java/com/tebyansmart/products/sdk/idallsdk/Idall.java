package com.tebyansmart.products.sdk.idallsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Idall {

    @SuppressLint("StaticFieldLeak")
    private static Idall instance;

    private ResponseListener responseListener;
    private Context context;
    private static String appId = null;
    private static JSONObject discoveryObject;

    private Idall(Context context, String appID) {
        this.context = context;
        appId = appID;
    }

    public static Idall getInstance(@NonNull Context context, @NonNull String appID) {
        if (context == null) {
            throw new RuntimeException("Context needed by Idall service");
        } else if (appID == null) {
            throw new RuntimeException("appID needed by Idall service");
        } else {
            if (instance == null) {
                instance = new Idall(context, appID);
            }
        }
        return instance;
    }

    public void authenticate(@NonNull ResponseListener responseListener) {
        if (responseListener == null)
            throw new RuntimeException("ResponseListener needed to communicate with Idall services");
        else {
            this.responseListener = responseListener;
            // Execute Discovery Async task
            new GetDiscoveryInfo().execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetDiscoveryInfo extends AsyncTask<String, String, String> {
        private HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(IdallConfigs.DISCOVERY_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(5000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                } else {
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    discoveryObject = new JSONObject(result);
                    Intent intent = new Intent(context, IdallActivity.class);
                    intent.putExtra(IdallConfigs.APP_ID, appId);
                    context.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    responseListener.onError(IdallError.DISCOVERY_PARSE);
                }
            } else {
                responseListener.onError(IdallError.DISCOVERY_FETCH);
            }
        }
    }

    JSONObject getDiscoveryObject() {
        return discoveryObject;
    }

    String getAppId() {
        return appId;
    }

    ResponseListener getResponseListener() {
        return responseListener;
    }
}
