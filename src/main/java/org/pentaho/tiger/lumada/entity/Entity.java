package org.pentaho.tiger.lumada.entity;

public class Entity {
    private String entityId;
    private String state;
    private String entityType;
    private String entityValidationType;
    private String entityRole;
    private String[] entityPrivileges;
    private String entityValue;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityValidationType() {
        return entityValidationType;
    }

    public void setEntityValidationType(String entityValidationType) {
        this.entityValidationType = entityValidationType;
    }

    public String getEntityValue() {
        return entityValue;
    }

    public void setEntityValue(String entityValue) {
        this.entityValue = entityValue;
    }

    public String getEntityRole() {
        return entityRole;
    }

    public void setEntityRole(String entityRole) {
        this.entityRole = entityRole;
    }

    public String[] getEntityPrivileges() {
        return entityPrivileges;
    }

    public void setEntityPrivileges(String[] entityPrivileges) {
        this.entityPrivileges = entityPrivileges;
    }
}