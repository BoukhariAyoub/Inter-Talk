package com.aboukhari.intertalking.activity;

import android.content.Intent;

import com.aboukhari.intertalking.R;
import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class SplashActivity extends AwesomeSplash {
    @Override
    public void initSplash(ConfigSplash configSplash) {

          /* you don't have to override every property */

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.DodgerBlue); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(200); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.logo_sans_titre); //or any other drawable
        configSplash.setAnimLogoSplashDuration(1000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.Landing); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)



        //Customize Title
        configSplash.setTitleSplash("INTER TALK");
        configSplash.setTitleTextColor(R.color.white);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(800);
        configSplash.setAnimTitleTechnique(Techniques.Landing);
       configSplash.setTitleFont("fonts/OpenSans-Bold.ttf"); //provide string to your font located in assets/fonts/*/

    }

    @Override
    public void animationsFinished() {
        Intent intent = new Intent(this, Login.class);
        finish();
        startActivity(intent);



    }

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }*/




}
