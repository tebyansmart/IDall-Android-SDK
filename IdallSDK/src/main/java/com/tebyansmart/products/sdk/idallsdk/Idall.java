package com.tebyansmart.products.sdk.idallsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.tebyansmart.products.sdk.idallsdk.model.IdallUserResponse;

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

    private AuthenticateListener authenticateListener;
    private String appId = null;
    private static JSONObject discoveryObject;
    boolean isAuthorized = false;

    private Idall() {
    }

    public static Idall getInstance() {
        if (instance == null) {
            instance = new Idall();
        }
        return instance;
    }

    public void authenticate(@NonNull Context context, @NonNull AuthenticateListener authenticateListener) {
        if (context == null) {
            throw new RuntimeException("Context needed by Idall service");
        } else if (appId == null || TextUtils.isEmpty(appId)) {
            throw new RuntimeException("AppId is needed by Idall service");
        } else if (authenticateListener == null)
            throw new RuntimeException("ResponseListener needed to communicate with Idall service");
        else {
            this.authenticateListener = authenticateListener;
            // Execute Discovery Async task
            new GetDiscoveryInfo(context).execute();
        }
    }

    public void userInfo(@NonNull String accessToken, @NonNull UserInfoListener userInfoListener) {
        if (accessToken == null) {
            userInfoListener.onError(IdallUserInfoError.NULL_ACCESS_TOKEN);
        } else if (accessToken.length() < 10) {
            userInfoListener.onError(IdallUserInfoError.INVALID_ACCESS_TOKEN);
        } else if (userInfoListener == null) {
            throw new RuntimeException("userInfoListener is needed to communicate with Idall services");
        } else {
            // Execute GetUser Async task
            if (isAuthorized)
                new GetUserInfo(userInfoListener).execute(accessToken);
            else
                userInfoListener.onError(IdallUserInfoError.IDALL_NOT_AUTHORIZED);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetDiscoveryInfo extends AsyncTask<String, String, String> {
        private HttpURLConnection urlConnection;
        private Context context;

        public GetDiscoveryInfo(Context context) {
            this.context = context;
        }

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
                    authenticateListener.onError(IdallAuthError.DISCOVERY_PARSE);
                }
            } else {
                authenticateListener.onError(IdallAuthError.DISCOVERY_FETCH);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetUserInfo extends AsyncTask<String, String, String> {
        private HttpURLConnection urlConnection;
        private UserInfoListener userInfoListener;

        public GetUserInfo(UserInfoListener userInfoListener) {
            this.userInfoListener = userInfoListener;
        }

        @Override
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(discoveryObject.getString("userinfo_endpoint"));
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
                    JSONObject userObject = new JSONObject(result);
                    IdallUserResponse user = new IdallUserResponse();
                    if (userObject.has("sub"))
                        user.sub = userObject.getString("sub");
                    if (userObject.has("name"))
                        user.sub = userObject.getString("name");
                    if (userObject.has("role"))
                        user.sub = userObject.getString("role");
                    userInfoListener.onResponse(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                    userInfoListener.onError(IdallUserInfoError.USER_PARSE);
                }
            } else {
                userInfoListener.onError(IdallUserInfoError.USER_FETCH);
            }
        }
    }

    JSONObject getDiscoveryObject() {
        return discoveryObject;
    }

    String getAppId() {
        return appId;
    }

    public Idall setApplicationId(String appId) {
        if (appId == null || TextUtils.isEmpty(appId)) {
            throw new RuntimeException("appID needed by Idall service");
        } else {
            instance.appId = appId;
            return instance;
        }
    }

    AuthenticateListener getAuthenticateListener() {
        return authenticateListener;
    }
}
