package com.ltaeng.Domain.rest.KeywordStoreList;

import com.ltaeng.Domain.rest.Keyword;

import java.util.List;

public class StoreList {
    Keyword keyword;
    List<Document> documents = null;

    public Keyword getKeyword() {
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
