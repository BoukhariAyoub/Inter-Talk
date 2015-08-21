package com.aboukhari.intertalking.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.adapter.FindUsersRecyclerAdapter;
import com.aboukhari.intertalking.adapter.MyGridLayoutManager;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;

public class FindUsers extends AppCompatActivity {

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);
        Firebase.setAndroidContext(this);

        Firebase ref = new Firebase(getString(R.string.firebase_url));

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);


        FireBaseManager fireBaseManager = new FireBaseManager(this);
        fireBaseManager.addGeneratedUsers();

        Query query = ref.child("users");
        FindUsersRecyclerAdapter recyclerAdapter = new FindUsersRecyclerAdapter(this,query);
        recyclerAdapter.setHasStableIds(true);
        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        toolbar.setTitle("");
        mTitle.setText("Search For Contacts");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.mipmap.ic_begginer);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(this,1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(recyclerAdapter);
        mRecyclerView.setItemAnimator(new FadeInAnimator());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            setupWindowAnimations();

        }


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(2000);
        getWindow().setReturnTransition(fade);
    }




}
