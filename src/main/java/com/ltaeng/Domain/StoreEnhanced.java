package com.ltaeng.Domain;

import java.util.List;

public class StoreEnhanced extends StoreNormal {
    List<StoreMenu> menu;
    List<StoreImage> image;

    public List<StoreMenu> getMenu() {
        return menu;
    }

    public void setMenu(List<StoreMenu> menu) {
        this.menu = menu;
    }

    public List<StoreImage> getImage() {
        return image;
    }

    public void setImage(List<StoreImage> image) {
        this.image = image;
    }
}
