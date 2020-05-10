package com.tebyansmart.products.sdk.idallsdk.utils;

import com.tebyansmart.products.sdk.idallsdk.model.IdallAuthResponse;
import com.tebyansmart.products.sdk.idallsdk.model.IdallUserResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelUtils {
    public static IdallAuthResponse createIdallAuthResponse(JSONObject authObject) throws JSONException {
        IdallAuthResponse authResponse = new IdallAuthResponse();
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

    public static IdallUserResponse createIdallUserResponse(JSONObject userObject) throws JSONException {
        IdallUserResponse user = new IdallUserResponse();
        if (userObject.has("sub"))
            user.sub = userObject.getString("sub");
        if (userObject.has("name"))
            user.name = userObject.getString("name");
        if (userObject.has("role"))
            user.role = userObject.getString("role");

        return user;
    }
}
