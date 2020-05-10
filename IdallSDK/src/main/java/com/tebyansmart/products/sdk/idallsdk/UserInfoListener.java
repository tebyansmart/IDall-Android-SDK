package com.tebyansmart.products.sdk.idallsdk;

import com.tebyansmart.products.sdk.idallsdk.model.IdallUserResponse;

public interface UserInfoListener {
    void onResponse(IdallUserResponse response);

    void onError(IdallUserInfoError error);
}
