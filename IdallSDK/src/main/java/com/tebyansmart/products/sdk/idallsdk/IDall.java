package com.tebyansmart.products.sdk.idallsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.tebyansmart.products.sdk.idallsdk.communication.external.AuthenticateListener;
import com.tebyansmart.products.sdk.idallsdk.communication.external.UserInfoListener;
import com.tebyansmart.products.sdk.idallsdk.communication.internal.DiscoveryListener;
import com.tebyansmart.products.sdk.idallsdk.model.IDallAuthError;
import com.tebyansmart.products.sdk.idallsdk.model.IDallAuthResponse;
import com.tebyansmart.products.sdk.idallsdk.model.IDallUserInfoError;
import com.tebyansmart.products.sdk.idallsdk.network.IDallServices;
import com.tebyansmart.products.sdk.idallsdk.utils.IDallConfigs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * IDall main class to initialize IDall services.
 * Use IDall authenticate method to authenticate user.
 * Use userInfo method to get user information.
 */
public class IDall {

    @SuppressLint("StaticFieldLeak")
    private static IDall instance;
    private AuthenticateListener authenticateListener;
    private String appId = null;
    JSONObject discoveryObject;
    boolean isAuthorized = false;
    private DiscoveryListener discoveryListener = new DiscoveryListener() {
        @Override
        public void onResponse(Context context, JSONObject discovery) {
            discoveryObject = discovery;
            Intent intent = new Intent(context, IDallActivity.class);
            intent.putExtra(IDallConfigs.APP_ID, instance.appId);
            context.startActivity(intent);
        }

        @Override
        public void onError(Throwable error) {
            authenticateListener.onError(IDallAuthError.DISCOVERY_FETCH);
        }
    };

    private IDall() {
    }

    /**
     * Initialize IDall Service
     *
     * @return IDall
     */
    public static IDall getInstance() {
        if (instance == null) {
            instance = new IDall();
        }
        return instance;
    }

    /**
     * Authenticate user with browser flow
     *
     * @param context              Activity/Fragment {@link Context} to start {@link IDallActivity}
     * @param authenticateListener {@link AuthenticateListener} to get response or error details
     * @throws RuntimeException if applicationId or context not passed or null, also applicationId should not be empty
     */
    public void authenticate(@NonNull Context context, @NonNull AuthenticateListener authenticateListener) {
        if (context == null) {
            throw new RuntimeException("Context needed by IDall service");
        } else if (appId == null || TextUtils.isEmpty(appId)) {
            throw new RuntimeException("AppId is needed by IDall service");
        } else if (authenticateListener == null)
            throw new RuntimeException("ResponseListener needed to communicate with IDall service");
        else {
            this.authenticateListener = authenticateListener;
            // Execute Discovery Async task
            new IDallServices.GetDiscoveryInfo(context, discoveryListener).execute();
        }
    }

    JSONObject getDiscoveryObject() {
        return discoveryObject;
    }

    String getAppId() {
        return appId;
    }

    /**
     * Get User Info based on your accessibility
     *
     * @param accessToken      accessToken that fetched with authenticate method response {@link IDallAuthResponse}
     * @param userInfoListener to get response or error details
     * @throws RuntimeException if userInfoListener is null
     */
    public void userInfo(@NonNull String accessToken, @NonNull UserInfoListener userInfoListener) {
        if (accessToken == null) {
            userInfoListener.onError(IDallUserInfoError.NULL_ACCESS_TOKEN);
        } else if (accessToken.length() < 10) {
            userInfoListener.onError(IDallUserInfoError.INVALID_ACCESS_TOKEN);
        } else if (userInfoListener == null) {
            throw new RuntimeException("userInfoListener is needed to communicate with IDall services");
        } else {
            // Execute GetUser Async task
            if (isAuthorized) {
                try {
                    new IDallServices.GetUserInfo(discoveryObject.getString("userinfo_endpoint"), userInfoListener).execute(accessToken);
                } catch (JSONException e) {
                    userInfoListener.onError(IDallUserInfoError.DISCOVERY);
                    e.printStackTrace();
                }
            } else {
                userInfoListener.onError(IDallUserInfoError.IDALL_NOT_AUTHORIZED);
            }
        }
    }

    /**
     * Set your applicationId
     *
     * @param appId your IDall applicationId
     * @return {@link IDall}
     */
    public IDall setApplicationId(String appId) {
        if (appId == null || TextUtils.isEmpty(appId)) {
            throw new RuntimeException("appID needed by IDall service");
        } else {
            instance.appId = appId;
            return instance;
        }
    }

    AuthenticateListener getAuthenticateListener() {
        return authenticateListener;
    }
}
