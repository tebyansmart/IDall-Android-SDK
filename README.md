# IDall Android SDK

#### IDall is an oauth service that helps you to authenticate users easily with their idall accounts based on their phone number.

## Installation
[ ![Download](https://api.bintray.com/packages/tebyansmart/IDall/IDallSDK/images/download.svg?version=1.0.0.1) ](https://bintray.com/tebyansmart/IDall/IDallSDK/1.0.0.1/link)

#### 1) Import IDall Android SDK by add this line in your build.gradle app module :
```groovy
implementation 'com.tebyansmart.products.sdk:IDallSDK:1.0.0.1'
```
#### 2) Add following values to your project's string.xml file :
```xml
    <string name="idall_app_id">co-bodyguard-android-app</string>
    <string name="idall_login_protocol_scheme">idall</string>
```
#### 3) Add the following lines in your project's Manifest.xml :
```xml
        <activity
            android:name="com.tebyansmart.products.sdk.idallsdk.IDallActivity"
            android:exported="true"
            android:launchMode="singleTop"
            android:noHistory="true"
            android:screenOrientation="portrait">

            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

                <data
                    android:host="@string/idall_app_id"
                    android:scheme="@string/idall_login_protocol_scheme" />
            </intent-filter>
        </activity>
```

#### 4) Sync project to get IDallSDK 
## Usage

#### 1) Create IDall Object

#### Add following lines in your Activity/Fragment :
```java
IDall idall = IDall.getInstance().setApplicationId(getString(R.string.idall_app_id));
```
#### 2) Authenticate User
```java
idall.authenticate(this, new AuthenticateListener() {
            @Override
            public void onResponse(IDallAuthResponse response) {
                Toast.makeText(MainActivity.this, response.accessToken, Toast.LENGTH_SHORT).show();
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
                    case TOKEN_FETCH:
                        break;
                    case UNKNOWN:
                        break;
                }
            }
        });
```

#### 3) Get user info

#### After authenticate user , you can get user info with token provided in authenticate onResponse :
```java
idall.userInfo(response.accessToken, new UserInfoListener() {
                    @Override
                    public void onResponse(IDallUserResponse response) {
                        // Retrieve user information from response object
                    }
                    @Override
                    public void onError(IDallUserInfoError error) {
                        switch (error) {
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
                            case UNKNOWN:
                                break;
                        }
                    }
                });
```
