package hotelbackend;

import couchdb.DB;
import couchdb.DBNames;
import devutil.ConsoleColors;
import devutil.MyUtil;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

//reservation class for all hotel guests
public class Reservation implements Serializable
{
    public String reservationID;
    public Date fromDate;
    public Date toDate;
    public serviceTypes serviceType;//Hotel, Spa, Restuarant & Bar, Meeting & Events
    public String customerID; // = Customers().uId >> "84847344"
    private Long lowestRervationNumber = new Long(1000);
    private Long highestReservationNumber = new Long(9000);;

    //public members
    public enum serviceTypes {
        hotelRoom, spa, meetingRoom, restuarantBar
    };





    public Reservation(Date fromDate, Date toDate, serviceTypes serviceType, String customerID)
    {


        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        Long reservationID = ThreadLocalRandom.current().nextLong(lowestRervationNumber, highestReservationNumber+1);

        this.reservationID = reservationID+"";  //generate reservationID, cast to string
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.serviceType = serviceType; //room, spa, meeting
        this.customerID = customerID;
    }

    public Reservation()
    {
        this.reservationID = null;
        this.fromDate = null;
        this.toDate = null;
        this.serviceType = null;
        this.customerID = null;
    }










    //filter the reservationList for the corresponding hotel serviceType, and return the filtered list
    public Map<String, Object> getReservationMapForServiceTypeFromDB(serviceTypes serviceType)
    {
        DB dbRef = new DB();
        MyUtil util = new MyUtil();
        Map<String, Object> reservationsMap = dbRef.readDocInDB(DBNames.reservations);

        Map<String, Object> reservationsListWithinServiceType = reservationsMap.entrySet().stream().filter((entry -> {

            Reservation reservation = null;

            if(entry.getKey().equals("_rev")||entry.getKey().equals("_id"))
            {
                return false;
            }

            try{
                reservation = util.deserializeObject(Reservation.class, (String)entry.getValue());
            }catch(Exception e)
            {
                System.out.println(e.getLocalizedMessage());
            }

            if(reservation == null)
            {
                System.out.println(ConsoleColors.yellowText("reservation is null, please see:  getReservationMapForServiceTypeFromDB in Class: Reservation"));
                return false;
            }
            else if( reservation.serviceType == serviceType)
            {
                entry.setValue(reservation);
                return true;
            }


            else
                return false;

        })).collect(Collectors.toMap(result -> result.getKey(), result -> result.getValue()));

    return reservationsListWithinServiceType;

    }

    public void addToReservationsMapInDB(Reservation reservation)
    {
        DB db = new DB();
        MyUtil util = new MyUtil();
        String serializedReservation = null;

        //get reservationsMap from database
        Map<String, Object> reservationsMap = db.readDocInDB(DBNames.reservations);

        if(reservationsMap == null)
        {
            System.out.println(ConsoleColors.yellowText("Cannot add reservation to ReservationsMap In Database, this Map does not exist "));
            return;
        }

        try{
            //serialized reservation object
            serializedReservation = util.serializeObject(reservation);

        }catch(Exception e)
        {
            System.out.println(e.getLocalizedMessage());
        }

        //if reservation is not null, save to reservationsMap in database
        if(serializedReservation != null)
        {
            reservationsMap.put(reservation.reservationID, serializedReservation);
            db.updateDocInDB(DBNames.reservations, reservationsMap);
        }else{
            System.out.println(ConsoleColors.yellowText("Could not save reservation: "+reservation.toString()+" to database"));
        }

    }


}
















