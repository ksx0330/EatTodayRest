package com.ltaeng.Domain;

public class StoreNormal  extends Store {
    private String flag;
    private String picture;

    public StoreNormal() { }

    public StoreNormal(Store store) {
        setId(store.getId());
        setDaumId(store.getDaumId());
        setName(store.getName());
        setAddress(store.getAddress());
        setPhone(store.getPhone());
        setX(store.getX());
        setY(store.getY());
        setRate(store.getRate());
        flag = "";
        picture = "";
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + getId() +
                ", daumId=" + getDaumId() +
                ", name='" + getName() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", x='" + getX() + '\'' +
                ", y='" + getY() + '\'' +
                ", flag='" + flag + '\'' +
                ", picture='" + picture + '\'' +
                ", rate=" + getRate() +
                '}';
    }
}