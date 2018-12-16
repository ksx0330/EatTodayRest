package com.ltaeng.Domain.rest.KeywordStoreList;

import com.ltaeng.Domain.Store;
import com.ltaeng.Domain.StoreEnhanced;

import java.util.List;

public class Document {
    Store place;
    StoreEnhanced recommend = null;
    List<Store> nearStore = null;

    public Store getPlace() {
        return place;
    }

    public void setPlace(Store place) {
        this.place = place;
    }

    public StoreEnhanced getRecommend() {
        return recommend;
    }

    public void setRecommend(StoreEnhanced recommend) {
        this.recommend = recommend;
    }

    public List<Store> getNearStore() {
        return nearStore;
    }

    public void setNearStore(List<Store> nearStore) {
        this.nearStore = nearStore;
    }
}
