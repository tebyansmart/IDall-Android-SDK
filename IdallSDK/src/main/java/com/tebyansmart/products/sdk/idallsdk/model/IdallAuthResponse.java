package com.tebyansmart.products.sdk.idallsdk.model;

public class IdallAuthResponse {

    public String idToken = null;
    public String accessToken = null;
    public String tokenType = null;
    public String scope = null;
    public Long expireIn = null;

    public IdallAuthResponse() {
    }

    public IdallAuthResponse(String idToken, String accessToken, String tokenType, String scope, Long expireIn) {
        this.idToken = idToken;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.scope = scope;
        this.expireIn = expireIn;
    }
}
