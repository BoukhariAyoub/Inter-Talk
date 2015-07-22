package com.aboukhari.intertalking.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.model.Room;
import com.firebase.client.Query;

import java.text.DateFormat;

/**
 * Created by aboukhari on 15/07/2015.
 */
public class RoomListAdapter extends FirebaseListAdapter<Room> {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.FULL);


    /**
     * @param ref        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                   combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param layout     This is the layout used to represent a single list item. You will be responsible for populating an
     *                   instance of the corresponding view with the data from an instance of modelClass.
     * @param activity   The activity containing the ListView
     */
    public RoomListAdapter(Query ref, Activity activity, int layout) {
        super(ref, Room.class, layout, activity);
    }


    @Override
    protected void populateView(View view, Room room) {
        ((TextView) view.findViewById(R.id.tv_name)).setText(room.getName());
        ((TextView) view.findViewById(R.id.tv_date)).setText(DATE_FORMAT.format(room.getDate()));
    }



}
