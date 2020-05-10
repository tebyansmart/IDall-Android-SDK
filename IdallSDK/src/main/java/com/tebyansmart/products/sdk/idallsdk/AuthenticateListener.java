package com.tebyansmart.products.sdk.idallsdk;

import com.tebyansmart.products.sdk.idallsdk.model.IdallAuthResponse;

public interface AuthenticateListener {
    void onResponse(IdallAuthResponse response);

    void onError(IdallAuthError error);
}
