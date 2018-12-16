package com.ltaeng.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StoreImage {
    @JsonIgnore
    private int id;
    @JsonIgnore
    private int storeId;
    private String path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
