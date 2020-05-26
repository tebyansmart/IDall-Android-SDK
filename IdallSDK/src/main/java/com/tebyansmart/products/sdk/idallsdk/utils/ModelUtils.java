package com.tebyansmart.products.sdk.idallsdk.utils;

import com.tebyansmart.products.sdk.idallsdk.model.IDallAuthResponse;
import com.tebyansmart.products.sdk.idallsdk.model.IDallUserResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelUtils {
    public static IDallAuthResponse createIDallAuthResponse(JSONObject authObject) throws JSONException {
        IDallAuthResponse authResponse = new IDallAuthResponse();
        if (authObject.has("id_token")) {
            authResponse.idToken = authObject.getString("id_token");
        }
        if (authObject.has("access_token")) {
            authResponse.accessToken = authObject.getString("access_token");
        }

        if (authObject.has("token_type")) {
            authResponse.tokenType = authObject.getString("token_type");
        }

        if (authObject.has("scope")) {
            authResponse.scope = authObject.getString("scope");
        }

        if (authObject.has("expires_in")) {
            authResponse.expireIn = authObject.getLong("expires_in");
        }

        return authResponse;
    }

    public static IDallUserResponse createIDallUserResponse(JSONObject userObject) throws JSONException {
        IDallUserResponse user = new IDallUserResponse();
        if (userObject.has("sub"))
            user.sub = userObject.getString("sub");
        if (userObject.has("name"))
            user.name = userObject.getString("name");
        if (userObject.has("phone_number"))
            user.phone_number = userObject.getString("phone_number");
        if (userObject.has("phone_number_verified"))
            user.phone_number_verified = userObject.getBoolean("phone_number_verified");

        return user;
    }
}
