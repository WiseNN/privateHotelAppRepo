package hotelbackend;

import couchdb.DB;
import couchdb.DBNames;
import couchdb.Room;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;



public class HotelBackEndNorris
{
    public ArrayList<Room>  bookRoomNorris(Date fromDate, Date toDate, Room.allRoomTypes roomType, int numOfRooms)
    {

        //create the reservation
        Reservation roomReservation = new Reservation(fromDate, toDate, Reservation.serviceTypes.hotelRoom, "94843345");

        //reserve room
        Room roomClass = new Room();
        ArrayList<Room> listOfRooms = roomClass.reserveRoom(numOfRooms, roomType, roomReservation);

        //return arrayList of rooms
        return listOfRooms;
    }
}

