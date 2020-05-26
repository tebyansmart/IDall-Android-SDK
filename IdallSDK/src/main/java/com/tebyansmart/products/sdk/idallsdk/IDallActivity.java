package com.tebyansmart.products.sdk.idallsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.tebyansmart.products.sdk.idallsdk.communication.internal.TokenListener;
import com.tebyansmart.products.sdk.idallsdk.model.IDallAuthError;
import com.tebyansmart.products.sdk.idallsdk.network.IDallServices;
import com.tebyansmart.products.sdk.idallsdk.utils.IDallConfigs;
import com.tebyansmart.products.sdk.idallsdk.utils.ModelUtils;
import com.tebyansmart.products.sdk.idallsdk.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import static com.tebyansmart.products.sdk.idallsdk.utils.IDallConfigs.TOKEN_ENDPOINT_KEY;

public class IDallActivity extends AppCompatActivity implements TokenListener {

    private SharedPreferences preferences;
    private IDall idall = IDall.getInstance();
    private CustomTabsIntent customTabsIntent;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialCustomTabIntent();
        preferences = getPreferences(Context.MODE_PRIVATE);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(IDallConfigs.APP_ID)) {
                saveStringToPreferences(IDallConfigs.APP_ID, intent.getStringExtra(IDallConfigs.APP_ID));
                try {
                    customTabsIntent.launchUrl(this, UrlUtils.buildAuthorizeUrl(idall.getDiscoveryObject().getString("authorization_endpoint"),
                            preferences.getString(IDallConfigs.APP_ID, null), generateAndSaveState()));
                } catch (JSONException e) {
                    idall.getAuthenticateListener().onError(IDallAuthError.DISCOVERY_PARSE);
                    e.printStackTrace();
                }
                finish();
            } else if (intent.getCategories().contains(Intent.CATEGORY_BROWSABLE) &&
                    intent.getData() != null &&
                    intent.getData().getScheme() != null &&
                    intent.getData().getHost() != null &&
                    intent.getData().getScheme().equals("idall") &&
                    intent.getData().getHost().equals(preferences.getString(IDallConfigs.APP_ID, null))) {

                if (intent.getData().getQuery() != null) {
                    Uri uri = intent.getData();
                    if (uri.getQueryParameter("state").equals(preferences.getString(IDallConfigs.STATE, ""))) {
                        try {
                            new IDallServices.GetToken(this).execute(idall.getDiscoveryObject().getString(TOKEN_ENDPOINT_KEY),
                                    uri.getQueryParameter("code"),
                                    preferences.getString(IDallConfigs.APP_ID, null));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        idall.getAuthenticateListener().onError(IDallAuthError.STATE_MISMATCH);
                    }
                } else {
                    idall.getAuthenticateListener().onError(IDallAuthError.UNKNOWN);
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

    private String generateAndSaveState() {
        saveStringToPreferences(IDallConfigs.STATE, UUID.randomUUID().toString());
        return preferences.getString(IDallConfigs.STATE, null);
    }

    @SuppressLint("ApplySharedPref")
    private void saveStringToPreferences(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onResponse(JSONObject auth) {
        try {
            idall.isAuthorized = true;
            idall.getAuthenticateListener().onResponse(ModelUtils.createIDallAuthResponse(auth));
            preferences.edit().putString("idall_token_data", auth.toString()).commit();
        } catch (JSONException e) {
            idall.getAuthenticateListener().onError(IDallAuthError.TOKEN_PARSE);
            e.printStackTrace();
        }

        finish();
    }

    @Override
    public void onError(Throwable error) {
        idall.getAuthenticateListener().onError(IDallAuthError.TOKEN_FETCH);
    }
}