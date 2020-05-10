package com.tebyansmart.products.sdk.idallsdk.communication.internal;

import android.content.Context;

import org.json.JSONObject;

public interface DiscoveryListener {
    void onResponse(Context context, JSONObject discovery);

    void onError(Throwable error);
}
