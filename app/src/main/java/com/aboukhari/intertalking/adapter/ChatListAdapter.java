package com.aboukhari.intertalking.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.CircularImageView;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.model.Message;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


/**
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class ChatListAdapter extends FirebaseListAdapter<Message> {

    private static final DateFormat CHAT_MSG_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    // The USERNAME for this client. We use this to indicate which messages originated from this user
    private String uid;
private Firebase ref;
    public ChatListAdapter(Query ref, Activity activity, int layout, String uid) {
        super(ref, Message.class, layout, activity);
        this.uid = uid;
        this.ref = ref.getRef().getRoot();
    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view    A view instance corresponding to the layout we passed to the constructor.
     * @param message An instance representing the current state of a chat message
     */


    @Override
    protected void populateView(final View view,final Message message) {
        // Map a Chat object to an entry in our listview
        Log.d("natija message", message.toString());
        String author = message.getAuthor();

        ref.getRoot().child("users").child(author).child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String imageUrl = dataSnapshot.getValue(String.class);
                TextView messageText = (TextView) view.findViewById(R.id.message);
                TextView dateText = (TextView) view.findViewById(R.id.date);
                CircularImageView imageView = (CircularImageView) view.findViewById(R.id.iv_avatar);

                messageText.setText(message.getMessage());

                CHAT_MSG_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
                dateText.setText(CHAT_MSG_DATE_FORMAT.format(message.getDate()));

                Utils.setImage(imageUrl, imageView);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });






        // LinearLayout linearChat = (LinearLayout) view.findViewById(R.id.linear_chat);
        //   LinearLayout linearMessage = (LinearLayout) view.findViewById(R.id.linear_message);
        //   authorText.setText(author + ": ");
        // If the message was sent by this user, color it differently
      /*  if (author.equals(uid)) {
            view.setBackgroundColor(Color.TRANSPARENT);
            messageText.setTextColor(Color.WHITE);
        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.LEFT;
            linearChat.setGravity(Gravity.LEFT);
            linearChat.setLayoutParams(layoutParams);
            messageText.setTextColor(Color.BLACK);
            dateText.setTextColor(Color.DKGRAY);
            Drawable drawable = view.getContext().getResources().getDrawable(R.drawable.message_friend_shape);
            linearMessage.setBackgroundDrawable(drawable);
        }
*/
        //   translateMessage(chat.getMessage(),messageText);


    }


  /*  private void translateMessage(String txt, final TextView messageText){
        String lang = "en-de";
        String text = txt;

        RestClient.get().translate(RestService.API_KEY, lang, text, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                Log.d("natija", jsonElement.getAsJsonObject().get("text").getAsJsonArray().get(0).toString());
                String result = jsonElement.getAsJsonObject().get("text").getAsJsonArray().get(0).toString();
                messageText.setText(result);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }*/
}