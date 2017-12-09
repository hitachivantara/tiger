package org.pentaho.tiger.lumada.request;

import java.util.Date;

public class AssetViewEventDataRequest implements LumadaRestRequest {
    private String assetId;
    private Date start;
    private Date end;

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
