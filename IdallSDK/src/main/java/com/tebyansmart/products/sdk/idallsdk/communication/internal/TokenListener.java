package com.tebyansmart.products.sdk.idallsdk.communication.internal;

import org.json.JSONObject;

public interface TokenListener {
    void onResponse(JSONObject discovery);

    void onError(Throwable error);
}
