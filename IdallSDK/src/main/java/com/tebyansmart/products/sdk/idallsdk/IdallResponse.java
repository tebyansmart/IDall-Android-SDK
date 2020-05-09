package com.tebyansmart.products.sdk.idallsdk;

public class IdallResponse {

    public String idToken;
    public String accessToken;
    public String tokenType;
    public String scope;
    public Long expireIn;

    public IdallResponse(String idToken, String accessToken, String tokenType, String scope, Long expireIn) {
        this.idToken = idToken;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.scope = scope;
        this.expireIn = expireIn;
    }
}
