package org.pentaho.tiger.lumada.entity;

public class AssetProperty {
    private String name;
    private String value;

    public AssetProperty() {}

    public AssetProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
