package com.enyteam.abc.smartkitchen.Storage;

import java.io.Serializable;

/**
 * Created by abc on 16-Jan-17.
 */

public class OrderPojo implements Serializable{
    public String itemName;
    public Double itemQty;

    public OrderPojo(String n, Double q) {
        itemName = n;
        itemQty = q;
    }
}
