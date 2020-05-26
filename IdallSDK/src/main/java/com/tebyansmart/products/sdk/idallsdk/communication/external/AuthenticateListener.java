package com.tebyansmart.products.sdk.idallsdk.communication.external;

import com.tebyansmart.products.sdk.idallsdk.model.IDallAuthError;
import com.tebyansmart.products.sdk.idallsdk.model.IDallAuthResponse;

public interface AuthenticateListener {
    void onResponse(IDallAuthResponse response);

    void onError(IDallAuthError error);
}
