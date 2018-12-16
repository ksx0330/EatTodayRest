package com.ltaeng.Domain.rest.SimpleStoreList;

import com.ltaeng.Domain.Store;
import com.ltaeng.Domain.StoreEnhanced;

import java.util.List;

public class StoreList {
    StoreEnhanced recommend = null;
    List<Store> storeList;

    public StoreEnhanced getRecommend() {
        return recommend;
    }

    public void setRecommend(StoreEnhanced recommend) {
        this.recommend = recommend;
    }

    public List<Store> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<Store> storeList) {
        this.storeList = storeList;
    }
}
