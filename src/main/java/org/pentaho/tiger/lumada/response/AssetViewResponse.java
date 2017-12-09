package org.pentaho.tiger.lumada.response;

import org.pentaho.tiger.lumada.entity.AssetProperty;

import java.util.List;

public class AssetViewResponse implements LumadaRestResponse {
    private String id;
    private int version;
    private String name;
    private String gatewayId;
    private String assetTypeId;
    private List<AssetProperty> properties;
    private long created;
    private long modified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<AssetProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<AssetProperty> properties) {
        this.properties = properties;
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

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Name:").append(this.name).append(", Type Id: ").append(this.assetTypeId);
        if (properties != null) {
            buf.append("\n");
            for (AssetProperty ap : properties) {
                buf.append("\t").append(ap.getName()).append(" = ").append(ap.getValue()).append("\n");
            }
        }

        return buf.toString();
    }
}
