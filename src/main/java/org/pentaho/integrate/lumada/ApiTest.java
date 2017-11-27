package org.pentaho.integrate.lumada;

import com.google.gson.JsonObject;
import com.hds.lumada.client.api.AssetClient;
import com.hds.lumada.client.config.*;
import com.hds.lumada.client.implementation.AssetClientImpl;

public class ApiTest {
    public static void main(String[] args) throws Exception {
        String entityId = "9d23824d-5ac1-48e9-8b97-cad607938a8f";
        String entityValue = "qgUOIkpyJuRDon0MLMXWfNE3k0FWWcnX";
        publishState(entityId, entityValue);
    }

    public static void publishState(String entityId, String entityValue) throws Exception {
        //Creating Credentials
        final EntityCredentials credentials = new EntityCredentials(entityId, entityValue);
        //Creating Lumada End Point
        final LumadaEndpoint endpoint = new LumadaEndpoint("localhost");
        //Creating Asset Config
        final AssetClientConfig config = new AssetClientConfig(
                credentials,
                AssetCommunicationProtocol.MQTT,
                LumadaMessagePayloadFormat.JSON,
                endpoint,
                entityId);
        AssetClient client = new AssetClientImpl(config);

        final JsonObject payload = new JsonObject();                                                    //Creating payload object
        payload.addProperty("temp", 45);
        payload.addProperty("weight", 33);
        payload.addProperty("speed", "2");
        client.publishState(payload);
    }
}
