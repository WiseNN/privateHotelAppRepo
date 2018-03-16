package couchdb;


import com.couchbase.lite.*;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import mainhotelapp.SysConstants;
;
import java.net.URL;


import java.util.HashMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import org.json.*;



//DO NOT MODIFY THE CouchbaseSingleton.java file WHILE LEARNING HOW TO USE THE DATABASE!

public class DBTester
{


    public static void main(String[] args)
    {

        DBTester demoTest = new DBTester();

        //please see the runDBClassDemo test to learn how to use the DB.Class
        demoTest.runDBClassDemo();

    }










    void runDBClassDemo()
    {
        DB dbClassRef = new DB();

        HashMap<String, Object> newMap1 = new HashMap<String, Object>();

        for(int i=0; i< 20; i++)
        {
            newMap1.put("key"+i, "value"+i); //puts key, value up to 19
        }


        //creating a document, and returns a Map<K, V>
        Map<String, Object> newDocMap = dbClassRef.createDoc(DBNames.sampleDocName, newMap1);

        //reads a Map (not a document, only send a Map) to the console with an indent factor
        System.out.println("\nprintMap1\n");
        dbClassRef.printDocAsJSON(newDocMap, 3);

        //updates a document, and returns the updated Map
        newMap1.put("key"+20, "value"+20);
        Map<String, Object> updatedMap = dbClassRef.updateDocInDB(DBNames.sampleDocName,newMap1);

        //prints updatedMap to console
        System.out.println("\nprintMap2\n");
        dbClassRef.printDocAsJSON(updatedMap, 6);

        //searches for a keyValue pair in a document
        Map.Entry<String, Object> returnedKeyValuePair = dbClassRef.searchForKeyValueInDoc(DBNames.sampleDocName, "key20", "value20");

        //prints key value pair to the console (wrap this in a map, then print it to the console)
        HashMap newMap = new HashMap<String, Object>();
        newMap.put(returnedKeyValuePair.getKey(), returnedKeyValuePair.getValue());
        System.out.println("\nprintMap3\n");
        dbClassRef.printDocAsJSON(newMap, 2);

        //permenantly removes (purges) a document from the database and returns the deleted Map
        Map<String, Object> removedDoc = dbClassRef.permenantlyRemoveDoc(DBNames.sampleDocName);

        //prints removed doc to console
        System.out.println("\nprintMap4\n");
        dbClassRef.printDocAsJSON(removedDoc, 10);

        //...please see the DB.Class for further documentation.
    }





}

/*
* NOTE:
* - When using getExistingDocument(), it will return an unmodifiable map, (readOnly) unmodifiable Map<K,V >*/