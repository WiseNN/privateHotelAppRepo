package couchdb;

import devutil.ConsoleColors;
import devutil.MyUtil;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RestaurantTable implements Serializable
{
    public int tableNumber = 0;
    public int numberOfSeats = 0;
    private int lowestNumberOfSeats = 1;
    private int highestNumberOfSeats = 10;
    public Reservation activeReservation;
    public ArrayList<String> reservations;


    private RestaurantTable( int tableNumber, int numberOfSeats)
    {
        this.numberOfSeats = numberOfSeats;
        this.reservations = new ArrayList<>();
        this.tableNumber = tableNumber;
        //property is filled when table is getting reserved
        this.activeReservation = null;
    }

    public RestaurantTable()
    {
        this.reservations = null;
        this.activeReservation = null;
    }

    public void createTables()
    {

        DB db = new DB();



        //if the rooms database exists, do not create one
        if(db.readDocInDB(DBNames.restaurantTables) != null)
        {
            return;
        }

        //create an object of multiple rooms
        Map<String, Object> tablesMap = new HashMap<String, Object>(); //track all tables of HashMap (Object)


        //create one table
        RestaurantTable oneTable = null;

        int totalAmount = 70;


        //use for loop to generate all tables
        for(Integer i=1;i<totalAmount+1;i++)
        {
            //create new room HashMap
            HashMap<String, Object> tableObj = new HashMap<>();

            //create one table with random number of numberOfSeats
            int numberOfSeats = ThreadLocalRandom.current().nextInt(lowestNumberOfSeats, highestNumberOfSeats+1);
            oneTable = new RestaurantTable(i,numberOfSeats);

            MyUtil util = new MyUtil();
            String serializedRoom = null;

            try{//serialize one table
                serializedRoom = util.serializeObject(oneTable);
            }
            catch(Exception e)
            {
                System.out.println(e.getLocalizedMessage());
            }

            System.out.println("serialized: "+serializedRoom.toString());

            //i is room number, one room is Room object (cast to Object)
            tablesMap.put(i.toString(), serializedRoom);
        }


        //create rooms database
        db.createDoc(DBNames.restaurantTables, tablesMap);
    }


    //reserves a room, and returns the arrayList<Room> of reserved Rooms
    public ArrayList<RestaurantTable> reserveTable(int partySize, Reservation pendingReservation)
    {
        DB db = new DB();
        MyUtil util = new MyUtil();
        //get rooms map from the database
        Map<String, Object> tablesMap = null;
        Map<String, Object> remainingTablesMap = null;


        //create ArrayList to store retrieved rooms
        ArrayList<RestaurantTable> reservedTablesList = new ArrayList<>();

        int maxSeatsAtTable = 10;
        //party size over max seats available returns the number of tables we need
        int tableCount = partySize / maxSeatsAtTable;
        //remainder will be the number of seats needed left over
        int remainingSeats = partySize % maxSeatsAtTable;
        //flag to track if there are remaining seats
        Boolean isRemainingSeats = false;

        //if quotient is 0, and remainder is not, those are the number of seats
        if(tableCount == 0 && remainingSeats > 0)
        {
            tablesMap = getRestaurantTablesMapForPartySize(remainingSeats);
            tableCount++;
        }
        else if(tableCount > 0 && remainingSeats > 0)
        {
            tablesMap = getRestaurantTablesMapForPartySize(maxSeatsAtTable);
            remainingTablesMap = getRestaurantTablesMapForPartySize(remainingSeats);
            isRemainingSeats = true;
            tableCount++;
        }
        else if(tableCount > 0 && remainingSeats == 0)
        {
            tablesMap = getRestaurantTablesMapForPartySize(maxSeatsAtTable);
        }
        else{
            System.out.println(ConsoleColors.yellowText("Whooa, Error reserving table, check reserveTable() in Class: RestaurantTable"));
        }

        //make table count final, to use in lambda expression
        final int finalTableCount = tableCount;

        //loop through the tablesMap for the given party size, and reserve all available tables
        for(int i=0;i<tableCount;i++)
        {
            Map.Entry<String, Object> tableObj = ((isRemainingSeats) ? remainingTablesMap : tablesMap).entrySet().stream().filter((tableEntry) -> {

                System.out.println(ConsoleColors.cyanText("Watching Reservation process..."));
                System.out.println(ConsoleColors.cyanText("is Table: " + tableEntry.getKey() + " available?"));


                //create Room object of Room class from Entry value
                RestaurantTable table = (RestaurantTable) tableEntry.getValue();


                if (isBookingAvailable(table, pendingReservation))
                {
                    //get the current table's list of reservationID's
                    ArrayList<String> tableReservationsList = table.reservations;

                    //add the reservationID to the current rooms list of reservationID's
                    tableReservationsList.add(pendingReservation.reservationID);

                    //attach the number of tables to pendingReservation
                    pendingReservation.tableCount = finalTableCount;

                    //print that this table is available
                    System.out.println(ConsoleColors.cyanText("\n\nYes!"));

                    //re-attach the updated list of reservationID's to the table
                    table.reservations = tableReservationsList;

                    //update the current stay's duration (table duration property)
//                    table.stayDuration = pendingReservation.fromDate.toString()+";"+pendingReservation.toDate.toString();

                    //attach the activeReservation to thisRoom
                    table.activeReservation = pendingReservation;

                    //put the updated table back in the tablesMap the database
                    Map<String, Object> tablesMapFromDB = db.readDocInDB(DBNames.rooms);
                    //serialize
                    String serializedTable = null;
                    try{
                        serializedTable = util.serializeObject(table);

                        if(tablesMapFromDB != null)
                        {
                            tablesMapFromDB.replace(tableEntry.getKey(), serializedTable);

                            //save the updated tablesMap to the database
                            db.updateDocInDB(DBNames.rooms, tablesMapFromDB);

                            //add pendingReservation to the reservationsMap in the database
                            Reservation reservation = new Reservation();
                            reservation.addToReservationsMapInDB(pendingReservation);
                        }else{
                            System.out.println(ConsoleColors.yellowText("tablesMapFromDB is null check reserveTable() in Class: Room"));
                        }
                    }catch (Exception e)
                    {
                        System.out.println(e.getLocalizedMessage());
                    }

                    //return true, as table has been found and is available for booking
                    return true;
                } else {
                    //print that this table is not available
                    System.out.println(ConsoleColors.cyanText("\n\nNo"));
                    return false;
                }


            }).findFirst().orElse(null);


            isRemainingSeats = false;
            remainingTablesMap = null;

            reservedTablesList.add((RestaurantTable) tableObj.getValue());

        }


        return reservedTablesList;
    }

    //filter the reservationList for the corresponding hotel serviceType, and return the filtered list
    private Map<String, Object> getRestaurantTablesMapForPartySize(int partySize)
    {
        DB dbRef = new DB();
        Map<String, Object> tablesMap = dbRef.readDocInDB(DBNames.restaurantTables);

        MyUtil util = new MyUtil();


        Map<String, Object> tablesListForPartySize = tablesMap.entrySet().stream().filter((entry -> {

            RestaurantTable oneTable = null;


            try{
                oneTable = util.deserializeObject(RestaurantTable.class, (String) entry.getValue());
            }catch(Exception e)
            {
                System.out.println(e.getLocalizedMessage());
            }

            if(oneTable != null && oneTable.numberOfSeats == partySize)
            {
                entry.setValue(oneTable);
                return true;
            }
            else
                return false;

        })).collect(Collectors.toMap((result) -> result.getKey(), (result) -> result.getValue()));

        return tablesListForPartySize;

    }


    public Boolean isBookingAvailable(RestaurantTable forTable,Reservation pendingReservation)
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
        ArrayList<String> reservationsIDList = forTable.reservations;

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

        //returns the first none match on the room, ** >> ! << flips Boolean value (true >> false in a success case)
        Boolean isRoomAvailable =  reservationsIDList.stream().noneMatch((String reservationID) -> {

            //***** CURRENT ISSUE *****
            /*
             * Situation:
             * ----------
             * The reservation alagorithm will check the pending reservation against all reservations in the list, it will
             * return true on the first no conflict that it finds when comparing dates (truthy predicate).
             *Issue:
             * -----
             *  The issue is, whenever we are comparing reservation dates, there might be another reservation that conflicts
             * with the current pending reservation dates. However, we wont find this out because the conditional statement will
             * return true on the first match that it finds to pass the test.
             *
             * Conflict Resolution Proposition:
             * --------------------------------
             *
             * We have to find a way to compare all reservation dates to make sure that there are no conflicts with ANY
             * reservation.....not just the first reservation
             * */

            //get the reservationFromDBMap (with specific service type) from the reservations map
            Reservation reservationFromDBMap = (Reservation) reservationsMapForServiceTypeFromDB.get(reservationID);

            //if reservationFromDBMap is null, return false and print msg in console...function needs debugging
            if(reservationFromDBMap != null)
            {
                //if booking date is on the same day, check to see if the times are allowed
                //if the pending reservation time is +/- 5hrs before/after the reservationIn the system,
                //allow the reservation

                if(pendingReservation.fromDate.equals(reservationFromDBMap.fromDate))
                {

                    //subtract the hrs...if there are atleast 4 hrs in between the times, the reservation can be made
                    int hrs = Math.abs(pendingReservation.time.getHour() - reservationFromDBMap.time.getHour());
                    if(hrs >= 4)
                    {
                        return false;
                    }else{
                        return true;
                    }

                    // --- old algorithm ---
                    //if the times are far enough apart, the table is available (return false)
//                    if( (pendingReservation.time.isAfter( LocalTime.of(reservationFromDBMap.time.getHour()+4,0 )))  ||
//                            (pendingReservation.time.isBefore( LocalTime.of(reservationFromDBMap.time.getHour()-4,0 ))))
//                    {
//                        return false;
//                    }else{// if dates are too close, return true
//                        return true;
//                    }
                }
                else
                { //return false if the dates do not match
                    return false;
                }
            }
            else{
                System.out.println(ConsoleColors.yellowText("RESERVATION IS INVALID...THIS IS A PROBLEM, PLEASE DEBUG isAvailable function in Class: Reservation"));
                return false;
            }
        });

        return isRoomAvailable;
    }


}


