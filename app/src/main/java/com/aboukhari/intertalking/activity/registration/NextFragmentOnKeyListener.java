package com.aboukhari.intertalking.activity.registration;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by aboukhari on 20/08/2015.
 */
public class NextFragmentOnKeyListener implements View.OnKeyListener {

    RegistrationActivity parentFragment;

    public NextFragmentOnKeyListener(FragmentActivity parent) {
        parentFragment = (RegistrationActivity) parent;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.d("natija", keyCode + " -  event " + event.getAction());
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            parentFragment.goToNextFragment();
            return true;
        }
        return false;
    }
}
