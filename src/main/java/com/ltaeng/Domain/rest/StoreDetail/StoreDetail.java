package com.ltaeng.Domain.rest.StoreDetail;

import com.ltaeng.Domain.StoreEnhanced;
import com.ltaeng.Domain.rest.Keyword;

import java.util.List;

public class StoreDetail {
    Keyword keyword;
    List<StoreEnhanced> detail = null;

    public Keyword getKeyword() {
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    public List<StoreEnhanced> getDetail() {
        return detail;
    }

    public void setDetail(List<StoreEnhanced> detail) {
        this.detail = detail;
    }
}
