package org.pentaho.tiger.lumada.request;

import org.pentaho.tiger.lumada.entity.AssetProperty;

import java.util.List;

public class AssetNewRequest implements LumadaRestRequest {
    private String name;
    private String assetTypeId;
    private List<AssetProperty> priperties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssetTypeId() {
        return assetTypeId;
    }

    public void setAssetTypeId(String assetTypeId) {
        this.assetTypeId = assetTypeId;
    }

    public List<AssetProperty> getPriperties() {
        return priperties;
    }

    public void setPriperties(List<AssetProperty> priperties) {
        this.priperties = priperties;
    }
}
