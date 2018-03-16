package couchdb;

import java.util.HashMap;

public class Room {

    static String roomNumber;
    static String Key;


    public Room()
    {

    }




    //HashMap keys
    static final String bedTypeKey = "bedType";
    static final String roomTypeKey = "roomType";
    static final String isSmokingKey = "isSmoking";
    static final String petKey = "hasPet";
    static final String amenitiesKey = "amenities";

    public static enum typeKey {
        bedType, roomType, isSmoking, hasPet, amenities;
    }


    public static enum roomType {
        suite, reg, handi
    }

    public static enum bedType {
        king, queen, twin
    }

    public static enum isSmoking {
        yes, no
    }

    public static enum hasPet {
        yes, no
    }


    static final HashMap<String, Boolean> regAmenities = getRegAmenities();
    static final HashMap<String, Boolean> suiteAmenities = getSuiteAmenities();


    private static final HashMap<String, Boolean> getRegAmenities() {

        HashMap<String, Boolean> regRoomAmenities = new HashMap<String, Boolean>();

        //create reg amenities object with boolean values to change if room as amenity available
        regRoomAmenities.put(amenities.fridge.toString(), true);
        regRoomAmenities.put(amenities.microwave.toString(), true);
        regRoomAmenities.put(amenities.couch.toString(), true);
        regRoomAmenities.put(amenities.telephone.toString(), true);
        regRoomAmenities.put(amenities.iron.toString(), true);
        regRoomAmenities.put(amenities.safe.toString(), true);

        return regRoomAmenities;
    }

    private static HashMap<String, Boolean> getSuiteAmenities()
    {
        HashMap<String, Boolean> suiteRoomAmenities = Room.getRegAmenities();

        suiteRoomAmenities.put(amenities.cofferMaker.toString(), true);
        suiteRoomAmenities.put(amenities.stereo.toString(), true);
        suiteRoomAmenities.put(amenities.miniBar.toString(), true);

        return suiteRoomAmenities;
    }

    public static enum amenities {
        fridge, microwave, couch, telephone, iron, safe, cofferMaker, stereo, miniBar

    }

    public static String getRoomNumValue() {
        return roomNumber;
    }

    public static void setRoomNumValue(String roomNum) {
        roomNumber = roomNum;
    }


   /* public void getRoomTypeKey() {

    if(){
        //update the value for hasPet, change to use (using enum)
        oneRoom.put(Room.petKey.toString(), Room.hasPet.yes.toString());
    }
}*/



        public HashMap<String, Object> getRooms()
        {
            //create an object of multiple rooms
            HashMap<String, Object> roomsObj = new HashMap<>(); //track all rooms of HashMap (Object)

            //use for loop to generate all rooms
            for(Integer i=1;i<201;i++)
            {

                //create new room HashMap
                HashMap<String, Object> roomObj = new HashMap<>();

                //if i is even, make isEven true, else make false
                int isEven = ((i % 201) == 0) ? 1 : 0;

                //if isEven is 1, create different room, than if isEven is 0
                switch(isEven)
                {
                    //set even even rooms to different properties than odd rooms
                    case 1:


                        roomObj.put(Room.roomTypeKey,Room.roomType.handi.toString());
                        roomObj.put(Room.bedTypeKey,Room.bedType.queen.toString());
                        roomObj.put(Room.petKey, Room.hasPet.no.toString());
                        roomObj.put(Room.isSmokingKey,Room.isSmoking.no.toString());
                        roomObj.put(Room.amenitiesKey, Room.suiteAmenities);

                        break;

                    //set odd rooms to different properties than even rooms
                    case 0:

                        roomObj.put(Room.roomTypeKey,Room.roomType.reg.toString());
                        roomObj.put(Room.bedTypeKey,Room.bedType.king.toString());
                        roomObj.put(Room.petKey, Room.hasPet.no.toString());
                        roomObj.put(Room.isSmokingKey,Room.isSmoking.yes.toString());
                        roomObj.put(Room.amenitiesKey, Room.regAmenities);


                        break;

                }

                //i is room number, roomObj is room, embedded object
                roomsObj.put(i.toString(), roomObj);

            }
            return roomsObj;
        }


}


