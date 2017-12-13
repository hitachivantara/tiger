package org.pentaho.tiger.lumada;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.pentaho.tiger.lumada.request.AssetNewRequest;
import org.pentaho.tiger.lumada.request.AssetTypeNewRequest;
import org.pentaho.tiger.lumada.request.AssetViewEventDataRequest;
import org.pentaho.tiger.lumada.request.LoginRequest;
import org.pentaho.tiger.lumada.response.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class LumadaRestClient {
    public static boolean DEBUG = false;

    private static SimpleDateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static LumadaRestHelper helper = new LumadaRestHelper();

    private static String LOGIN_ENDPOINT = "https://%s/v1/security/oauth/token";
    private static String ASSET_VIEW_ENDPOINT = "https://%s/v1/asset-management/assets/%s";
    private static String ASSET_VIEW_EVENT_DATA_ENDPOINT = "https://%s/v1/asset-data/assets/%s/events?startTime=%s&endTime=%s";
    private static String ASSET_GET_ACCESS_TOKEN_ENDPOINT = "https://%s/v1/asset-management/assets/%s/token";
    private static String ASSET_ADD_NEW_AVATAR_ENDPOINT = "https://%s/v1/asset-management/assets";
    private static String ASSET_ADD_NEW_AVATAR_TYPE_ENDPOINT = "https://%s/v1/asset-management/asset-types";
    private static String FILE_UPLOAD_ENDPOINT = "https://%s/v1/file-management/files/";

    private String host;
    private String token;

    public LumadaRestClient(String host) {
        this.host = host;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        String url = String.format(LOGIN_ENDPOINT, host);
        if (DEBUG) {
            System.out.println(url);
        }

        OkHttpClient client = helper.createHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("grant_type", loginRequest.getGrantType())
                .addFormDataPart("username", loginRequest.getUsername())
                .addFormDataPart("password", loginRequest.getPassword())
                .addFormDataPart("scope", loginRequest.getScope())
                .addFormDataPart("client_id", loginRequest.getClientId())
                .addFormDataPart("realm", loginRequest.getRealm())
                .build();

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

    public String uploadFile(File file) {
        String url = String.format(FILE_UPLOAD_ENDPOINT, host);
        if (DEBUG) {
            System.out.println(url);
        }

        OkHttpClient client = helper.createHttpClient();

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(formBody).build();

        String body;
        Response response;
        try {
            response = client.newCall(request).execute();
            body = response.body().string();

            if (DEBUG) {
                System.out.println(body);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            UploadFileResponse uploadResponse = gson.fromJson(body, UploadFileResponse.class);

            if (DEBUG) {
                System.out.println(gson.toJson(uploadResponse));
            }

            return uploadResponse.getId();
        } catch (IOException ioe) {
            System.err.println(ioe);
        } finally {
        }

        return null;
    }

    public void viewAsset(String assetId) {
        String url = String.format(ASSET_VIEW_ENDPOINT, host, assetId);
        if (DEBUG) {
            System.out.println(url);
        }

        AssetViewResponse response = (AssetViewResponse) helper.execute(token, url, AssetViewResponse.class);
        if (DEBUG) {
            System.out.println(gson.toJson(response));
        }
    }

    public void viewEvent(AssetViewEventDataRequest viewEventRequest) {
        String start = EVENT_DATE_FORMAT.format(viewEventRequest.getStart());
        String end = EVENT_DATE_FORMAT.format(viewEventRequest.getEnd());

        String url = String.format(ASSET_VIEW_EVENT_DATA_ENDPOINT, host, viewEventRequest.getAssetId(), start, end);
        if (DEBUG) {
            System.out.println(url);
        }

        AssetViewEventDataResponse response = (AssetViewEventDataResponse) helper.execute(token, url, AssetViewEventDataResponse.class);
        if (DEBUG) {
            System.out.println(gson.toJson(response));
        }
    }

    public String getAssetAccessToken(String assetId) {
        String url = String.format(ASSET_GET_ACCESS_TOKEN_ENDPOINT, host, assetId);
        if (DEBUG) {
            System.out.println(url);
        }

        AssetGetAccessTokenResponse response = (AssetGetAccessTokenResponse) helper.execute(token, url, AssetGetAccessTokenResponse.class);
        if (DEBUG) {
            System.out.println(gson.toJson(response));
        }
        if (response != null) {
            return response.getToken();
        }
        return null;
    }

    public String addAsset(AssetNewRequest assetNewRequest) {
        String url = String.format(ASSET_ADD_NEW_AVATAR_ENDPOINT, host);
        if (DEBUG) {
            System.out.println(url);
        }

        AssetViewResponse response = (AssetViewResponse) helper.execute(token, url, AssetViewResponse.class, new Gson().toJson(assetNewRequest), "POST");
        if (DEBUG) {
            System.out.println(gson.toJson(response));
        }
        if (response != null) {
            return response.getId();
        }

        return null;
    }

    public String addAssetType(AssetTypeNewRequest assetTypeNewRequest) {
        String url = String.format(ASSET_ADD_NEW_AVATAR_TYPE_ENDPOINT, host);
        if (DEBUG) {
            System.out.println(url);
        }

        AssetTypeViewResponse response = (AssetTypeViewResponse) helper.execute(token, url, AssetTypeViewResponse.class, new Gson().toJson(assetTypeNewRequest), "POST");
        if (DEBUG) {
            System.out.println(gson.toJson(response));
        }
        if (response != null) {
            return response.getId();
        }

        return null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}