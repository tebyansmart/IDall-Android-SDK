package com.tebyansmart.products.sdk.idallsdk.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {

    // Build Authorization Url to request IDall Server
    public static Uri buildAuthorizeUrl(String authorizeUrl, String appId,String state) {
        Uri discoveryUri = Uri.parse(authorizeUrl);
        Uri.Builder uriBuilder = new Uri.Builder().scheme(discoveryUri.getScheme())
                .authority(discoveryUri.getHost())
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("client_id", appId)
                .appendQueryParameter("state", state)
                .appendQueryParameter("redirect_uri", IDallConfigs.SCHEME + "://" + appId)
                .appendQueryParameter("scope", "openid profile phone");

        for (int i = 0; i < discoveryUri.getPathSegments().size(); i++) {
            uriBuilder.appendPath(discoveryUri.getPathSegments().get(i));
        }
        return uriBuilder.build();
    }

    public static URL buildTokenUrl(String tokenUrl) throws MalformedURLException {
        Uri discoveryUri = Uri.parse(tokenUrl);
        Uri.Builder uriBuilder = new Uri.Builder().scheme(discoveryUri.getScheme())
                .authority(discoveryUri.getHost());

        for (int i = 0; i < discoveryUri.getPathSegments().size(); i++) {
            uriBuilder.appendPath(discoveryUri.getPathSegments().get(i));
        }
        return new URL(uriBuilder.build().toString());
    }
}
