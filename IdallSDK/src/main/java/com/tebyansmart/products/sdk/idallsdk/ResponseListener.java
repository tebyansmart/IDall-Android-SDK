package com.tebyansmart.products.sdk.idallsdk;

public interface ResponseListener {
    void onResponse(IdallResponse response);

    void onError(IdallError error);
}
