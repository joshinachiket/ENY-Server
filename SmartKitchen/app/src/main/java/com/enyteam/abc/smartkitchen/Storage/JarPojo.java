package com.enyteam.abc.smartkitchen.Storage;

import java.io.Serializable;

/**
 * Created by abc on 14-Jan-17.
 */

public class JarPojo implements Serializable{
    public String jarId;
    public String content;
    public double maxJarQty;
    public double currentQty;
    public boolean toOrder;

    @Override
    public String toString() {
        return "jarId: "+jarId+"    content:"+content+"   toOrder:"+toOrder;
    }
}
