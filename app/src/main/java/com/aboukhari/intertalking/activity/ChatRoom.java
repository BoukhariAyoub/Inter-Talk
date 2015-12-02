package com.aboukhari.intertalking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.activity.main.Conversations;
import com.aboukhari.intertalking.activity.profile.ProfileView;
import com.aboukhari.intertalking.adapter.MessageRecyclerAdapter;
import com.aboukhari.intertalking.adapter.MyGridLayoutManager;
import com.aboukhari.intertalking.database.DatabaseManager;
import com.aboukhari.intertalking.model.Message;
import com.aboukhari.intertalking.model.User;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.client.Firebase;

import java.util.Date;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;


public class ChatRoom extends AppCompatActivity implements View.OnClickListener {

    private Firebase ref;
    private RecyclerView recyclerView;
    private String roomName;
    private DatabaseManager databaseManager;
    private FireBaseManager fireBaseManager;
    private MessageRecyclerAdapter messageRecyclerAdapter;
    ImageView mTranslateImageView, mActivatedImageView, mSearchImageView, mSendPictureImageView, mBackImageView, mAvatarImageView;
    LinearLayout mLinearAttach;
    FrameLayout mTranslationFrameLayout;
    boolean isTranslationActivated;
    Date enabledDate;
    User mFriend;
    boolean isLayoutShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);


        databaseManager = DatabaseManager.getInstance(this);
        fireBaseManager = new FireBaseManager(this);

        isTranslationActivated = true; //TODO add from shared preferences
        enabledDate = new Date();


        ImageView avatarImageView = (ImageView) findViewById(R.id.iv_avatar);
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mTranslateImageView = (ImageView) findViewById(R.id.toolbar_auto_translate);
        mActivatedImageView = (ImageView) findViewById(R.id.toolbar_translate_activated);
        mSearchImageView = (ImageView) findViewById(R.id.toolbar_search);
        mSendPictureImageView = (ImageView) findViewById(R.id.toolbar_send_picture);
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mAvatarImageView = (ImageView) findViewById(R.id.iv_avatar);
        mLinearAttach = (LinearLayout) findViewById(R.id.layout_attach);

        mTranslationFrameLayout = (FrameLayout) findViewById(R.id.translation_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mTranslateImageView.setOnClickListener(this);
        mActivatedImageView.setOnClickListener(this);
        mSearchImageView.setOnClickListener(this);
        mSendPictureImageView.setOnClickListener(this);
        mBackImageView.setOnClickListener(this);
        mAvatarImageView.setOnClickListener(this);

        roomName = getIntent().getStringExtra("roomName");
        mFriend = getIntent().getParcelableExtra("friend");
        String title = mFriend.getDisplayName();

        textView.setText(title);
        Utils.loadImage(this, mFriend.getImageUrl(), avatarImageView);

        FireBaseManager.currentRoom = roomName;

        // Setup our Firebase ref
        ref = new Firebase(getString(R.string.firebase_url));

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);

        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        fireBaseManager.updateLastRead(roomName);
    }

    @Override
    public void onStart() {
        super.onStart();

        messageRecyclerAdapter = new MessageRecyclerAdapter(ref.child("messages").child(roomName), fireBaseManager, this);
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(messageRecyclerAdapter);
        recyclerView.setItemAnimator(new FadeInAnimator());

        messageRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                Log.d("natija recycler", "positionStart " + positionStart + " - itemCount " + itemCount);
                //   recyclerView.smoothScrollToPosition(positionStart + itemCount - 1);
                recyclerView.scrollToPosition(positionStart + itemCount - 1);
            }

            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("natija recycler", "count " + messageRecyclerAdapter.getItemCount());
                //   recyclerView.smoothScrollToPosition(messageRecyclerAdapter.getItemCount() - 1);
                recyclerView.scrollToPosition(messageRecyclerAdapter.getItemCount() - 1);

            }
        });

        FireBaseManager.currentRoom = roomName;


    }

    @Override
    public void onStop() {
        super.onStop();
        messageRecyclerAdapter.cleanup();
        fireBaseManager.updateLastRead(roomName);
        fireBaseManager.checkUnread(roomName);
        Conversations.conversationsRecyclerAdapter.notifyDataSetChanged();
        FireBaseManager.currentRoom = null;
    }


    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            Message message = new Message(input, ref.getAuth().getUid());
            ref.child("messages").child(roomName).push().setValue(message);
            inputText.setText("");
        }
    }

    public Date getEnabledDate() {
        return enabledDate;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == mActivatedImageView.getId() || v.getId() == mTranslateImageView.getId()) {
            isTranslationActivated = !isTranslationActivated;
            int drawable = isTranslationActivated ? android.R.drawable.presence_online : android.R.drawable.presence_offline;
            enabledDate = isTranslationActivated ? new Date() : new Date(Long.MAX_VALUE);
            mActivatedImageView.setImageDrawable(ContextCompat.getDrawable(this, drawable));
        }

        if (v.getId() == mBackImageView.getId()) {
            this.finish();
        }
        if (v.getId() == mAvatarImageView.getId()) {
            Intent intent = new Intent(this, ProfileView.class);
            intent.putExtra("user", mFriend);
            startActivity(intent);

        }

        mLinearAttach.setEnabled(false);
        if (v.getId() == mSendPictureImageView.getId()) {
            if (isLayoutShown) {
                hideView(mLinearAttach);
            } else {
                showView(mLinearAttach);
            }
        }
    }


    private void showView(View view) {
        Log.d("natija", view.isEnabled() + " show ");

        view.setVisibility(View.VISIBLE);
        view.setEnabled(true);
        YoYo.with(Techniques.ZoomInDown)
                .duration(1000)
                .playOn(view);
        isLayoutShown = true;
     /*   view.setVisibility(View.VISIBLE);
        view.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.slide_in));*/


    }

    private void hideView(final View view) {

        Log.d("natija", view.isEnabled() + " hide ");
        YoYo.with(Techniques.ZoomOutUp)
                .duration(1000)
                .playOn(view);
        isLayoutShown = false;
      /*  view.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.slide_out));
        view.setVisibility(View.GONE);*/


    }
}
