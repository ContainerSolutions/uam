package com.diosoft.uam.db.entry;

import java.util.Date;

public class AuditLogEntry {
    public static final String VERTEX_AUDIT_LOG = "AuditLog";

    public static final String PROPERTY_USER_ID = "user_id";
    public static final String PROPERTY_REQUEST_NUMBER = "request_number";
    public static final String PROPERTY_APPLICATION = "application";
    public static final String PROPERTY_EXECUTOR = "executor";
    public static final String PROPERTY_ACTION = "action";
    public static final String PROPERTY_DATETIME = "datetime";

    private Long requestNumber;
    private String userId;
    private String application;
    private String executor;
    private String action;
    private Date datetime;

    public AuditLogEntry(Long requestNumber, String userId, String application, String executor, String action) {
        this.requestNumber = requestNumber;
        this.userId = userId;
        this.application = application;
        this.executor = executor;
        this.action = action;
        this.datetime = new Date();
    }

    public Long getRequestNumber() {
        return requestNumber;
    }
    public String getUserId() {
        return userId;
    }
    public String getApplication() {
        return application;
    }
    public String getExecutor() {
        return executor;
    }
    public String getAction() {
        return action;
    }
    public Date getDatetime() {
        return datetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditLogEntry that = (AuditLogEntry) o;

        if (requestNumber != null ? !requestNumber.equals(that.requestNumber) : that.requestNumber != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (application != null ? !application.equals(that.application) : that.application != null) return false;
        if (executor != null ? !executor.equals(that.executor) : that.executor != null) return false;
        //if (datetime != null ? !datetime.equals(that.datetime) : that.datetime != null) return false;
        return !(action != null ? !action.equals(that.action) : that.action != null);

    }

    @Override
    public int hashCode() {
        int result = requestNumber != null ? requestNumber.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (application != null ? application.hashCode() : 0);
        result = 31 * result + (executor != null ? executor.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        //result = 31 * result + (datetime != null ? datetime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AuditLogEntry{" +
                "requestNumber=" + requestNumber +
                ", userId='" + userId + '\'' +
                ", application='" + application + '\'' +
                ", executor='" + executor + '\'' +
                ", action='" + action + '\'' +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
