package com.aboukhari.intertalking.Utils;

import android.content.Context;
import android.os.Environment;
import android.widget.ImageView;

import com.aboukhari.intertalking.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public static  String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        return formatter.format(date);
    }

    public static void exportDB(String dataBaseName){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.aboukhari.intertalking" +"/databases/"+dataBaseName;
        String backupDBPath = dataBaseName;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static String setupRoomName(String myId, String friendId) {
        if (myId.compareTo(friendId) < 0) {
            return myId + "_" + friendId;
        }
        return friendId + "_" + myId;
    }

    public static String loadJSONFromAsset(Context context,String fileName) {
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

    public static String getCountryByIso(String language,String iso){
        Locale locale = new Locale(language, iso);
       return locale.getDisplayCountry();

    }

    public static void setImage(String imageUrl, final ImageView imageView) {

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


}
