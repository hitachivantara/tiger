package org.pentaho.tiger.lumada;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;

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
    private static String LUMADA_HOST = "localhost";
    private static String ASSET_VIEW_EVENT_DATA_ENDPOINT = "https://%s/v1/asset-data/assets/%s/events?startTime=%s&endTime=%s";
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
            e.printStackTrace();
        }

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        OkHttpClient client = okHttpClientBuilder.build();
        return client;
    }


    public static LoginResponse login(String url, String username, String password) {
        OkHttpClient client = createHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("grant_type", "password")
                .addFormDataPart("username", username)
                .addFormDataPart("password", password)
                .addFormDataPart("scope", "all")
                .addFormDataPart("client_id", "lumada-ui")
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
            System.out.println(body);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            LoginResponse loginResponse = gson.fromJson(body, LoginResponse.class);

            /*
            List<AssetProperty> properties = asset.getPriperties();
            if (properties != null) {
                for (AssetProperty ap : properties) {
                    System.out.println(asset.getName() + "=" + ap.getValue());
                }
            }
            */


            System.out.println(gson.toJson(loginResponse));

            return loginResponse;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
        }

        return null;
    }

    public static void viewAsset(String url, String token, String assetId) {
        OkHttpClient client = createHttpClient();

        Request request = new Request.Builder()
                .url(url + "/" + assetId)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        String body;
        Response response;
        try {
            response = client.newCall(request).execute();
            body = response.body().string();
            System.out.println(body);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Asset asset = gson.fromJson(body, Asset.class);

            List<AssetProperty> properties = asset.getPriperties();
            if (properties != null) {
                for (AssetProperty ap : properties) {
                    System.out.println(asset.getName() + "=" + ap.getValue());
                }
            }


            System.out.println(gson.toJson(asset));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
        }
    }

    public static void viewEvent(String token, AssetViewEventDataRequest viewEventRequest) {
        String start = EVENT_DATE_FORMAT.format(viewEventRequest.getStart());
        String end = EVENT_DATE_FORMAT.format(viewEventRequest.getEnd());

        String endpoint = String.format(ASSET_VIEW_EVENT_DATA_ENDPOINT, LUMADA_HOST, viewEventRequest.getAssetId(), start, end);
        System.out.println(endpoint);

        OkHttpClient client = createHttpClient();

        Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        String body;
        Response response;
        try {
            response = client.newCall(request).execute();
            body = response.body().string();
            System.out.println("body: " + body);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            AssetViewEventDataResponse event = gson.fromJson(body, AssetViewEventDataResponse.class);

            System.out.println(gson.toJson(event));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
        }
    }

    public static void main(String[] args) throws Exception {
        String username = "admin";
        String password = "YOUR_PASSWORD";//YOUR_PASSWORD

        System.out.println("Connecting to Lumada");
        LoginResponse loginResponse = LumadaRestClient.login(String.format("https://%s/v1/security/oauth/token", LUMADA_HOST), username, password);

        if (loginResponse != null) {
            String token = loginResponse.getAccessToken();
            System.out.println("access token:" + token);

            String assetId = "9d23824d-5ac1-48e9-8b97-cad607938a8f";
            LumadaRestClient.viewAsset(String.format("https://%s/v1/asset-management/assets", LUMADA_HOST), token, assetId);

            AssetViewEventDataRequest request = new AssetViewEventDataRequest();
            //Query event for 1 day
            Date end = new Date();
            Date start = new Date(end.getTime() - 1000 * 60 * 60 * 24);
            request.setEnd(end);
            request.setStart(start);
            request.setAssetId(assetId);
            viewEvent(token, request);
        }
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