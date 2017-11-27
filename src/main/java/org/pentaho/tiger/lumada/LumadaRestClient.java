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
import java.util.List;

public class LumadaRestClient {
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
                System.out.println("Hostname: " + String.valueOf(hostname));
                //if (hostname.equals(HOST)) {
                return true;
                //}
                //return false;
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

    public static void main(String[] args) throws Exception {
        String username = "YOUR_ADMIN";
        String password = "YOUR_PASSWORD";
        LoginResponse loginResponse = LumadaRestClient.login("https://localhost/v1/security/oauth/token", username, password);

        if (loginResponse != null) {
            String token = loginResponse.getAccessToken();
            System.out.println("token:" + token);

            String assetId = "9d23824d-5ac1-48e9-8b97-cad607938a8f";
            LumadaRestClient.viewAsset("https://localhost/v1/asset-management/assets", token, assetId);
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
