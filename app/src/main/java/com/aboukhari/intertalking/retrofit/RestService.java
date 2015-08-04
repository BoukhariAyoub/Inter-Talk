package com.aboukhari.intertalking.retrofit;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

/**
 * Created by aboukhari on 10/04/2015.
 */
public interface RestService {

    String app_index = "/ws";


    @FormUrlEncoded
    @POST("/AutoCompleteCity")
    ArrayList<String> autoCompleteCity(@Field("q") String query);


    @FormUrlEncoded
    @POST("/GetCityDetails")
    JsonElement getCityDetails(@Field("fqcn") String query);

    @FormUrlEncoded
    @POST("/GetCityDetails")
    void getCityDetails(@Field("fqcn") String query, Callback<JsonElement> callback);


    @FormUrlEncoded
    @POST(app_index + "/trace")
    void sendTrace(@Field("date") String date, @Field("numero") String numero
            , @Field("flux") String flux, @Field("statut") String statut, @Field("etat") String etat, Callback<String> callback);

    @Multipart
    @POST(app_index + "/photo")
    void upload(@Part("myfile") TypedFile file, @Part("type") String type, Callback<String> cb);


    //  @POST(app_index + "/dump/body")
    @POST(app_index + "/rapport")
    void sendRapport(@Body TypedInput rapport, Callback<JsonElement> cb);


    @Multipart
    @POST(app_index + "/photo")
    void uploadphoto(@PartMap Map<String, TypedFile> files, @Part("type") String type, Callback<ArrayList<Integer>> cb);

    @Multipart
    @POST(app_index + "/photo")
    ArrayList<Integer> uploadphotoSync(@PartMap Map<String, TypedFile> files, @Part("type") String type);



    /* UNUSED */

    @GET(app_index + "/check/token")
    void checkToken(@Query("bearer") String bearer, Callback<JsonElement> callback);


    @GET(app_index + "/download/{id}")
    void downloadFile(@Path("id") int id, @Query("bearer") String bearer, Callback<JSONObject> callback);


}
