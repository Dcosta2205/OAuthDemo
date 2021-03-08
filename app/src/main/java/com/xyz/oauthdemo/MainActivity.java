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
}