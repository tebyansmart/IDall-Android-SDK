package com.tebyansmart.products.sdk.idallsdk.model;

public class IdallAuthResponse {

    public String idToken;
    public String accessToken;
    public String tokenType;
    public String scope;
    public Long expireIn;

    public IdallAuthResponse(String idToken, String accessToken, String tokenType, String scope, Long expireIn) {
        this.idToken = idToken;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.scope = scope;
        this.expireIn = expireIn;
    }
}
