package org.pentaho.tiger.lumada.response;

import org.pentaho.tiger.lumada.entity.AssetEvent;

public class AssetViewEventDataResponse implements LumadaRestResponse {
    private String assetId;
    private AssetEvent[] timeseries;
    private String pagingToken;

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public AssetEvent[] getTimeseries() {
        return timeseries;
    }

    public void setTimeseries(AssetEvent[] timeseries) {
        this.timeseries = timeseries;
    }

    public String getPagingToken() {
        return pagingToken;
    }

    public void setPagingToken(String pagingToken) {
        this.pagingToken = pagingToken;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Asset Id: ").append(assetId);
        if (timeseries != null) {
            buf.append("\n");
            for (AssetEvent e : timeseries) {
                buf.append("\t").append(e).append("\n");
            }
        }
        return buf.toString();
    }
}
