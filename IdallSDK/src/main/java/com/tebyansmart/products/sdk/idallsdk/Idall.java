package com.tebyansmart.products.sdk.idallsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.tebyansmart.products.sdk.idallsdk.communication.external.AuthenticateListener;
import com.tebyansmart.products.sdk.idallsdk.communication.external.UserInfoListener;
import com.tebyansmart.products.sdk.idallsdk.communication.internal.DiscoveryListener;
import com.tebyansmart.products.sdk.idallsdk.model.IdallAuthError;
import com.tebyansmart.products.sdk.idallsdk.model.IdallUserInfoError;
import com.tebyansmart.products.sdk.idallsdk.network.IdallServices;
import com.tebyansmart.products.sdk.idallsdk.utils.IdallConfigs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Idall main class to initialize Idall services.
 * Use Idall authenticate method to authenticate user.
 * Use userInfo method to get user information.
 */
public class Idall {

    @SuppressLint("StaticFieldLeak")
    private static Idall instance;
    private AuthenticateListener authenticateListener;
    private String appId = null;
    JSONObject discoveryObject;
    boolean isAuthorized = false;

    private Idall() {
    }

    /**
     * Initialize Idall Service
     *
     * @return Idall
     */
    public static Idall getInstance() {
        if (instance == null) {
            instance = new Idall();
        }
        return instance;
    }

    /**
     * Authenticate user with browser flow
     *
     * @param context              Activity/Fragment {@link Context} to start {@link IdallActivity}
     * @param authenticateListener {@link AuthenticateListener} to get response or error details
     * @throws RuntimeException if applicationId or context not passed or null, also applicationId should not be empty
     */
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
            new IdallServices.GetDiscoveryInfo(context, discoveryListener).execute();
        }
    }

    /**
     * Get User Info based on your accessibility
     *
     * @param accessToken      accessToken that fetched with authenticate method response {@link com.tebyansmart.products.sdk.idallsdk.model.IdallAuthResponse}
     * @param userInfoListener to get response or error details
     * @throws RuntimeException if userInfoListener is null
     */
    public void userInfo(@NonNull String accessToken, @NonNull UserInfoListener userInfoListener) {
        if (accessToken == null) {
            userInfoListener.onError(IdallUserInfoError.NULL_ACCESS_TOKEN);
        } else if (accessToken.length() < 10) {
            userInfoListener.onError(IdallUserInfoError.INVALID_ACCESS_TOKEN);
        } else if (userInfoListener == null) {
            throw new RuntimeException("userInfoListener is needed to communicate with Idall services");
        } else {
            // Execute GetUser Async task
            if (isAuthorized) {
                try {
                    new IdallServices.GetUserInfo(discoveryObject.getString("userinfo_endpoint"), userInfoListener).execute(accessToken);
                } catch (JSONException e) {
                    userInfoListener.onError(IdallUserInfoError.DISCOVERY);
                    e.printStackTrace();
                }
            } else {
                userInfoListener.onError(IdallUserInfoError.IDALL_NOT_AUTHORIZED);
            }
        }
    }

    JSONObject getDiscoveryObject() {
        return discoveryObject;
    }

    String getAppId() {
        return appId;
    }

    /**
     * Set your applicationId
     *
     * @param appId your Idall applicationId
     * @return {@link Idall}
     */
    public Idall setApplicationId(String appId) {
        if (appId == null || TextUtils.isEmpty(appId)) {
            throw new RuntimeException("appID needed by Idall service");
        } else {
            instance.appId = appId;
            return instance;
        }
    }

    private DiscoveryListener discoveryListener = new DiscoveryListener() {
        @Override
        public void onResponse(Context context, JSONObject discovery) {
            discoveryObject = discovery;
            Intent intent = new Intent(context, IdallActivity.class);
            intent.putExtra(IdallConfigs.APP_ID, instance.appId);
            context.startActivity(intent);
        }

        @Override
        public void onError(Throwable error) {
            authenticateListener.onError(IdallAuthError.DISCOVERY_FETCH);
        }
    };

    AuthenticateListener getAuthenticateListener() {
        return authenticateListener;
    }
}
