# OAuthDemo

**OAuth Basics**

OAuth is a token based authorization method which uses an access token for interaction between user and API. OAuth requires several steps and requests against the API to get your access token.

1. Register an app for the API you want to develop. Use the developer sites of the public API you're going to develop for.
2. Save client id and client secret in your app.
3. Request access to user data from your app.
4. Use the authorization code to get the access token.
5. Use the access token to interact with the API.

**Register Your App**

efore starting with the implementation you have to register your app for the service/API you want to develop. Once the sign up for your application (which you're going to build) is finished, you'll receive a client id and a client secret. Both values are required to authenticate your app against the service/API.

**Integrate OAuth in Your App**

```
package com.xyz.oauthdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    /*
     Get the client secret and client id by registering your app https://www.mixcloud.com/developers/create/
     */
    private static final String client_id = "XDP4K.........";
    private static final String client_secret = "q65bCTf8YSLN.........";
    private static final String redirectUri = "mixcloud://callback";
    private String access_token = "";
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewsAndListeners();
    }

    private void initViewsAndListeners() {
        mBtnLogin = findViewById(R.id.btnLogin);
        /*
        When the button is clicked, we make a request to the server and the browser opens where authorization takes place.
         */
        mBtnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.mixcloud.com/oauth/authorize?client_id=" + client_id + "&redirect_uri=" + redirectUri));
            startActivity(intent);
        });
    }
}
```

**Define Activity and Intent Filter in AndroidManifest.Xml**

```
      <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data
                    android:host="callback"
                    android:scheme="mixcloud" />
            </intent-filter>
        </activity>
```

**Catch the Authorization Code**

Now we want to get the access token for further API interaction. This token is another two API requests away. First, we need to parse and use the returned authorization code which is part of the response when pressing the allow button within the intent webview

```
@Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();

        /*
        Once the user is authorized the server will send the user a Auth code.
         */
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            // use the parameter your API exposes for the code (mostly it's "code")
            String code = uri.getQueryParameter("code");
            if (code != null) {
                // get access token

                getAccessToken(code);
            } else if (uri.getQueryParameter("error") != null) {
                // show an error message here
            }
        }
    }
 ```
 
 Your app returns into the onResume method of Android's lifecycle. Here you can see we're using the getIntent().getData() methods to retrieve the intent's response.

Now, we don't want to run into any NullPointerException and check the values. Afterwards, we extract the authorization code from query parameters. Imagine the response url when clicking allow like

```
your://redirecturi?code=1234  
```

and For **Deny** access like

```
your://redirecturi?error=message  
```

**Get Your Access Token**

In the following, we just extend the previous presented onResume method to do another API request. But first, we have to extend the ApiService interface and define a method to request the access token. We'll just extend the ApiService from the basic authentication post with another method called getAccessToken.

```
public interface ApiService {

    @FormUrlEncoded
    @POST("/oauth/access_token")
    Call<AccessToken> getAccessToken(
            @Field("grant_type") String type,
            @Query("client_id") String client_id,
            @Query("redirect_uri") String redirect_url,
            @Query("client_secret") String client_secret,
            @Query("code") String code
    );

}
```
Now the complete code for **onResume** to get the token.

```
    /**
     * Once the user gets the Auth code from server make an API call with this code and the server will
     * give an Access token which can be used to get the user details
     * @param code Authorization code
     */
    private void getAccessToken(String code) {
        ApiService apiService = ApiNetwork.getRetrofitInstance().create(ApiService.class);
        Call<AccessToken> accessTokenCall = apiService.getAccessToken("authorization_code", client_id, redirectUri, client_secret, code);
        accessTokenCall.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                Toast.makeText(MainActivity.this, "yay!", Toast.LENGTH_SHORT).show();
                if (response.body() != null) {
                    /*
                    This access token can be used to make API calls
                     */
                    access_token = response.body().getAccessToken();
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Toast.makeText(MainActivity.this, "No!", Toast.LENGTH_SHORT).show();
            }
        });
    }
```




        

