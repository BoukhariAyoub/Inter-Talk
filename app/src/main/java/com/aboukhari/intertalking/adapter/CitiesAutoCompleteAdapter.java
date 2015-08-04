package com.aboukhari.intertalking.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.aboukhari.intertalking.retrofit.RestClient;

import java.util.ArrayList;

/**
 * Created by aboukhari on 03/08/2015.
 */
public class CitiesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> resultList;

    public CitiesAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                if (constraint != null && constraint.length() > 2) {
                    // Retrieve the autocomplete results.
                    resultList = RestClient.get().autoCompleteCity(constraint.toString());
                    if (resultList.size()> 0 && resultList.get(0).isEmpty()) {
                        resultList.remove(0);
                    }
                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null && results.count > 0) {
                    Log.d("publish", constraint.length() + " length");
                    Log.d("publish", results.count + " count");
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
}


