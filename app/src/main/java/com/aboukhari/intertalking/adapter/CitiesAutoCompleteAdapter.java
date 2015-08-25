package com.aboukhari.intertalking.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.aboukhari.intertalking.Utils.Constants;
import com.aboukhari.intertalking.model.Place;
import com.aboukhari.intertalking.retrofit.RestClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by aboukhari on 03/08/2015.
 */
public class CitiesAutoCompleteAdapter extends ArrayAdapter<Place> implements Filterable {

    private ArrayList<Place> resultList;
    private String language;

    public CitiesAutoCompleteAdapter(Context context, int textViewResourceId, String language) {
        super(context, textViewResourceId);
        this.language = language;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Place getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            public void stopFiltering(){

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                if (constraint != null && constraint.length() > 2) {
                    // Retrieve the autocomplete results.
                    resultList = mapsJsonToPlaces(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }

    private ArrayList<Place> mapsJsonToPlaces(String constraint) {
        JsonElement json = RestClient.get(RestClient.GOOGLE_MAPS_ENDPOINT).autoCompleteCity(constraint.toString(), "(cities)", language, Constants.GOOGLE_API_KEY);
        ArrayList<Place> places = new ArrayList<>();
        JsonArray predictions = json.getAsJsonObject().getAsJsonArray("predictions");
        for (int i = 0, size = predictions.size(); i < size; i++) {
            JsonObject jsonPlace = predictions.get(i).getAsJsonObject();
            String description = jsonPlace.get("description").getAsString();
            String id = jsonPlace.get("place_id").getAsString();

            Place place = new Place(id, description);
            places.add(place);
        }
        return places;
    }
}


