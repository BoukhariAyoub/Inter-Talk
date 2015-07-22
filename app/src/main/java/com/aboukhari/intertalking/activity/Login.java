package com.aboukhari.intertalking.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aboukhari.intertalking.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Login extends Activity implements View.OnClickListener {


    Button btnFbLogin,btnEmailLogin;
    Firebase ref;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        ref = new Firebase(getString(R.string.firebase_url));

        btnEmailLogin = (Button) findViewById(R.id.btn_email_sign_in);
        btnFbLogin = (Button) findViewById(R.id.btn_fb_login);

        if (AccessToken.getCurrentAccessToken() != null) {
            Intent intent = new Intent(Login.this,
                    Friends.class);
            startActivity(intent);
            this.finish();
        }

            ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.d("natija", "profile change");

                onFacebookAccessTokenChange(AccessToken.getCurrentAccessToken());
            }
        };

        profileTracker.startTracking();

        btnFbLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v == btnEmailLogin){

        }
        if (v == btnFbLogin) {
            if (AccessToken.getCurrentAccessToken() != null) {
                Intent mainIntent = new Intent(Login.this,
                        Friends.class);
                mainIntent.putExtra("id", "1");

                startActivity(mainIntent);
          //      LoginManager.getInstance().logOut();
          //      btnFbLogin.setText("Logout");
            } else {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_friends"));
                btnFbLogin.setText("Login");


            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void onFacebookAccessTokenChange(final AccessToken token) {
        if (token != null) {

            ref.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Log.d("natija", "authData " + authData.getExpires());
                    Map<String, String> map2 = new HashMap<>();

                    map2.put("provider", authData.getProvider());
                    if (authData.getProviderData().containsKey("id")) {
                        map2.put("provider_id", authData.getProviderData().get("id").toString());
                    }
                    if (authData.getProviderData().containsKey("displayName")) {
                        map2.put("displayName", authData.getProviderData().get("displayName").toString());
                    }

                    if (authData.getProviderData().containsKey("profileImageURL")) {
                        map2.put("profileImageURL", authData.getProviderData().get("profileImageURL").toString());
                    }

                    if (authData.getProviderData().containsKey("email")) {
                        map2.put("email", authData.getProviderData().get("email").toString());
                    }

                    ref.child("users").child(authData.getUid()).setValue(map2);

                    System.out.println(AccessToken.getCurrentAccessToken().toString());

                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.e("natija", "error " + firebaseError.getMessage());
                }
            });
        } else {
        /* Logged out of Facebook so do a logout from the Firebase app */
            ref.unauth();
        }
    }


}

