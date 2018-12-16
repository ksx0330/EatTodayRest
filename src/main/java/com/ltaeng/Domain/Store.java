package com.ltaeng.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Store {
    private int id;
    @JsonIgnore
    private int daumId;
    private String name;
    private String address;
    private String phone;
    private String x;
    private String y;
    private double rate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDaumId() {
        return daumId;
    }

    public void setDaumId(int daumId) {
        this.daumId = daumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
