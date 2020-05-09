package com.tebyansmart.products.sdk.idallsdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class IdallActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private Idall idall;
    private CustomTabsIntent customTabsIntent;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialCustomTabIntent();
        preferences = getPreferences(Context.MODE_PRIVATE);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(IdallConfigs.APP_ID)) {
                saveStringToPreferences(IdallConfigs.APP_ID, intent.getStringExtra(IdallConfigs.APP_ID));
                initialIdallService();
                try {
                    customTabsIntent.launchUrl(this, buildAuthorizeUrl(idall.getDiscoveryObject().getString("authorization_endpoint")));
                } catch (JSONException e) {
                    idall.getResponseListener().onError(IdallError.DISCOVERY_PARSE);
                    e.printStackTrace();
                }
                finish();
            } else if (intent.getCategories().contains(Intent.CATEGORY_BROWSABLE) &&
                    intent.getData() != null &&
                    intent.getData().getScheme() != null &&
                    intent.getData().getHost() != null &&
                    intent.getData().getScheme().equals("idall") &&
                    intent.getData().getHost().equals(preferences.getString(IdallConfigs.APP_ID, null))) {

                initialIdallService();

                if (intent.getData().getQuery() != null) {
                    Uri uri = intent.getData();
                    if (uri.getQueryParameter("state").equals(preferences.getString(IdallConfigs.STATE, ""))) {
                        new GetToken().execute(uri.getQueryParameter("code"));
                    } else {
                        idall.getResponseListener().onError(IdallError.STATE_MISMATCH);
                    }
                } else {
                    idall.getResponseListener().onError(IdallError.UNKNOWN);
                }
            }
        }
    }

    private void initialCustomTabIntent() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        customTabsIntent = builder.build();
        customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    private void initialIdallService() {
        idall = Idall.getInstance(this, preferences.getString(IdallConfigs.APP_ID, null));
    }

    // Build Authorization Url to request Idall Server
    private Uri buildAuthorizeUrl(String authorizeUrl) {
        Uri discoveryUri = Uri.parse(authorizeUrl);
        Uri.Builder uriBuilder = new Uri.Builder().scheme(discoveryUri.getScheme())
                .authority(discoveryUri.getHost())
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("client_id", preferences.getString(IdallConfigs.APP_ID, null))
                .appendQueryParameter("state", generateAndSaveState())
                .appendQueryParameter("redirect_uri", IdallConfigs.SCHEME + "://" + preferences.getString(IdallConfigs.APP_ID, null))
                .appendQueryParameter("scope", "openid profile email");

        for (int i = 0; i < discoveryUri.getPathSegments().size(); i++) {
            uriBuilder.appendPath(discoveryUri.getPathSegments().get(i));
        }
        return uriBuilder.build();
    }

    private URL buildTokenUrl(String tokenUrl, String code) throws MalformedURLException {
        Uri discoveryUri = Uri.parse(tokenUrl);
        Uri.Builder uriBuilder = new Uri.Builder().scheme(discoveryUri.getScheme())
                .authority(discoveryUri.getHost());

        for (int i = 0; i < discoveryUri.getPathSegments().size(); i++) {
            uriBuilder.appendPath(discoveryUri.getPathSegments().get(i));
        }
        return new URL(uriBuilder.build().toString());
    }

    private String generateAndSaveState() {
        saveStringToPreferences(IdallConfigs.STATE, UUID.randomUUID().toString());
        return preferences.getString(IdallConfigs.STATE, null);
    }

    @SuppressLint("ApplySharedPref")
    private void saveStringToPreferences(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetToken extends AsyncTask<String, String, String> {
        private HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = buildTokenUrl(idall.getDiscoveryObject().getString("token_endpoint"), args[0]);

                Map<String, Object> params = new LinkedHashMap<>();
                params.put("grant_type", "authorization_code");
                params.put("code", args[0]);
                params.put("redirect_uri", IdallConfigs.SCHEME + "://" + preferences.getString(IdallConfigs.APP_ID, null));
                params.put("client_id", preferences.getString(IdallConfigs.APP_ID, null));

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

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject object = new JSONObject(result);
                    idall.getResponseListener().onResponse(new IdallResponse(object.getString("id_token"),
                            object.getString("access_token"),
                            object.getString("token_type"),
                            object.getString("scope"),
                            object.getLong("expires_in")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                idall.getResponseListener().onError(IdallError.TOKEN_FETCH);
            }
            finish();
        }
    }
}