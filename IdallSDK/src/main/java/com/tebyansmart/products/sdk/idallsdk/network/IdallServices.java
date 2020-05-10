package com.tebyansmart.products.sdk.idallsdk.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.tebyansmart.products.sdk.idallsdk.communication.external.UserInfoListener;
import com.tebyansmart.products.sdk.idallsdk.communication.internal.DiscoveryListener;
import com.tebyansmart.products.sdk.idallsdk.communication.internal.TokenListener;
import com.tebyansmart.products.sdk.idallsdk.model.IdallAuthError;
import com.tebyansmart.products.sdk.idallsdk.model.IdallUserInfoError;
import com.tebyansmart.products.sdk.idallsdk.utils.IdallConfigs;
import com.tebyansmart.products.sdk.idallsdk.utils.ModelUtils;
import com.tebyansmart.products.sdk.idallsdk.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class IdallServices {
    public static class GetUserInfo extends AsyncTask<String, String, String> {
        private HttpURLConnection urlConnection;
        private UserInfoListener userInfoListener;
        private String userInfoEndpoint;

        public GetUserInfo(String userInfoEndpoint, UserInfoListener userInfoListener) {
            this.userInfoListener = userInfoListener;
            this.userInfoEndpoint = userInfoEndpoint;
        }

        @Override
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(userInfoEndpoint);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.addRequestProperty("Authorization", "Bearer " + args[0]);
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
                    userInfoListener.onResponse(ModelUtils.createIdallUserResponse(new JSONObject(result)));
                } catch (JSONException e) {
                    e.printStackTrace();
                    userInfoListener.onError(IdallUserInfoError.USER_PARSE);
                }
            } else {
                userInfoListener.onError(IdallUserInfoError.USER_FETCH);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public static class GetDiscoveryInfo extends AsyncTask<String, String, String> {
        private HttpURLConnection urlConnection;
        private Context context;
        private DiscoveryListener discoveryListener;

        public GetDiscoveryInfo(Context context, DiscoveryListener discoveryListener) {
            this.context = context;
            this.discoveryListener = discoveryListener;
        }

        @Override
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(IdallConfigs.DISCOVERY_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
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
                    discoveryListener.onResponse(context, new JSONObject(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                    discoveryListener.onError(new Throwable("Parse-Exception"));
                }
            } else {
                discoveryListener.onError(new Throwable("Null-Result"));
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public static class GetToken extends AsyncTask<String, String, String> {
        private HttpURLConnection urlConnection;
        private TokenListener tokenListener;

        public GetToken(TokenListener tokenListener) {
            this.tokenListener = tokenListener;
        }

        @Override
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = UrlUtils.buildTokenUrl(args[0]);

                Map<String, Object> params = new LinkedHashMap<>();
                params.put("grant_type", "authorization_code");
                params.put("code", args[1]);
                params.put("redirect_uri", IdallConfigs.SCHEME + "://" + args[2]);
                params.put("client_id", args[2]);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.getOutputStream().write(postDataBytes);
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

        @SuppressLint("ApplySharedPref")
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    tokenListener.onResponse(new JSONObject(result));
                } catch (JSONException e) {
                    tokenListener.onError(new Throwable(IdallAuthError.TOKEN_PARSE.name()));
                    e.printStackTrace();
                }
            } else {
                tokenListener.onError(new Throwable(IdallAuthError.TOKEN_FETCH.name()));
            }
        }
    }
}
