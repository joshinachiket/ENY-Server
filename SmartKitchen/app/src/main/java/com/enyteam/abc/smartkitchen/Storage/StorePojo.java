package com.enyteam.abc.smartkitchen.Storage;

/**
 * Created by abc on 16-Jan-17.
 */

public class StorePojo {
    int storeId;
    String storeName;
    long storePhone;
    String storeAdd;

    public StorePojo(int storeId, String storeName, String storeAdd,long storePhone) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storePhone = storePhone;
        this.storeAdd = storeAdd;
    }
}
