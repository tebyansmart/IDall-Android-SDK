package com.tebyansmart.products.sdk.idallandroidsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.tebyansmart.products.sdk.idallsdk.IdallError;
import com.tebyansmart.products.sdk.idallsdk.IdallResponse;
import com.tebyansmart.products.sdk.idallsdk.ResponseListener;
import com.tebyansmart.products.sdk.idallsdk.Idall;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Idall idall = Idall.getInstance(this, getString(R.string.idall_app_id));

        idall.authenticate(new ResponseListener() {
            @Override
            public void onResponse(IdallResponse response) {
                Toast.makeText(MainActivity.this, response.accessToken, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(IdallError error) {
                switch (error) {
                    case STATE_MISMATCH:

                        break;
                    case DISCOVERY_FETCH:

                        break;
                    case DISCOVERY_PARSE:

                        break;
                    case UNKNOWN:

                        break;
                }
            }
        });
    }
}