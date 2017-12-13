package org.pentaho.tiger.lumada;

import org.pentaho.tiger.lumada.entity.AssetProperty;
import org.pentaho.tiger.lumada.request.AssetNewRequest;
import org.pentaho.tiger.lumada.request.AssetTypeNewRequest;
import org.pentaho.tiger.lumada.request.LoginRequest;
import org.pentaho.tiger.lumada.response.LoginResponse;

import java.io.File;
import java.util.*;

/*
Command line arguments

    --debug     output debug message
    --host      host of the Lumada server
    --username
    --password
    --clean     clean the data previously created

    Example:
    java -cp .:./lib/* org.pentaho.tiger.lumada.ApiTest --debug --host 10.0.2.15 --username YOUR_USERNAME --password YOUR_PASSWORD

    Make sure all required jars are in "lib" directory:
        gson-2.8.2.jar
        okhttp-3.9.1.jar
*/

public class ApiTest {
    private static String USER_HOME = System.getenv("HOME");

    private static String LUMADA_HOST = null;
    private static String LUMADA_USER = null;
    private static String LUMADA_PASS = null;

    private static boolean CLEAN = false;

    public static void main(String[] args) {
        System.out.println("User home: " + USER_HOME);

        if (USER_HOME == null) {
            Map<String, String> envMap  = System.getenv();
            for (String key : envMap.keySet()) {
                System.out.println(key + "=" + envMap.get(key));
            }
        }

        try {
            if (args.length != 0) {
                for (int i = 0; i < args.length; i++) {
                    if ("--debug".equals(args[i])) {
                        LumadaRestClient.DEBUG = true;
                    } else if ("--host".endsWith(args[i])) {
                        LUMADA_HOST = args[++i];
                    } else if ("--username".endsWith(args[i])) {
                        LUMADA_USER = args[++i];
                    } else if ("--password".endsWith(args[i])) {
                        LUMADA_PASS = args[++i];
                    } else if ("--clean".endsWith(args[i])) {
                        CLEAN = true;
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Invalid arguments");
            System.err.println(ex);
        }

        if (LUMADA_HOST == null || LUMADA_USER == null || LUMADA_PASS == null) {
            System.out.println("Need Lumada hostname/ip, username and password");
            System.out.println("EX: java -cp .:./lib/* org.pentaho.tiger.lumada.ApiTest --host 10.0.2.15 --username YOUR_USERNAME --password YOUR_PASSWORD");
            return;
        }

        System.out.println("Connecting to Lumada");

        LumadaRestClient client = new LumadaRestClient(LUMADA_HOST);

        //Login and obtain token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(LUMADA_USER);
        loginRequest.setPassword(LUMADA_PASS);
        LoginResponse loginResponse = client.login(loginRequest);

        if (loginResponse == null) {
            System.out.println("Could not login.");
            return;
        }

        String token = loginResponse.getAccessToken();
        System.out.println("access token:" + token);

        if (token == null) {
            System.out.println("Could not obtain valid token.");
            return;
        }

        client.setToken(token);
        if (!CLEAN) {
            create(client);
        }
    }

    /**
     * Upload picture
     * Create new asset avatar type
     * Create new asset
     * List event
     * View asset access token
     *
     * @param client
     */
    public static void create(LumadaRestClient client) {
        createTypeAndAsset(client, "ToyotaElectricForklift", new File(USER_HOME, "ToyotaElectricForklift.png"));

        createTypeAndAsset(client, "ToyotaElectricStacker", new File(USER_HOME, "ToyotaElectricStacker.png"));

        /*
        //List asset's event
        System.out.println("----------------------------------------");
        AssetViewEventDataRequest request = new AssetViewEventDataRequest();
        //Query event for 30 day
        Date end = new Date();
        Date start = new Date(end.getTime() - 1000l * 60 * 60 * 24 * 30);
        request.setEnd(end);
        request.setStart(start);
        request.setAssetId(assetId);
        client.viewEvent(request);
        */
    }

    private static void createTypeAndAsset(LumadaRestClient client, String assetName, File pictureFile) {
        long namePrefix = new Date().getTime();

        //upload image file
        System.out.println("----------------------------------------");
        String pictureId = null;
        if (pictureFile.exists()) {
            pictureId = client.uploadFile(pictureFile);
            System.out.println("new picture id: " + pictureId);
        } else {
            System.out.println("Could not find the picture file: " + pictureFile.getAbsolutePath());
        }

        //Create new asset avatar type
        System.out.println("----------------------------------------");
        AssetTypeNewRequest assetTypeNewRequest = new AssetTypeNewRequest();
        assetTypeNewRequest.setName(assetName + "-" + namePrefix);
        if (pictureId != null) {
            assetTypeNewRequest.setPictureId(pictureId);
        }
        String newTypeId = client.addAssetType(assetTypeNewRequest);
        System.out.println("New Type Id: " + newTypeId);

        //Create new asset avatar
        if (newTypeId == null) {
            System.out.println("Failed to create new asset avatar type");
        } else {
            System.out.println("----------------------------------------");
            AssetNewRequest assetNewRequest = new AssetNewRequest();
            assetNewRequest.setName(assetName + "-" + namePrefix);
            assetNewRequest.setAssetTypeId(newTypeId);
            //Create properties for this new asset avatar
            List<AssetProperty> properties = new ArrayList<AssetProperty>();
            properties.add(new AssetProperty("Location", "Bellevue"));
            properties.add(new AssetProperty("Speed", "5"));
            properties.add(new AssetProperty("Battery", "30"));
            properties.add(new AssetProperty("WorkingHour", "5678"));
            assetNewRequest.setPriperties(properties);
            String newId = client.addAsset(assetNewRequest);
            System.out.println("New Id: " + newId);

            //View an asset
            System.out.println("----------------------------------------");
            String assetId = newId;
            client.viewAsset(assetId);


            //Obtian the asset access token
            System.out.println("----------------------------------------");
            String assetAccessToken = client.getAssetAccessToken(assetId);
            System.out.println("Asset access token: " + assetAccessToken);
        }
    }
}
