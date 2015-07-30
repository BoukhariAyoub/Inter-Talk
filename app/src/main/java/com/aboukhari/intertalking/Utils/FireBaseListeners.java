package com.aboukhari.intertalking.Utils;

import android.content.Context;

import com.aboukhari.intertalking.database.DatabaseManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by aboukhari on 29/07/2015.
 */
public class FireBaseListeners {

    Context context;
    private DatabaseManager databaseManager;
    Firebase ref;


    public FireBaseListeners(Context context, DatabaseManager databaseManager, Firebase ref) {
        this.context = context;
        this.databaseManager = databaseManager;
        this.ref = ref;
    }


    void messageListener(final String roomName){

    }

    void roomListener(final String roomName){

    }

    void lastReadListener(final String roomName){

        ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms").child(roomName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long read = dataSnapshot.getValue(Long.class);
                databaseManager.updateLastSeen(roomName, read);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


}
