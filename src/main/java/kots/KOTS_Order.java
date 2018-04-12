package kots;

import couchdb.RestaurantItem;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class KOTS_Order implements Serializable
{
    public final String employeeID;
    public final double total;
    public final String creationTime;
    public final String orderID;
    public final List<RestaurantItem> itemsList;
    public boolean isStopOrder = false;
    public boolean isStopOrderResolved = true;

    public KOTS_Order(String employeeID,String orderId, List<RestaurantItem> itemsOrderedList)
    {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now(); ;
        this.employeeID = employeeID;
        this.total = getOrderTotal(itemsOrderedList);
        this.orderID = orderId;
        this.itemsList = itemsOrderedList;
        this.creationTime = dtf.format(now);
    }


    private double getOrderTotal(List<RestaurantItem> forItems)
    {
        double  total = 0.0;
        if(forItems != null)
        {

            for(RestaurantItem item : forItems)
            {
                total += item.price;
            }
        }

        return total;
    }
}
