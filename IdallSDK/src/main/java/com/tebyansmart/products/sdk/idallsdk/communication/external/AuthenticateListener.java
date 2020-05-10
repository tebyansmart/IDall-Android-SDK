package com.tebyansmart.products.sdk.idallsdk.communication.external;

import com.tebyansmart.products.sdk.idallsdk.model.IdallAuthError;
import com.tebyansmart.products.sdk.idallsdk.model.IdallAuthResponse;

public interface AuthenticateListener {
    void onResponse(IdallAuthResponse response);

    void onError(IdallAuthError error);
}
