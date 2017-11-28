package org.pentaho.tiger.lumada;

public class AssetViewEventDataResponse {
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
}
