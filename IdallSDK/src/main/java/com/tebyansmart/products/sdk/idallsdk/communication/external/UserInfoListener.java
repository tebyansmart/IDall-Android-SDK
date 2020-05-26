package com.tebyansmart.products.sdk.idallsdk.communication.external;

import com.tebyansmart.products.sdk.idallsdk.model.IDallUserInfoError;
import com.tebyansmart.products.sdk.idallsdk.model.IDallUserResponse;

public interface UserInfoListener {
    void onResponse(IDallUserResponse response);

    void onError(IDallUserInfoError error);
}
