package com.aboukhari.intertalking.Utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

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

}
