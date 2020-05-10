package com.tebyansmart.products.sdk.idallsdk.model;

public class IdallUserResponse {

    public String sub = null;
    public String role = "null";
    public String name = null;


    public IdallUserResponse() {
    }

    public IdallUserResponse(String sub, String role, String name) {
        this.sub = sub;
        this.role = role;
        this.name = name;
    }

    public IdallUserResponse(String sub, String name) {
        this.sub = sub;
        this.name = name;
    }
}
