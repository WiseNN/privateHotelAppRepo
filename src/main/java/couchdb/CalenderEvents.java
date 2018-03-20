package couchdb;

import devutil.ConsoleColors;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.Map;

public class CalenderEvents
{

    public void addEvent(Date forDay, URL eventURL)
    {
        DB db = new DB();

        Map<String, Object> eventsMap =  db.readDocInDB(DBNames.hotelCalenderEvents);
        eventsMap.put(forDay.toString(), eventURL);

        db.updateDocInDB(DBNames.hotelCalenderEvents, eventsMap);

    }


    public void removeEvent(Date forDay)
    {
        DB db = new DB();
        Map<String, Object> eventsMap =  db.readDocInDB(DBNames.hotelCalenderEvents);
        eventsMap.remove(forDay);

        db.updateDocInDB(DBNames.hotelCalenderEvents, eventsMap);

    }

    public URL getEvent(Date forDay)
    {
        DB db = new DB();

        Map<String, Object> eventsMap =  db.readDocInDB(DBNames.hotelCalenderEvents);

        Object urlObject = eventsMap.get(forDay.toString());


        if(urlObject == null)
        {
            System.out.println(ConsoleColors.yellowText("URL for date:"+forDay+" does not exist. Check getEvent() in Class: CalenderEvent"));
        }else{
            URL url = (URL)urlObject;
            return url;
        }

        return null;

    }

}
