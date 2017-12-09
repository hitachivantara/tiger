package org.pentaho.tiger.lumada;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.pentaho.tiger.lumada.entity.AssetProperty;
import org.pentaho.tiger.lumada.request.AssetNewRequest;
import org.pentaho.tiger.lumada.request.AssetViewEventDataRequest;
import org.pentaho.tiger.lumada.request.LoginRequest;
import org.pentaho.tiger.lumada.response.AssetGetAccessTokenResponse;
import org.pentaho.tiger.lumada.response.AssetViewEventDataResponse;
import org.pentaho.tiger.lumada.response.AssetViewResponse;
import org.pentaho.tiger.lumada.response.LoginResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
Command line arguments

    --debug     output debug message
    --host      host of the Lumada server

    Example:
    java -cp .:./lib/* org.pentaho.tiger.lumada.LumadaRestClient --debug --host 10.0.2.15

    Make sure all required jars are in "lib" directory:
        gson-2.8.2.jar
        okhttp-3.9.1.jar
*/
public class LumadaRestClient {
    public static boolean DEBUG = false;

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static LumadaRestHelper helper = new LumadaRestHelper();

    private static String LUMADA_HOST = "localhost";
    private static String LUMADA_USER = "admin";
    private static String LUMADA_PASS = "admin";

    private static String LOGIN_ENDPOINT = "https://%s/v1/security/oauth/token";
    private static String ASSET_VIEW_ENDPOINT = "https://%s/v1/asset-management/assets/%s";
    private static String ASSET_VIEW_EVENT_DATA_ENDPOINT = "https://%s/v1/asset-data/assets/%s/events?startTime=%s&endTime=%s";
    private static String ASSET_GET_ACCESS_TOKEN_ENDPOINT = "https://%s/v1/asset-management/assets/%s/token";
    private static String ASSET_ADD_NEW_AVATAR_ENDPOINT = "https://%s/v1/asset-management/assets";

    private static SimpleDateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static LoginResponse login(LoginRequest loginRequest) {
        OkHttpClient client = helper.createHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("grant_type", loginRequest.getGrantType())
                .addFormDataPart("username", loginRequest.getUsername())
                .addFormDataPart("password", loginRequest.getPassword())
                .addFormDataPart("scope", loginRequest.getScope())
                .addFormDataPart("client_id", loginRequest.getClientId())
                .build();

        String url = String.format(LOGIN_ENDPOINT, LUMADA_HOST);


        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(requestBody)
                .build();
        String body;
        Response response;
        try {
            response = client.newCall(request).execute();
            body = response.body().string();

            if (DEBUG) {
                System.out.println(body);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            LoginResponse loginResponse = gson.fromJson(body, LoginResponse.class);

            if (DEBUG) {
                System.out.println(gson.toJson(loginResponse));
            }

            return loginResponse;
        } catch (IOException ioe) {
            System.err.println(ioe);
        } finally {
        }

        return null;
    }

    public static void viewAsset(String token, String assetId) {
        String url = String.format(ASSET_VIEW_ENDPOINT, LUMADA_HOST, assetId);
        if (DEBUG) {
            System.out.println(url);
        }

        AssetViewResponse response = (AssetViewResponse) helper.execute(token, url, AssetViewResponse.class);
        System.out.println(response);
    }

    public static void viewEvent(String token, AssetViewEventDataRequest viewEventRequest) {
        String start = EVENT_DATE_FORMAT.format(viewEventRequest.getStart());
        String end = EVENT_DATE_FORMAT.format(viewEventRequest.getEnd());

        String url = String.format(ASSET_VIEW_EVENT_DATA_ENDPOINT, LUMADA_HOST, viewEventRequest.getAssetId(), start, end);
        if (DEBUG) {
            System.out.println(url);
        }

        AssetViewEventDataResponse response = (AssetViewEventDataResponse) helper.execute(token, url, AssetViewEventDataResponse.class);
        System.out.println(response);
    }

    public static String getAssetAccessToken(String token, String assetId) {
        String url = String.format(ASSET_GET_ACCESS_TOKEN_ENDPOINT, LUMADA_HOST, assetId);
        if (DEBUG) {
            System.out.println(url);
        }

        AssetGetAccessTokenResponse accessTokenResp = (AssetGetAccessTokenResponse) helper.execute(token, url, AssetGetAccessTokenResponse.class);
        if (accessTokenResp != null) {
            return accessTokenResp.getToken();
        }
        return null;
    }

    public static String addAsset(String token, AssetNewRequest assetNewRequest) {
        String url = String.format(ASSET_ADD_NEW_AVATAR_ENDPOINT, LUMADA_HOST);
        if (DEBUG) {
            System.out.println(url);
        }

        AssetViewResponse response = (AssetViewResponse) helper.execute(token, url, AssetViewResponse.class, new Gson().toJson(assetNewRequest), "POST");
        System.out.println(response);
        if (response != null) {
            return response.getId();
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        try {
            if (args.length != 0) {
                for (int i = 0; i < args.length; i++) {
                    if ("--debug".equals(args[i])) {
                        DEBUG = true;
                        LumadaRestHelper.DEBUG = true;
                    } else if ("--host".endsWith(args[i])) {
                        LUMADA_HOST = args[++i];
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Invalid arguments");
            System.err.println(ex);
        }

        String username = "admin";
        String password = "YOUR_PASSWORD";//YOUR_PASSWORD

        System.out.println("Connecting to Lumada");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        LoginResponse loginResponse = login(loginRequest);

        if (loginResponse == null) {
            System.out.println("Could not login.");
            return;
        }

        String token = loginResponse.getAccessToken();
        if (DEBUG) {
            System.out.println("access token:" + token);
        }

        if (token == null) {
            System.out.println("Could not obtain valid token.");
            return;
        }

        System.out.println("----------------------------------------");
        String assetId = "9d23824d-5ac1-48e9-8b97-cad607938a8f";
        LumadaRestClient.viewAsset(token, assetId);

        System.out.println("----------------------------------------");
        AssetViewEventDataRequest request = new AssetViewEventDataRequest();
        //Query event for 60 day
        Date end = new Date();
        Date start = new Date(end.getTime() - 1000l * 60 * 60 * 24 * 30);
        request.setEnd(end);
        request.setStart(start);
        request.setAssetId(assetId);
        viewEvent(token, request);

        System.out.println("----------------------------------------");
        String assetAccessToken = getAssetAccessToken(token, assetId);
        System.out.println("Asset access token: " + assetAccessToken);

        System.out.println("----------------------------------------");
        AssetNewRequest assetNewRequest = new AssetNewRequest();
        assetNewRequest.setName("Doosan B13R-5B");
        assetNewRequest.setAssetTypeId("662bf4d4-d14c-11e7-8bb5-080027e18512");

        List<AssetProperty> properties = new ArrayList<AssetProperty>();
        properties.add(new AssetProperty("location","Seattle"));
        properties.add(new AssetProperty("Speed","5"));
        assetNewRequest.setPriperties(properties);
        String newId = addAsset(token, assetNewRequest);
        System.out.println("New Id: " + newId);
    }
}