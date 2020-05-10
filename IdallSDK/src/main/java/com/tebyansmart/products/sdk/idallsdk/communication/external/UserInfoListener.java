package com.tebyansmart.products.sdk.idallsdk.communication.external;

import com.tebyansmart.products.sdk.idallsdk.model.IdallUserInfoError;
import com.tebyansmart.products.sdk.idallsdk.model.IdallUserResponse;

public interface UserInfoListener {
    void onResponse(IdallUserResponse response);

    void onError(IdallUserInfoError error);
}
