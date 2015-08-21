package com.aboukhari.intertalking.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.model.Place;
import com.aboukhari.intertalking.model.User;
import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by aboukhari on 16/07/2015.
 */
public abstract class Utils {


    public static Date stringToDate(String dateInString) {

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        try {
            return formatter.parse(dateInString);
        } catch (ParseException e) {
            return new Date(0L);
        }
    }

    public static String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        return formatter.format(date);
    }

    public static void exportDB(String dataBaseName) {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + "com.aboukhari.intertalking" + "/databases/" + dataBaseName;
        String backupDBPath = dataBaseName;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String setupRoomName(String myId, String friendId) {
        if (myId.compareTo(friendId) < 0) {
            return myId + "_" + friendId;
        }
        return friendId + "_" + myId;
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

    public static String getCountryByIso(String language, String iso) {
        Locale locale = new Locale(language, iso);
        return locale.getDisplayCountry();

    }

    public static void loadImage(Context context, String imageUrl, final ImageView imageView) {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
            ImageLoader.getInstance().init(config);
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.crop__ic_done)
                .showImageForEmptyUri(R.drawable.crop__ic_cancel)
                .showImageOnFail(R.mipmap.ic_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();


        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imageUrl, imageView, options);
    }


    public static void saveUserToPreferences(Context context, User user) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("user", json);
        prefsEditor.commit();
    }

    public static User getUserFromPreferences(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = mPrefs.getString("user", "");
        User user = gson.fromJson(json, User.class);

        return user;

    }

    public static int getAgeFromDate(Date date) {

        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.setTime(date);
        if (dob.after(now)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }
        int year1 = now.get(Calendar.YEAR);
        int year2 = dob.get(Calendar.YEAR);
        int age = year1 - year2;
        int month1 = now.get(Calendar.MONTH);
        int month2 = dob.get(Calendar.MONTH);
        if (month2 > month1) {
            age--;
        } else if (month1 == month2) {
            int day1 = now.get(Calendar.DAY_OF_MONTH);
            int day2 = dob.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1) {
                age--;
            }
        }

        return age;
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

    public static String getCountyIso(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimCountryIso();
    }

    public static Map<String, Object> objectToMap(Object object) {
        ObjectMapper m = new ObjectMapper();
        Map<String, Object> userMap = m.convertValue(object, Map.class);
        return userMap;
    }

    public static Cloudinary getCloudinary() {

        Map config = new HashMap();
        config.put("cloud_name", "dvn1zs11q");
        config.put("api_key", "637982581753689");
        config.put("api_secret", "OZEhUlBooq7Vybql2uCo3JK4Heg");
        Cloudinary cloudinary = new Cloudinary(config);

        return cloudinary;

    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
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
