package org.pentaho.tiger.lumada;

public class AssetEvent {
    private String name;
    private AssetEventData data;
    private String timestamp;
    private String source;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetEventData getData() {
        return data;
    }

    public void setData(AssetEventData data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
