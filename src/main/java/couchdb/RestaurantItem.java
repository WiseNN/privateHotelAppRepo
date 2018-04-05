package couchdb;

import devutil.ConsoleColors;
import devutil.MyUtil;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

public class RestaurantItem implements Serializable
{
    public String name;
    public double price;

//    ArrayList<RestaurantItem> items = new ArrayList<>();

    RestaurantItem(String name, double price)
    {
        this.name = name;
        this.price = price;

    }
    public RestaurantItem()
    {
        this.name = null;
        this.price = -1;
    }




    public void addItem(String inCategory, String itemName, double itemPrice)
    {
        DB db = new DB();

        Map<String, Object> menuMap = db.readDocInDB(DBNames.restaurantMenu);
        String serializedCategoryMenu = (String)menuMap.get(inCategory);

        try{
            Object itemsObj = new MyUtil().deserializeObject(RestaurantItem.class, serializedCategoryMenu);
            ArrayList<RestaurantItem> itemsAry = (ArrayList<RestaurantItem>) itemsObj;
            itemsAry.add(new RestaurantItem(itemName,itemPrice));

            String updatedSerializedMenu = new MyUtil().serializeObject(itemsAry);

            menuMap.replace(inCategory, updatedSerializedMenu);

            db.updateDocInDB(DBNames.restaurantMenu, menuMap);
            System.out.println(ConsoleColors.greenText("Menu Item: "+itemName+" Added!"));
        }
        catch (Exception e)
        {
            System.out.println(ConsoleColors.yellowText(e.getMessage()));
        }


    }




    public void updateItem(String inCategory, String itemName, String updatedName, int updatedPrice)
    {
        DB db = new DB();

        Map<String, Object> menuMap = db.readDocInDB(DBNames.restaurantMenu);
        String serializedCategoryMenu = (String)menuMap.get(inCategory);
        try{
            Object itemsObj = new MyUtil().deserializeObject(RestaurantItem.class, serializedCategoryMenu);
            ArrayList<RestaurantItem> itemsAry = (ArrayList<RestaurantItem>) itemsObj;
            int itemsAryLength = itemsAry.size();
            Boolean found = false;

            for(int i=0;i<itemsAryLength;i++)
            {
                RestaurantItem menuItem = itemsAry.get(i);


                if (menuItem.name.equals(itemName))
                {
                    found = true;
                    menuItem.name = updatedName;
                    menuItem.price = updatedPrice;
                    itemsAry.remove(i);
                    itemsAry.add(i,menuItem);
                }
            }
            if(!found)
            {
                System.out.println(ConsoleColors.yellowText("Item: "+itemName+" does not exist in the Menu"));
                return;
            }


            String updatedSerializedMenu = new MyUtil().serializeObject(itemsAry);

            menuMap.replace("menu", updatedSerializedMenu);

            db.updateDocInDB(DBNames.restaurantMenu, menuMap);
        }
        catch (Exception e)
        {
            System.out.println(ConsoleColors.yellowText(e.getMessage()));
        }

        System.out.println(ConsoleColors.greenText("Menu Item: "+itemName+" Removed!"));
    }





    public void removeItem(String inCategory, String itemName)
    {
        DB db = new DB();

        Map<String, Object> menuMap = db.readDocInDB(DBNames.restaurantMenu);
        String serializedCategoryMenu = (String)menuMap.get(inCategory);
        try{
            Object itemsObj = new MyUtil().deserializeObject(RestaurantItem.class, serializedCategoryMenu);
            ArrayList<RestaurantItem> itemsAry = (ArrayList<RestaurantItem>) itemsObj;

            RestaurantItem menuItem = itemsAry.stream().filter(item -> {

                if(item.name.equals(itemName)) { return true; }
                else{ return false;}

            }).findFirst().orElse(null);

            if(menuItem == null)
            {
                System.out.println(ConsoleColors.yellowText("Item: "+itemName+" does not exist in the Menu"));
                return;
            }
            itemsAry.remove(menuItem);

            String updatedSerializedMenu = new MyUtil().serializeObject(itemsAry);

            menuMap.replace("menu", updatedSerializedMenu);

            db.updateDocInDB(DBNames.restaurantMenu, menuMap);
        }
        catch (Exception e)
        {
            System.out.println(ConsoleColors.yellowText(e.getMessage()));
        }

        System.out.println(ConsoleColors.greenText("Menu Item: "+itemName+" Removed!"));
    }

    public void addCategory(String categoryName)
    {
        DB db = new DB();
        MyUtil myUtil = new MyUtil();

        Map<String, Object> menuMap = db.readDocInDB(DBNames.restaurantMenu);
        try{
            String serializedNewArrayList = myUtil.serializeObject(new ArrayList<RestaurantItem>());
            menuMap.put(categoryName, serializedNewArrayList);

            db.updateDocInDB(DBNames.restaurantMenu, menuMap);

            System.out.println(ConsoleColors.greenText("Menu Category: "+categoryName+" Added!"));

        }catch (Exception e)
        {
            System.out.println(ConsoleColors.yellowText(e.getMessage()));
        }

    }

    public void removeCategory(String categoryName)
    {
        DB db = new DB();

        Map<String, Object> menuMap = db.readDocInDB(DBNames.restaurantMenu);
        menuMap.remove(categoryName);

        db.updateDocInDB(DBNames.restaurantMenu, menuMap);

        System.out.println(ConsoleColors.greenText("Menu Category: "+categoryName+" Removed!"));
    }

    public void updateCategoryName(String oldName, String newName)
    {
        DB db = new DB();

        Map<String, Object> menuMap = db.readDocInDB(DBNames.restaurantMenu);

        Object objectVal = menuMap.get(oldName);

        menuMap.put(newName, objectVal);

        menuMap.remove(oldName);


        db.updateDocInDB(DBNames.restaurantMenu, menuMap);

        System.out.println(ConsoleColors.greenText("Category Name Updated from: "+oldName+" to: "+newName));

    }

    public Map<String, Object> getDeserializedMenu(Map<String, Object> serializedMap)
    {
        Map<String, Object> usableMap = new HashMap<>();
        MyUtil myUtil = new MyUtil();

        serializedMap.entrySet().stream().forEach((entry) -> {

            try{

                Object itemsList = myUtil.deserializeObject(RestaurantItem.class, (String)entry.getValue());

                usableMap.put(entry.getKey(), itemsList);

            }
            catch (Exception e)
            {
                System.out.println(ConsoleColors.yellowText(e.getMessage()));
            }

        });


        return usableMap;
    }





    public void createMenu()
    {
        DB db = new DB();
        Map<String, Object> newMenu = new HashMap<>();

        db.createDoc(DBNames.restaurantMenu, newMenu);

    }








    public enum modifyMenu {
        add,remove,update
    }




}
