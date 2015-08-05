package com.aboukhari.intertalking.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.activity.main.MainActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;

import java.util.Arrays;


public class Login extends Activity implements View.OnClickListener {


    Button btnFbLogin, btnEmailLogin;
    Firebase ref;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        ref.getDefaultConfig().setPersistenceEnabled(true);
        final FireBaseManager fireBaseManager = new FireBaseManager(this);
        callbackManager = CallbackManager.Factory.create();
        ref = new Firebase(getString(R.string.firebase_url));

        btnEmailLogin = (Button) findViewById(R.id.btn_email_sign_in);
        btnFbLogin = (Button) findViewById(R.id.btn_fb_login);

        if (ref.getAuth() != null) {
            Intent intent = new Intent(Login.this,
                    MainActivity.class);
            startActivity(intent);
            this.finish();
        }

        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.d("natija", "profile change");

                if (newProfile != null) {
                    String pictureUrl = newProfile.getProfilePictureUri(400, 400).toString();
                    fireBaseManager.onFacebookAccessTokenChange(AccessToken.getCurrentAccessToken(), pictureUrl);
                }


            }
        };

        profileTracker.startTracking();

        btnFbLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == btnEmailLogin) {

        }
        if (v == btnFbLogin) {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();

            } else {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_friends", "user_birthday"));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}

