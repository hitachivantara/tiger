package org.pentaho.tiger.lumada;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.pentaho.tiger.lumada.entity.Asset;
import org.pentaho.tiger.lumada.entity.AssetProperty;
import org.pentaho.tiger.lumada.request.AssetViewEventDataRequest;
import org.pentaho.tiger.lumada.request.LoginRequest;
import org.pentaho.tiger.lumada.response.AssetGetAccessTokenResponse;
import org.pentaho.tiger.lumada.response.AssetViewEventDataResponse;
import org.pentaho.tiger.lumada.response.LoginResponse;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LumadaRestClient {
    public static boolean DEBUG = true;

    private static String LUMADA_HOST = "localhost";

    private static String LOGIN_ENDPOINT = "https://%s/v1/security/oauth/token";
    private static String ASSET_VIEW_ENDPOINT = "https://%s/v1/asset-management/assets/%s";
    private static String ASSET_VIEW_EVENT_DATA_ENDPOINT = "https://%s/v1/asset-data/assets/%s/events?startTime=%s&endTime=%s";
    private static String ASSET_GET_ACCESS_TOKEN_ENDPOINT = "https://%s/v1/asset-management/assets/%s/token";

    private static SimpleDateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static LumadaTrustManager trustManager = new LumadaTrustManager();

    private static OkHttpClient createHttpClient() {
        final TrustManager[] trustManagers = new TrustManager[]{trustManager};

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        try {
            final String PROTOCOL = "SSL";
            SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
            KeyManager[] keyManagers = null;
            SecureRandom secureRandom = new SecureRandom();
            sslContext.init(keyManagers, trustManagers, secureRandom);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            okHttpClientBuilder.sslSocketFactory(sslSocketFactory, trustManager);
        } catch (Exception e) {
            System.err.println(e);
        }

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        return okHttpClientBuilder.build();
    }

    private static Request buildRequest(String url, String token) {
        return new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }


    public static LoginResponse login(LoginRequest loginRequest) {
        OkHttpClient client = createHttpClient();

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
        OkHttpClient client = createHttpClient();

        String url = String.format(ASSET_VIEW_ENDPOINT, LUMADA_HOST, assetId);
        if (DEBUG) {
            System.out.println(url);
        }

        Request request = buildRequest(url, token);

        String body;
        Response response;
        try {
            response = client.newCall(request).execute();
            body = response.body().string();
            if (DEBUG) {
                System.out.println(body);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Asset asset = gson.fromJson(body, Asset.class);

            List<AssetProperty> properties = asset.getPriperties();
            if (properties != null) {
                for (AssetProperty ap : properties) {
                    System.out.println(asset.getName() + "=" + ap.getValue());
                }
            }

            if (DEBUG) {
                System.out.println(gson.toJson(asset));
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        } finally {
        }
    }

    public static void viewEvent(String token, AssetViewEventDataRequest viewEventRequest) {
        String start = EVENT_DATE_FORMAT.format(viewEventRequest.getStart());
        String end = EVENT_DATE_FORMAT.format(viewEventRequest.getEnd());

        String url = String.format(ASSET_VIEW_EVENT_DATA_ENDPOINT, LUMADA_HOST, viewEventRequest.getAssetId(), start, end);
        if (DEBUG) {
            System.out.println(url);
        }

        OkHttpClient client = createHttpClient();

        Request request = buildRequest(url, token);

        String body;
        Response response;
        try {
            response = client.newCall(request).execute();
            body = response.body().string();
            if (DEBUG) {
                System.out.println(body);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            AssetViewEventDataResponse event = gson.fromJson(body, AssetViewEventDataResponse.class);

            if (DEBUG) {
                System.out.println(gson.toJson(event));
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        } finally {
        }
    }

    public static String getAssetAccessToken(String token, String assetId) {
        OkHttpClient client = createHttpClient();

        String url = String.format(ASSET_GET_ACCESS_TOKEN_ENDPOINT, LUMADA_HOST, assetId);
        if (DEBUG) {
            System.out.println(url);
        }

        Request request = buildRequest(url, token);

        String body;
        Response response;
        try {
            response = client.newCall(request).execute();
            body = response.body().string();
            if (DEBUG) {
                System.out.println(body);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            AssetGetAccessTokenResponse accessTokenResp = gson.fromJson(body, AssetGetAccessTokenResponse.class);

            if (DEBUG) {
                System.out.println(gson.toJson(accessTokenResp));
            }

            return accessTokenResp.getToken();
        } catch (IOException ioe) {
            System.err.println(ioe);
        } finally {
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
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

        String assetId = "9d23824d-5ac1-48e9-8b97-cad607938a8f";
        LumadaRestClient.viewAsset(token, assetId);

        AssetViewEventDataRequest request = new AssetViewEventDataRequest();
        //Query event for 1 day
        Date end = new Date();
        Date start = new Date(end.getTime() - 1000 * 60 * 60 * 24);
        request.setEnd(end);
        request.setStart(start);
        request.setAssetId(assetId);
        viewEvent(token, request);

        String assetAccessToken = getAssetAccessToken(token, assetId);
        System.out.println("Asset access token: " + assetAccessToken);
    }

    private static class LumadaTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            X509Certificate[] x509Certificates = new X509Certificate[0];
            return x509Certificates;
        }

        public void checkServerTrusted(final X509Certificate[] chain,
                                       final String authType) throws CertificateException {
        }

        public void checkClientTrusted(final X509Certificate[] chain,
                                       final String authType) throws CertificateException {
        }
    }
}