package com.aboukhari.intertalking.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.adapter.CitiesAutoCompleteAdapter;
import com.aboukhari.intertalking.retrofit.RestClient;
import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GeoNames extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    EditText cityEditText;
    Button button;

    private static final String LOG_TAG = "Google Places Autocomplete";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBHQI4nagS-4FTD_kDTiTRArFJAncz5dnk";
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_names);

        button = (Button) findViewById(R.id.newbutton);


        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        autoCompView.setThreshold(2);

        autoCompView.setAdapter(new CitiesAutoCompleteAdapter(this, R.layout.support_simple_spinner_dropdown_item,""));
        autoCompView.setOnItemClickListener(this);
        button.setOnClickListener(this);

        autoCompView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                city = null;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        city = (String) adapterView.getItemAtPosition(position);
    }

    @Override
    public void onClick(View v) {

        if (v == button) {
            if (city == null) {
                Toast.makeText(this, "Please Choose A valid City", Toast.LENGTH_SHORT).show();

            } else {

                RestClient.get(RestClient.GOOGLE_MAPS_ENDPOINT).getCityDetails(city, new Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement jsonElement, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
            // String city = RestClient.get().getCityDetails(cityEditText.getText().toString()).toString();
            //  Log.d("natija city",city);


        }

    }
}
