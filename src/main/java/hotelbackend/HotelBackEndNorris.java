package hotelbackend;

import couchdb.*;
import devutil.ConsoleColors;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.net.URL;


public class HotelBackEndNorris
{
    public ArrayList<Room>  bookRoomNorris(Date fromDate, Date toDate, Room.allRoomTypes roomType, int numOfRooms)
    {

        //create the reservation
        Reservation roomReservation = new Reservation(fromDate, toDate,null,null,null, Reservation.serviceTypes.hotelRoom, "94843345",null,null,null);

        //reserve room
        Room roomClass = new Room();
        ArrayList<Room> listOfRooms = roomClass.reserveRoom(numOfRooms, roomType, roomReservation);

        //return arrayList of rooms
        return listOfRooms;
    }

    public ArrayList<RestaurantTable>  bookTable(Date fromDate, LocalTime time, Integer partySize, String specialRequests,String name, String emailAddress, String phoneNumber)
    {

        //create the reservation
        Reservation tableReservation = new Reservation(fromDate, null,time,partySize,specialRequests, Reservation.serviceTypes.restuarantBar, "94843345",name,emailAddress, phoneNumber);

        //reserve room
        RestaurantTable tableClass = new RestaurantTable();
        ArrayList<RestaurantTable> listOfTables = tableClass.reserveTable(partySize, tableReservation);

        //return arrayList of rooms
        return listOfTables;
    }


    public void modifyCalenderEvent(int forDay, String withEventUrl, String action)
    {
        CalenderEvents eventDB = new CalenderEvents();

        //logic for processing calender events


        URL url = null;

        Date date = createCalenderDate(forDay);

        System.out.println("calender date: "+date);

        try{
            url = new URL(withEventUrl);

        }catch(Exception e)
        {
            //print malformed URL Exception
            System.out.println(e.getLocalizedMessage());

        }

        switch(action)
        {
            case "add":
                eventDB.addEvent(date,url);
                    break;
            case "remove":
                eventDB.removeEvent(date);
            default:
                System.out.println(ConsoleColors.yellowText("Calender Event has not been processed... See: processCalenderEvent() in Class: HotelBackEnd"));
        }
    }

    public URL getCalenderEventForDay(int forDay)
    {
        Date date = createCalenderDate(forDay);

        return new CalenderEvents().getEvent(date);
    }

    public Date createCalenderDate(int forDay)
    {
        Date da = new Date();
        // ***  using deprecated functions ***
        Date date = new Date(da.getYear(),da.getMonth(),forDay);

        return date;
    }

    public void updatePOSMenu(String action, String category, String item, double price, String updatedName, int updatedPrice)
    {
        RestaurantItem modItems = new RestaurantItem();


        switch(action)
        {
           case "Add Item" :
               modItems.addItem(category, item, price);
                break;
            case "Remove Item" :
                modItems.removeItem(category, item);
                break;
            case "Update Item" :
                modItems.updateItem(category, item, updatedName, updatedPrice);
                break;
            case "Add Category" :
                modItems.addCategory(category);
                break;
            case "Remove Category" :
                modItems.removeCategory(category);
                break;

        }

    }
}

