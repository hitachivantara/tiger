package org.pentaho.tiger.lumada.entity;

import java.util.List;

public class Asset extends LumadaEntity {
    private int version;
    private String name;
    private String gatewayId;
    private String assetTypeId;
    private List<AssetProperty> priperties;
    private long created;
    private long modified;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
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

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }
}
