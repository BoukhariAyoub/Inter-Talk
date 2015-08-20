package com.aboukhari.intertalking.activity.registration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.activity.Login;

/**
 * Created by aboukhari on 20/08/2015.
 */
public class SignUpEmail extends Activity implements View.OnClickListener {

    EditText mEmailEditText;
    ImageButton mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_email);

        mEmailEditText = (EditText) findViewById(R.id.et_email);
        mSendButton = (ImageButton) findViewById(R.id.btn_done);
        mSendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mSendButton.getId()) {
            String email = mEmailEditText.getText().toString().trim();
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                FireBaseManager.getInstance(this).createUser(email);
                Intent intent = new Intent(this, Login.class);
                intent.putExtra("email", email);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please Enter A Valid Email", Toast.LENGTH_SHORT);
            }
        }
    }
}
