package hotelbackend;

import couchdb.DB;
import couchdb.DBNames;
import devutil.ConsoleColors;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

//reservation class for all hotel guests
public class Reservation {
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
        Map<String, Object> reservationsMap = dbRef.readDocInDB(DBNames.reservations);

        Map<String, Object> reservationsListWithinServiceType = reservationsMap.entrySet().stream().filter((entry -> {

            Reservation reservation = (Reservation) entry.getValue();

            if(reservation.serviceType == serviceType)
                return true;

            else
                return false;

        })).collect(Collectors.toMap(result -> result.getKey(), result -> result.getValue()));

    return reservationsListWithinServiceType;

    }



}
















