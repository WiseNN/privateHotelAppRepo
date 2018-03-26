package couchdb;

import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import devutil.ConsoleColors;
import devutil.MyUtil;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

//reservation class for all hotel guests
public class Reservation implements Serializable
{
    public String reservationID;
    public Date fromDate;
    public Date toDate;
    public LocalTime time;
    public Integer partySize;
    public int tableCount;
    public String specialRequests;
    public serviceTypes serviceType;//Hotel, Spa, RestaurantTable & Bar, Meeting & Events
    public String name;
    public String emailAddress;
    public String phoneNumber;
    public String customerID; // = Customers().uId >> "84847344"
    private Long lowestReservationNumber = new Long(1000);
    private Long highestReservationNumber = new Long(9000);;

    //public members
    public enum serviceTypes {
        hotelRoom, spa, meetingRoom, restuarantBar
    };





    public Reservation(Date fromDate, Date toDate, LocalTime time,Integer partySize,String specialRequests,serviceTypes serviceType, String customerID,String name, String emailAddress, String phoneNumber)
    {
        /*
        * RestuarantID Prefix Key:
        * ------------------------
        * HS -> Hotel Stay
        * RR -> RestaurantTable
        * SP -> Spa
        * */


        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        Long reservationID = ThreadLocalRandom.current().nextLong(lowestReservationNumber, highestReservationNumber+1);

        if(serviceTypes.hotelRoom.equals(serviceType))
        {
            this.reservationID = "HS"+reservationID;  //generate reservationID, cast to string
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.serviceType = serviceType; //room, spa, meeting
            this.customerID = customerID;
        }
        else if(serviceTypes.restuarantBar.equals(serviceType))
        {
            this.reservationID = "RR"+reservationID;  //generate reservationID, cast to string
            this.fromDate = fromDate;
            this.name = name;
            this.time = time; //dinner reservation time from user
            this.partySize = partySize;
            this.specialRequests = specialRequests;
            this.serviceType = serviceType; //room, spa, meeting
            this.customerID = customerID;
            this.emailAddress = emailAddress;
            this.phoneNumber = phoneNumber;
        }
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

    public String printReservation()
    {
        String result = null;
        if(this == null)
        {
            return result;
        }

        if(this.serviceType == serviceTypes.hotelRoom)
        {


            result = "From Date: "+this.fromDate;
            result = "\nTo Date: "+this.toDate;
            result += "\nService Type: "+this.serviceType.toString();
            result += "\nCustomer ID: "+this.customerID;
            result += "\nReservation ID: "+this.reservationID;

        }
        else if(this.serviceType == serviceTypes.restuarantBar)
        {
            result = "Date: "+this.fromDate;
            result += "\nService Type: "+this.serviceType.toString();
            result += "\nParty Size: "+this.partySize;
            result += "\nCustomer ID: "+this.customerID;
            result += "\n Customer Name: "+this.name;
            result += "\nCustomer Phone: "+this.phoneNumber;
            result += "\nReservation ID: "+this.reservationID;
        }


        return result;
    }


}
















