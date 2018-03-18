package couchdb;

import devutil.ConsoleColors;
import devutil.MyUtil;
import hotelbackend.Reservation;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.omg.CORBA.Any;

import javax.rmi.CORBA.Util;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Room implements Serializable
{


    public int roomNumber;
    public transient SimpleStringProperty roomNumberProperty = new SimpleStringProperty(this, "roomNumber");;

    public allRoomTypes roomType;
    public transient SimpleObjectProperty<allRoomTypes> roomTypeProperty = new SimpleObjectProperty<>(this, "roomType");;

    public allBedTypes bedType;
    public transient SimpleObjectProperty<allBedTypes> bedTypeProperty = new SimpleObjectProperty<>(this, "bedType");;

    public Boolean isSmoking;
    public transient SimpleBooleanProperty isSmokingProperty = new SimpleBooleanProperty(this, "isSmoking");;

    public Boolean hasPet;
    public transient SimpleBooleanProperty hasPetProperty = new SimpleBooleanProperty(this, "hasPet");;

    public Boolean isAvailableForOccupancy;
    public transient SimpleBooleanProperty isAvailableForOccupancyProperty = new SimpleBooleanProperty(this, "isAvailableForOccupancy");;

    public String notes = "";
    public transient SimpleStringProperty notesProperty = new SimpleStringProperty(this, "notes");;

    public Map<String, Object> amenities;
    public transient SimpleObjectProperty<Map<String, Object>> amenitiesProperty = new SimpleObjectProperty<Map<String, Object>>(this, "amenities");

    public String additionalPackages = getAdditionalPackages();
    public transient SimpleStringProperty additionalPackagesProperty = new SimpleStringProperty(this, "additionalPackages");;

    public String stayDuration;
    public transient SimpleStringProperty stayDurationProperty = new SimpleStringProperty(this, "stayDuration");


    public ArrayList<String> reservations;


    public  final HashMap<String, Object> regAmenities = getRegAmenities();
    public  final HashMap<String, Object> suiteAmenities = getSuiteAmenities();

    //HashMap keys
    private final String bedTypeKey = "bedType";
    private final String roomTypeKey = "roomType";
    private final String isSmokingKey = "isSmoking";
    private final String petKey = "hasPet";
    private final String amenitiesKey = "amenities";
    private final String isAvailabeKey = "isAvailableForOccupancy";
    private final String roomNumberKey = "roomNumber";



    //constructor - call to create a room
    public Room(int roomNumber, allRoomTypes roomType, allBedTypes bedType, Boolean isSmoking,
                Boolean hasPet, Boolean isAvailableForOccupancy, Map<String, Object> amenities, String additionalPackages)
    {
        this.roomNumber = roomNumber;
        this.roomNumberProperty = new SimpleStringProperty(""+roomNumber);

        this.roomType = roomType;
        this.roomTypeProperty = new SimpleObjectProperty<allRoomTypes>(roomType);



        this.bedType = bedType;
        this.bedTypeProperty = new SimpleObjectProperty<>(bedType);

        this.isSmoking = isSmoking;
        this.isSmokingProperty = new SimpleBooleanProperty(isSmoking);

        this.hasPet = hasPet;
        this.hasPetProperty = new SimpleBooleanProperty(hasPet);

        this.isAvailableForOccupancy = isAvailableForOccupancy;
        this.isAvailableForOccupancyProperty  = new SimpleBooleanProperty(isAvailableForOccupancy);

        this.amenities = amenities;
        this.amenitiesProperty = new SimpleObjectProperty<Map<String, Object>>(amenities);

        this.notes = "";
        this.notesProperty = new SimpleStringProperty(notes);


        this.reservations = new ArrayList<String>();



        //set additional packages
        DB db = new DB();
        Map<String, Object> roomPkgsObj = db.readDocInDB(DBNames.additionalRoomPackages);












    }

    //constructor - call to use Room class's Helper functions (utility)
    public Room()
    {
        this.roomNumber = 000;
        this.roomType = null;
        this.bedType = null;
        this.isSmoking = false;
        this.hasPet = false;
        this.isAvailableForOccupancy = true;
        this.amenities = null;
        this.reservations = null;

    }









    //REPLACING WITH BOOLEAN VALUES
//    public static enum isSmoking {
//        yes, no
//    }
//
//    public static enum hasPet {
//        yes, no
//    }
//
//    public static enum isAvailable {
//        yes, no
//    }





    private final HashMap<String, Object> getRegAmenities()
    {

        HashMap<String, Object> regRoomAmenities = new HashMap<String, Object>();

        //create reg amenities object with boolean values to change if room as amenity available
        regRoomAmenities.put(allAmenities.fridge.toString(), true);
        regRoomAmenities.put(allAmenities.microwave.toString(), true);
        regRoomAmenities.put(allAmenities.couch.toString(), true);
        regRoomAmenities.put(allAmenities.telephone.toString(), true);
        regRoomAmenities.put(allAmenities.iron.toString(), true);
        regRoomAmenities.put(allAmenities.safe.toString(), true);

        return regRoomAmenities;
    }

    private HashMap<String, Object> getSuiteAmenities()
    {
        HashMap<String, Object> suiteRoomAmenities = getRegAmenities();

        suiteRoomAmenities.put(allAmenities.cofferMaker.toString(), true);
        suiteRoomAmenities.put(allAmenities.stereo.toString(), true);
        suiteRoomAmenities.put(allAmenities.miniBar.toString(), true);

        return suiteRoomAmenities;
    }

    private String getAdditionalPackages()
    {
        //additionalPackages String (to be parsed for viewing in the tableView)
        String addPkgsString = "Spa=false;Casino=false;Meal Ticket=false;Free Valet=false";

        return  addPkgsString;
    }







    //creating sample rooms map, cannot access created rooms directly, only through database: DBNames.rooms
        public void createRooms()
        {

            DB db = new DB();



            //if the rooms database exists, do not create one
            if(db.readDocInDB(DBNames.rooms) != null)
            {
                return;
            }

            //create an object of multiple rooms
            Map<String, Object> roomsMap = new HashMap<String, Object>(); //track all rooms of HashMap (Object)

            //create one room
            Room oneRoom = null;

            //use for loop to generate all rooms
            for(Integer i=1;i<201;i++)
            {
                //create new room HashMap
                HashMap<String, Object> roomObj = new HashMap<>();

                //if i is even, make isEven true, else make false
                int isEven = ((200 % i) == 0) ? 1 : 0;

                //if isEven is 1, create different room, than if isEven is 0
                switch(isEven)
                {
                    //set even even rooms to different properties than odd rooms
                    case 1:
                         oneRoom = new Room(i, allRoomTypes.reg,allBedTypes.king, false, false, true, regAmenities, additionalPackages);

                        break;

                    //set odd rooms to different properties than even rooms
                    case 0:

                        oneRoom = new Room(i, allRoomTypes.suite,allBedTypes.queen, false, false, true, suiteAmenities, additionalPackages);




                        break;

                }

                MyUtil util = new MyUtil();
                String serializedRoom = null;

                try{
                    serializedRoom = util.serializeObject(oneRoom);
                }
                catch(Exception e)
                {
                    System.out.println(e.getLocalizedMessage());
                }

                System.out.println("serialized: "+serializedRoom.toString());

                //i is room number, one room is Room object (cast to Object)
                roomsMap.put(i.toString(), serializedRoom);
            }


                //create rooms database
                db.createDoc(DBNames.rooms, roomsMap);




        }





    //filter the reservationList for the corresponding hotel serviceType, and return the filtered list
    private Map<String, Object> getRoomsMapForRoomType(allRoomTypes currentRoomType)
    {
        DB dbRef = new DB();
        Map<String, Object> roomsMap = dbRef.readDocInDB(DBNames.rooms);

        MyUtil util = new MyUtil();
        Room copyRoom = null;

        Map<String, Object> roomsListWithinRoomType = roomsMap.entrySet().stream().filter((entry -> {

            Room oneRoom = null;

            try{
                oneRoom = util.deserializeObject(Room.class, (String) entry.getValue());
            }catch(Exception e)
            {
                System.out.println(e.getLocalizedMessage());
            }


            if(oneRoom != null && oneRoom.roomType == currentRoomType)
            {
                entry.setValue(oneRoom);
                return true;
            }
            else
                return false;

        })).collect(Collectors.toMap((result) -> result.getKey(), (result) -> result.getValue()));

        return roomsListWithinRoomType;

    }




    //reserves a room, and returns the arrayList<Room> of reserved Rooms
    public ArrayList<Room> reserveRoom(int roomCount, allRoomTypes roomType, Reservation pendingReservation)
    {
        DB db = new DB();
        MyUtil util = new MyUtil();
        //get rooms map from the database
        Map<String, Object> roomsMap = getRoomsMapForRoomType(roomType);

        //create ArrayList to store retrieved rooms
        ArrayList<Room> reservedRoomsList = new ArrayList<>();


        //loop through the roomCount & reserve # of rooms that needs to be
        for(int i=0;i<roomCount;i++)
        {


            Map.Entry<String, Object> roomObj = roomsMap.entrySet().stream().filter((roomEntry) -> {

                System.out.println(ConsoleColors.cyanText("Watching Reservation process..."));
                System.out.println(ConsoleColors.cyanText("is Room: " + roomEntry.getKey() + " available?"));


                //create Room object of Room class from Entry value
                Room room = (Room) roomEntry.getValue();

                if (isBookingAvailable(room, pendingReservation))
                {
                    //get the current room's list of reservationID's
                    ArrayList<String> roomReservationsList = room.reservations;

                    //add the reservationID to the current rooms list of reservationID's
                    roomReservationsList.add(pendingReservation.reservationID);

                    //print that this room is available
                    System.out.println(ConsoleColors.cyanText("\n\nYes!"));

                    //re-attach the updated list of reservationID's to the room
                    room.reservations = roomReservationsList;

                    //update the current stay's duration (room duration property)
                    room.stayDuration = pendingReservation.fromDate.toString()+";"+pendingReservation.toDate.toString();

                    //put the updated room back in the roomsMap the database
                    Map<String, Object> roomsMapFromDB = db.readDocInDB(DBNames.rooms);
                    //serialize
                    String serializedRoom = null;
                    try{
                        serializedRoom = util.serializeObject(room);

                        if(roomsMapFromDB != null)
                        {
                            roomsMapFromDB.replace(roomEntry.getKey(), serializedRoom);

                            //save the updated roomsMap to the database
                            db.updateDocInDB(DBNames.rooms, roomsMapFromDB);

                            //add pendingReservation to the reservationsMap in the database
                            Reservation reservation = new Reservation();
                            reservation.addToReservationsMapInDB(pendingReservation);
                        }else{
                            System.out.println(ConsoleColors.yellowText("roomsMapFromDB is null check reserveRoom() in Class: Room"));
                        }
                    }catch (Exception e)
                    {
                        System.out.println(e.getLocalizedMessage());
                    }





                    //return true, as room has been found and is available for booking
                    return true;
                } else {
                    //print that this room is not available
                    System.out.println(ConsoleColors.cyanText("\n\nNo"));
                    return false;
                }


            }).findFirst().orElse(null);

            reservedRoomsList.add((Room) roomObj.getValue());
        }


    return reservedRoomsList;
    }





    public Boolean isBookingAvailable(Room forRoom,Reservation pendingReservation)
    {
        //get reservationID
        // search reservations Dictionary for this reservationID
        // get reservation
        // get dates from & to dates from reservation
        //check if current from booking date is inBetween current reservation from & to date
        //check if current to booking date is inBetween current reservation from & to date
        //if yes, this room is unavailable, (lets go to the next room in the list of rooms), return false
        //if no, this room is available
        // create a reservation with the current booking information
        // get the reservationID from the reservation
        //add a "reservations" key to this room
        //create a new String ArrayList, and add there reservationID to the list
        // then make the arrayist of reservations as the value of the key for "reservations"
        // then put the room back into the rooms hashMap, with the reservation added
        //then return true from the function



        //get list of reservationIDs from room (Room Class Object)
        ArrayList<String> reservationsIDList = forRoom.reservations;

        //if there is no reservationsID list, then there are no reservaitons, return true
        if(reservationsIDList.size() == 0)
        {
            return true;
        }

        // get reservationMap for given serviceType from database (hotelRoom, Spa, event) HARD_CODED!
        Map<String, Object> reservationsMapForServiceTypeFromDB = pendingReservation.getReservationMapForServiceTypeFromDB(pendingReservation.serviceType);



        //search through the list of reservationID's,
        //if the booking dates are not in between the reservation dates for this current
        //  room, return true, else return false
        Boolean isRoomAvailable =  reservationsIDList.stream().anyMatch((String reservationID) -> {



            //get the reservationFromDBMap (with specific service type) from the reservations map
            Reservation reservationFromDBMap = (Reservation) reservationsMapForServiceTypeFromDB.get(reservationID);

            //if reservationFromDBMap is null, return false and print msg in console...function needs debugging
            if(reservationFromDBMap != null)
            {
                //if bookingFrom date is in between the reservationFromDBMap from & to date OR
                //if bookingTo date is in between the reservationFromDBMap from & to date
                //room is unavailable, else return true (Available)
                if( (pendingReservation.fromDate.after(reservationFromDBMap.fromDate) && pendingReservation.fromDate.before(reservationFromDBMap.toDate)) ||
                        (pendingReservation.toDate.after(reservationFromDBMap.fromDate) && pendingReservation.toDate.before(reservationFromDBMap.toDate)))
                {
                    return false;
                }
                else if( (pendingReservation.fromDate.equals(reservationFromDBMap.fromDate) || pendingReservation.toDate.equals(reservationFromDBMap.toDate)) ||
                        (pendingReservation.fromDate.equals(reservationFromDBMap.toDate) ))
                {
                    return false;
                }
                else
                {

                    return true;
                }
            }
            else{
                System.out.println(ConsoleColors.yellowText("RESERVATION IS INVALID...THIS IS A PROBLEM, PLEASE DEBUG isAvailable function in Class: Reservation"));
                return false;
            }
        });

        return isRoomAvailable;
    }


    //call to cancel the roomReservation, function returns the cancelled reservation
    //  and prints if the reservation is not attached to the current room
    public Reservation cancelReservation(Room forRoom, Reservation reservation)
    {

        ArrayList<String> roomReservationsList =  forRoom.reservations;

        if(roomReservationsList.contains(reservation.reservationID))
        {
            roomReservationsList.remove(reservation.reservationID);
            forRoom.reservations = roomReservationsList;
        }
        else
            {
            System.out.println(ConsoleColors.yellowText("Reservation with ID:"+reservation.reservationID+" is not attatched to this room!"));
        }

        return reservation;
    }

























    //returns a printable  Map<String, Object>, given the standard map saved to the database - to console and able to use through application if preferred
    Map<String, Object>convertToPrintableMap()
    {
        DB db = new DB();
        //get rooms map from the database
        Map<String, Object> roomsMap = db.readDocInDB(DBNames.rooms);

        //create printable rooms map that will be returned
        Map<String, Object> printableRoomsMap = new HashMap<String,Object>();

        roomsMap.entrySet().forEach((entry -> {

            //create oneRoom from entry value
            Room oneRoom = (Room) entry.getValue();

            //create prinatbleRoomMap
            Map<String, Object> prinatbleRoomMap = new HashMap<String, Object>();

            //create mapping
            prinatbleRoomMap.put(bedTypeKey, oneRoom.bedType.toString());
            prinatbleRoomMap.put(isAvailabeKey, oneRoom.isAvailableForOccupancy);
            prinatbleRoomMap.put(petKey, oneRoom.hasPet);
            prinatbleRoomMap.put(isSmokingKey, oneRoom.isSmoking);
            prinatbleRoomMap.put(roomTypeKey, oneRoom.roomType.toString());
            prinatbleRoomMap.put(roomNumberKey, oneRoom.roomNumber);
            prinatbleRoomMap.put(amenitiesKey, oneRoom.amenities);

            //put printable room into printable rooms map
            printableRoomsMap.put(oneRoom.roomNumber +"", prinatbleRoomMap);


        }));

        System.out.println(ConsoleColors.redText("DO NOT SAVE THIS PRINTABLE MAP TO THE DATABASE!!!"));
        return  printableRoomsMap;

    }

    //enums
    public enum allRoomTypes {
        suite, reg, handi
    }

    public enum allBedTypes {
        king, queen, twin
    }

    public enum allAmenities {
        fridge, microwave, couch, telephone, iron, safe, cofferMaker, stereo, miniBar

    }

}


