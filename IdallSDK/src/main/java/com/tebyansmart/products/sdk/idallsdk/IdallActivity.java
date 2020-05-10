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
import com.tebyansmart.products.sdk.idallsdk.model.IdallAuthError;
import com.tebyansmart.products.sdk.idallsdk.network.IdallServices;
import com.tebyansmart.products.sdk.idallsdk.utils.IdallConfigs;
import com.tebyansmart.products.sdk.idallsdk.utils.ModelUtils;
import com.tebyansmart.products.sdk.idallsdk.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import static com.tebyansmart.products.sdk.idallsdk.utils.IdallConfigs.TOKEN_ENDPOINT_KEY;

public class IdallActivity extends AppCompatActivity implements TokenListener {

    private SharedPreferences preferences;
    private Idall idall = Idall.getInstance();
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
                try {
                    customTabsIntent.launchUrl(this, UrlUtils.buildAuthorizeUrl(idall.getDiscoveryObject().getString("authorization_endpoint"),
                            preferences.getString(IdallConfigs.APP_ID, null), generateAndSaveState()));
                } catch (JSONException e) {
                    idall.getAuthenticateListener().onError(IdallAuthError.DISCOVERY_PARSE);
                    e.printStackTrace();
                }
                finish();
            } else if (intent.getCategories().contains(Intent.CATEGORY_BROWSABLE) &&
                    intent.getData() != null &&
                    intent.getData().getScheme() != null &&
                    intent.getData().getHost() != null &&
                    intent.getData().getScheme().equals("idall") &&
                    intent.getData().getHost().equals(preferences.getString(IdallConfigs.APP_ID, null))) {

                if (intent.getData().getQuery() != null) {
                    Uri uri = intent.getData();
                    if (uri.getQueryParameter("state").equals(preferences.getString(IdallConfigs.STATE, ""))) {
                        try {
                            new IdallServices.GetToken(this).execute(idall.getDiscoveryObject().getString(TOKEN_ENDPOINT_KEY),
                                    uri.getQueryParameter("code"),
                                    preferences.getString(IdallConfigs.APP_ID, null));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        idall.getAuthenticateListener().onError(IdallAuthError.STATE_MISMATCH);
                    }
                } else {
                    idall.getAuthenticateListener().onError(IdallAuthError.UNKNOWN);
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
        saveStringToPreferences(IdallConfigs.STATE, UUID.randomUUID().toString());
        return preferences.getString(IdallConfigs.STATE, null);
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
            idall.getAuthenticateListener().onResponse(ModelUtils.createIdallAuthResponse(auth));
            preferences.edit().putString("idall_token_data", auth.toString()).commit();
        } catch (JSONException e) {
            idall.getAuthenticateListener().onError(IdallAuthError.TOKEN_PARSE);
            e.printStackTrace();
        }

        finish();
    }

    @Override
    public void onError(Throwable error) {
        idall.getAuthenticateListener().onError(IdallAuthError.TOKEN_FETCH);
    }
}