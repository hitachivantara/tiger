package org.pentaho.tiger.lumada;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.pentaho.tiger.lumada.response.LumadaRestResponse;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class LumadaRestHelper {
    public static boolean DEBUG = false;

    private static LumadaTrustManager trustManager = new LumadaTrustManager();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static OkHttpClient createHttpClient() {
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

    public static Request buildRequest(String url, String token, String body, String method) {
        Request.Builder builder = new Request.Builder();
        builder = builder.url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token);

        if ("POST".equals(method) && body != null) {
            builder = builder.post(RequestBody.create(JSON, body));
        }

        return builder.build();
    }

    public static LumadaRestResponse execute(String token, String url, Class clazz, String requestBody, String method) {
        OkHttpClient client = createHttpClient();
        Request request = buildRequest(url, token, requestBody, method);

        String body;
        Response response;
        try {
            response = client.newCall(request).execute();
            body = response.body().string();
            if (DEBUG) {
                System.out.println(body);
            }

            return (LumadaRestResponse) gson.fromJson(body, clazz);
        } catch (IOException ioe) {
            System.err.println(ioe);
        } finally {
        }

        return null;
    }

    public static LumadaRestResponse execute(String token, String url, Class clazz) {
        return execute(token, url, clazz, null, null);
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
