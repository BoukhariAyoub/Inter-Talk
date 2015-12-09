package com.aboukhari.intertalking.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.activity.main.Main3Activity;
import com.aboukhari.intertalking.model.User;
import com.daimajia.androidanimations.library.Techniques;
import com.dd.processbutton.iml.ActionProcessButton;
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
    EditText mEmailEditText, mPasswordEditText, mEmailSignUpEditText;
    Firebase ref;
    CallbackManager callbackManager;
    ActionProcessButton btnSignUpEmail;
    TextView mLinkSignUp, mLinkSignIn,mLinkBack;
    LinearLayout mLinearSignIn, mLinearSignUp, mLinearEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
        } catch (Exception ex) {

        }

        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login_2);
        final FireBaseManager fireBaseManager = new FireBaseManager(this);
        callbackManager = CallbackManager.Factory.create();
        ref = new Firebase(getString(R.string.firebase_url));


        Utils.getCurrentHashForFacebook(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        btnEmailLogin = (Button) findViewById(R.id.btn_login);
        btnSignUpEmail = (ActionProcessButton) findViewById(R.id.btn_send_email);
        btnFbLogin = (Button) findViewById(R.id.btn_fb_login);
        btnSignUp = (Button) findViewById(R.id.btn_email_signup);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);
        mEmailEditText = (EditText) findViewById(R.id.et_email);
        mEmailSignUpEditText = (EditText) findViewById(R.id.et_email_signup);
        mLinkSignIn = (TextView) findViewById(R.id.link_signin);
        mLinkSignUp = (TextView) findViewById(R.id.link_signup);
        mLinkBack= (TextView) findViewById(R.id.link_back);
        mLinearSignIn = (LinearLayout) findViewById(R.id.layout_sign_in);
        mLinearSignUp = (LinearLayout) findViewById(R.id.layout_sign_up);
        mLinearEmail = (LinearLayout) findViewById(R.id.layout_email_sign_up);


        btnFbLogin.setOnClickListener(this);
        btnEmailLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        mPasswordEditText.setOnTouchListener(this);
        btnSignUpEmail.setOnClickListener(this);
        mLinkSignUp.setOnClickListener(this);
        mLinkSignIn.setOnClickListener(this);
        mLinkBack.setOnClickListener(this);


        if (ref.getAuth() != null) {
            FireBaseManager.getInstance(this).addCurrentUserChangeListener();
            User user = Utils.getUserFromPreferences(this);
            if (user != null && !user.getFirstLogin()) {
                Intent intent = new Intent(this,
                        Main3Activity.class);
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
                    fireBaseManager.onFacebookAccessTokenChange(AccessToken.getCurrentAccessToken(), pictureUrl);
                }
            }
        };


        profileTracker.startTracking();


    }


    @Override
    public void onClick(View v) {


        if (v == btnSignUpEmail) {

            String email = mEmailSignUpEditText.getText().toString().trim();
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                btnSignUpEmail.setMode(ActionProcessButton.Mode.ENDLESS);
                FireBaseManager.getInstance(this).createUser(email, btnSignUpEmail);
                btnSignUpEmail.setProgress(1);
                btnSignUpEmail.setText("Loading...");

            } else {
                Toast.makeText(this, "Please Enter A Valid Email", Toast.LENGTH_SHORT).show();
            }

            btnSignUpEmail.setMode(ActionProcessButton.Mode.ENDLESS);
        }




        if (v == btnEmailLogin) {
            FireBaseManager.getInstance(this).loginUserWithPassword(mEmailEditText.getText().toString().trim(), mPasswordEditText.getText().toString().trim());
        }


        if (v == btnFbLogin) {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            } else {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_friends", "user_birthday"));
            }
        }

        if (v == btnSignUp) {
            Utils.showAndHide(mLinearSignUp, mLinearEmail, Techniques.FadeOutUp, Techniques.FadeInDown);
        }

        if (v == mLinkSignIn) {
            Utils.showAndHide(mLinearSignUp, mLinearSignIn, Techniques.FadeOutUp, Techniques.FadeInDown);
        }

        if (v == mLinkSignUp) {
            Utils.showAndHide(mLinearSignIn,mLinearSignUp , Techniques.FadeOutUp, Techniques.FadeInDown);
        }

        if (v == mLinkBack) {
            Utils.showAndHide(mLinearEmail,mLinearSignUp , Techniques.FadeOutUp, Techniques.FadeInDown);
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

