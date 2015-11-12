package com.aboukhari.intertalking.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.activity.main.MainActivity;
import com.aboukhari.intertalking.activity.registration.SignUpEmail;
import com.aboukhari.intertalking.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Arrays;


public class Login extends Activity implements View.OnClickListener, View.OnTouchListener {


    Button btnFbLogin, btnEmailLogin, btnSignUp;
    EditText mEmailEditText, mPasswordEditText;
    Firebase ref;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
        }catch (Exception ex){

        }
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        final FireBaseManager fireBaseManager = new FireBaseManager(this);
        callbackManager = CallbackManager.Factory.create();
        ref = new Firebase(getString(R.string.firebase_url));

        Utils.getCurrentHashForFacebook(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        btnEmailLogin = (Button) findViewById(R.id.btn_email_sign_in);
        btnFbLogin = (Button) findViewById(R.id.btn_fb_login);
        btnSignUp = (Button) findViewById(R.id.btn_email_sign_up);

        mPasswordEditText = (EditText) findViewById(R.id.et_password);
        mEmailEditText = (EditText) findViewById(R.id.et_email);

        if (ref.getAuth() != null) {
            FireBaseManager.getInstance(this).addCurrentUserChangeListener();
            User user = Utils.getUserFromPreferences(this);
            if (user != null && !user.getFirstLogin()){
/*                if(user.getUid().contains("facebook")){
                Intent intent = new Intent(this,
                        RegistrationActivity.class);

                    intent.putExtra("facebook",true);
                }
                startActivity(intent);
            }*/

                Intent intent = new Intent(this,
                        MainActivity.class);
                startActivity(intent);
                this.finish();
            }






        }

        String email = getIntent().getStringExtra("email");
        if (email != null) {
            mEmailEditText.setText(email);
        }

        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                if (newProfile != null) {
                    String pictureUrl = newProfile.getProfilePictureUri(400, 400).toString();
                    fireBaseManager.onFacebookAccessTokenChange(Login.this, AccessToken.getCurrentAccessToken(), pictureUrl);
                }
            }
        };



        profileTracker.startTracking();

        btnFbLogin.setOnClickListener(this);
        btnEmailLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        mPasswordEditText.setOnTouchListener(this);
    }


    @Override
    public void onClick(View v) {


        if (v == btnSignUp) {
            Intent intent = new Intent(this, SignUpEmail.class);
            startActivity(intent);
        }


        if (v == btnEmailLogin) {
            FireBaseManager.getInstance(this).loginUserWithPassword(mEmailEditText.getText().toString().trim(),mPasswordEditText.getText().toString().trim());
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == mPasswordEditText) {


            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (event.getRawX() >= (mPasswordEditText.getRight() - mPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    mPasswordEditText.setTransformationMethod(null);
                    return true;
                }
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (mPasswordEditText.getRight() - mPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    mPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
                    return true;
                }
            }
        }
        return false;
    }


}

