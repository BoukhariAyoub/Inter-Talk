package com.aboukhari.intertalking.Utils;

import android.content.Context;

import com.aboukhari.intertalking.model.Language;
import com.aboukhari.intertalking.model.Place;
import com.aboukhari.intertalking.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by aboukhari on 26/08/2015.
 */
public abstract class JsonUtils {



    public static ArrayList<Language> jsonToLanguages(Context context) {
        ArrayList<Language> languages = new ArrayList<>();
        try {
           String json = loadJSONFromAsset(context, "languages.json");
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                String iso = jsonArray.getString(i);

                Language language = new Language(iso);
                languages.add(language);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return languages;
    }


    public static String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static ArrayList<User> generateRandomUsers(Context context) {
        String json = loadJSONFromAsset(context, "users.json");
        ArrayList<User> users = new ArrayList<>();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = (JsonArray) jsonParser.parse(json);
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                Gson gson = new GsonBuilder()
                        .setDateFormat("MM/dd/yyyy").create();
                User user = gson.fromJson(jsonObject, User.class);
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }


    public static Place jSonToPlace(JsonElement json) {
        Place place = new Place();
        JsonObject placeJson = json.getAsJsonObject().get("result").getAsJsonObject();

        place.setId(placeJson.get("place_id").getAsString());
        place.setDescription(placeJson.get("formatted_address").getAsString());
        place.setUrl(placeJson.get("url").getAsString());


        JsonObject location = placeJson.get("geometry").getAsJsonObject().get("location").getAsJsonObject();
        place.setLatitude(location.get("lat").getAsDouble());
        place.setLongitude(location.get("lng").getAsDouble());


        JsonArray components = placeJson.get("address_components").getAsJsonArray();


        for (JsonElement comp : components) {
            JsonObject jsonComp = comp.getAsJsonObject();
            JsonArray types = jsonComp.get("types").getAsJsonArray();
            String shortName = jsonComp.get("short_name").getAsString();
            String longName = jsonComp.get("long_name").getAsString();

            String type = types.get(0).getAsString();

            switch (type) {
                case "locality":
                    place.setCity(longName);
                    break;
                case "administrative_area_level_1":
                    place.setRegion(shortName);
                    break;
                case "country":
                    place.setCountry(shortName);
                    break;
            }
        }
        return place;
    }






}
