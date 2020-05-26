package com.tebyansmart.products.sdk.idallandroidsdk;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tebyansmart.products.sdk.idallsdk.IDall;
import com.tebyansmart.products.sdk.idallsdk.communication.external.AuthenticateListener;
import com.tebyansmart.products.sdk.idallsdk.communication.external.UserInfoListener;
import com.tebyansmart.products.sdk.idallsdk.model.IDallAuthError;
import com.tebyansmart.products.sdk.idallsdk.model.IDallAuthResponse;
import com.tebyansmart.products.sdk.idallsdk.model.IDallUserInfoError;
import com.tebyansmart.products.sdk.idallsdk.model.IDallUserResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final IDall idall = IDall.getInstance()
                .setApplicationId(getString(R.string.idall_app_id));

        idall.authenticate(this, new AuthenticateListener() {
            @Override
            public void onResponse(IDallAuthResponse response) {
                Toast.makeText(MainActivity.this, response.accessToken, Toast.LENGTH_SHORT).show();

                idall.userInfo(response.accessToken, new UserInfoListener() {
                    @Override
                    public void onResponse(IDallUserResponse response) {
                        Toast.makeText(MainActivity.this, response.name + "\n" + response.phone_number + "\n" + response.sub, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(IDallUserInfoError error) {
                        switch (error) {
                            case UNKNOWN:

                                break;
                            case NULL_ACCESS_TOKEN:

                                break;
                            case INVALID_ACCESS_TOKEN:

                                break;
                            case USER_FETCH:

                                break;
                            case USER_PARSE:

                                break;
                            case IDALL_NOT_AUTHORIZED:

                                break;
                        }
                    }
                });
            }

            @Override
            public void onError(IDallAuthError error) {
                switch (error) {
                    case STATE_MISMATCH:

                        break;
                    case DISCOVERY_FETCH:

                        break;
                    case DISCOVERY_PARSE:

                        break;
                    case UNKNOWN:

                        break;
                    case TOKEN_FETCH:

                        break;
                }
            }
        });
    }
}