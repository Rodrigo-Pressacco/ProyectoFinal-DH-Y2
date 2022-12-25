package com.dhmoney.transactions.utils;

import java.text.SimpleDateFormat;

public class JsonResponseError {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String timestamp;
    String path;
    Integer status;
    String error;

    public JsonResponseError() {
    }

    public JsonResponseError(String path, Integer status, String error)
    {
        this.timestamp = sdf.format(System.currentTimeMillis());
        this.path = path;
        this.status = status;
        this.error = error;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public String getPath()
    {
        return path;
    }

    public Integer getStatus()
    {
        return status;
    }

    public String getError()
    {
        return error;
    }

    @Override
    public String toString() {
        return "JsonResponseError{" +
                "timestamp=" + timestamp +
                ", path=" + path +
                ", status=" + status +
                ", error='" + error + '\'' +
                '}';
    }
}
