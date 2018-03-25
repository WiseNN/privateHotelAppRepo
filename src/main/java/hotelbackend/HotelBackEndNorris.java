package hotelbackend;

import couchdb.CalenderEvents;
import couchdb.DB;
import couchdb.DBNames;
import couchdb.Room;
import devutil.ConsoleColors;

import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.net.URL;
import java.util.Map;



public class HotelBackEndNorris
{
    public ArrayList<Room>  bookRoomNorris(Date fromDate, Date toDate, Room.allRoomTypes roomType, int numOfRooms)
    {

        //create the reservation
        Reservation roomReservation = new Reservation(fromDate, toDate,null,null,null, Reservation.serviceTypes.hotelRoom, "94843345");

        //reserve room
        Room roomClass = new Room();
        ArrayList<Room> listOfRooms = roomClass.reserveRoom(numOfRooms, roomType, roomReservation);

        //return arrayList of rooms
        return listOfRooms;
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
}

