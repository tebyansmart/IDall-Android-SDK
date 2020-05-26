package com.tebyansmart.products.sdk.idallsdk.model;

public class IDallUserResponse {

    public String sub = null;
    public String name = null;
    public String phone_number = null;
    public boolean phone_number_verified;

    public IDallUserResponse() {
    }

    public IDallUserResponse(String sub, String name, String phone_number, boolean phone_number_verified) {
        this.sub = sub;
        this.name = name;
        this.phone_number = phone_number;
        this.phone_number_verified = phone_number_verified;
    }
}
