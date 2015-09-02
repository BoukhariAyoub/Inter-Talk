package com.aboukhari.intertalking.retrofit;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by aboukhari on 10/04/2015.
 */
public interface RestService {


    @GET("/place/autocomplete/json")
    JsonElement autoCompleteCity(@Query("input") String input,@Query("types") String types,@Query("language") String language,@Query("key") String key);


    @GET("/place/details/json")
    void getPlaceDetails(@Query("placeid") String placeid,@Query("language") String language,@Query("key") String key,Callback<JsonElement> callback);


    @GET("/GetCityDetails")
    JsonElement getCityDetails(@Query("fqcn") String query);

    @FormUrlEncoded
    @POST("/GetCityDetails")
    void getCityDetails(@Field("fqcn") String query, Callback<JsonElement> callback);

    @GET("/translate")
    void getTranslation(@Query("key") String key,@Query("lang") String lang,@Query("text") String text,@Query("options") String options,Callback<JsonElement> callback);






}
