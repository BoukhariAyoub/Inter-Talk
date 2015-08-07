package com.aboukhari.intertalking.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by aboukhari on 10/04/2015.
 */
public class RestClient {

    private static RestService REST_CLIENT;
    private static RestAdapter.Builder BUIlDER;

    public final static String GOOGLE_MAPS_ENDPOINT = "https://maps.googleapis.com/maps/api/";

    //private static String ENDPOINT = "http://gd.geobytes.com/";
    private static String ENDPOINT;


    static {

        //setupRestClient(ENDPOINT);
    }

    private RestClient() {

    }

    public static RestService get(String endpoint) {

        setupRestClient(endpoint);
        ENDPOINT = endpoint;
        RestAdapter restAdapter = BUIlDER.build();
        restAdapter.create(RestService.class);

        //  REST_CLIENT =restAdapter.create(RestService.class);
        return restAdapter.create(RestService.class);
    }

    private static void setupRestClient(String endPoint) {
        OkClient client = //new OkClient();
                getOkClient();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        GsonConverter gsonConverter = new GsonConverter(gson);
        BUIlDER = new RestAdapter.Builder()
                .setEndpoint(endPoint)
                .setClient(client)
                .setConverter(gsonConverter)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("natija retrofit"));


        //    REST_CLIENT =restAdapter.create(RestService.class);

    }

    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;


                }
            });

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static OkClient getOkClient() {
        OkHttpClient client1 = new OkHttpClient();
        client1 = getUnsafeOkHttpClient();
        OkClient _client = new OkClient(client1);
        return _client;
    }

}
