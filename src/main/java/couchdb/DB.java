package couchdb;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.util.Log;
import com.sun.javaws.exceptions.InvalidArgumentException;
import com.github.underscore.*;
import org.json.JSONObject;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

import javax.json.Json;
import javax.print.Doc;
import devutil.ConsoleColors;
import java.util.*;

public class DB
{

   private Database allDB = CouchbaseSingleton.getInstance().getDatabase();


    //This function creates documents that do not exist
    //It takes in the document name, and a new hashMap (if one exists).
    //This will return null if you try to create a document with an ID that already exists.
    Map<String, Object> createDoc(String withDocName, Map<String, Object> andNewMap) throws IllegalArgumentException
    {
        Document newDoc = null;

        if((withDocName == "")||(withDocName == null)||(andNewMap == null))
        {
            //create formatted params string to display in console
            String params = "--- Arguments ---\n\twithDocName: "+withDocName+"\n\tupdatedHashMap: "+andNewMap;

            throw new IllegalArgumentException("Cannot pass a null or empty string value to this function\nCheck Params:\n\n"+params);
        }
        else
        {
            Document docInDB = allDB.getExistingDocument(withDocName);

            if(docInDB == null)
            {
                try{
                    newDoc = allDB.getDocument(withDocName);
                    newDoc.putProperties(andNewMap);
                }
                catch (CouchbaseLiteException e) {
                    Log.e(Log.TAG, "Cannot create document **FROM DB CLASS** "+e);
                }

                Document updatedDoc = allDB.getExistingDocument(withDocName);


                Map<String, Object> mutableCopy = new HashMap<String, Object>();

                mutableCopy.putAll(updatedDoc.getProperties());
                return mutableCopy;

            }
            else{

                System.out.println(ConsoleColors.yellowText("A CouchBase Document with the name: "+withDocName+" already exists\nreturning null"));
                return null;
            }
        }

    }





    //This function updates an existing document in the database with the new Map.
    //It takes in the EXISTING document name, and updated Map, then returns the updated document to the caller.
    //This update function will throw if a couchbase error is incurred.
    //Update conflicts will resolve themselves, because the update function is in-use, instead of
    //putProperties.
    Map<String, Object> updateDocInDB(String withDocName, Map<String,Object> updatedMap) throws IllegalArgumentException
    {
        Document updatedDoc = null;
        if((withDocName == "")||(withDocName == null)||(updatedMap == null))
        {
            //create formatted params string to display in console
            String params = "--- Arguments ---\n\twithDocName: "+withDocName+"\n\tupdatedMap: "+updatedMap;

            throw new IllegalArgumentException("Cannot pass a null or empty string value to this function\nCheck Params:\n\n"+params);
        }
        else{
            Document currentDoc = allDB.getDocument(withDocName);

            try{
                currentDoc.update((newRevision) -> {
                    newRevision.setUserProperties(updatedMap);
                    return true;
                });
            }
            catch (CouchbaseLiteException e)
            {
                Log.e(Log.TAG, "Cannot save document **FROM DB CLASS**", e);
            }
            updatedDoc = allDB.getExistingDocument(withDocName);
        }

        Map<String, Object> mutableCopy = new HashMap<String, Object>();

        mutableCopy.putAll(updatedDoc.getProperties());
        return mutableCopy;
    }





    //This function looks for a key value pair in a document, and returns the pair to the caller
    //It takes in the name of an existing document in the database, the key name, and object value you are looking for
    // and returns the key value pair of Type: Entry<K,V>. If the document does not exist, or the
    //key value pair does not exist in the document, this function will return null and
    //print in yellow, if the document was not found, or the key value pair was not found
    //PLEASE NOTE:
    //- You can only search for top level objects in the HashMap (Yes it means what you think it means Wiley)
    //- However, you can pass in full objects as values ex.) a HashMap within a hashMap
    // *** if this throws, please report this to the creator ***

    Map.Entry<String, Object> searchForKeyValueInDoc(String withDocName, String key, Object value) throws IllegalArgumentException
    {
        Map.Entry<String, Object> keyValuePairInDoc = null;


        if((withDocName == "")||(withDocName == null)||(key == "")||(value == null))
        {
            //create formatted params string to display in console
            String params = "--- Arguments ---\n\twithDocName: "+withDocName+"\n\tkey: "+key+"\n\tvalue: "+value;

            throw new IllegalArgumentException("Cannot pass a null or empty string value to this function\nCheck Params:\n\n"+params);
        }
        else
        {

            Document currentDoc = allDB.getExistingDocument(withDocName);

            if(currentDoc != null)
            {

                Map<String, Object> docHashMap = currentDoc.getProperties();

                keyValuePairInDoc = docHashMap.entrySet().stream().filter((entry) -> {

                    if(( key.equals(entry.getKey())) && (value.equals(entry.getValue()))) return true;

                    return false;

                }).findFirst().orElse(null);

                if(keyValuePairInDoc == null) System.out.println(ConsoleColors.cyanText("Object does not exist for key: "+key+" in Couchbase document with name: "+withDocName));

            }
            else
            {
                System.out.println(ConsoleColors.yellowText("Document with the name: "+withDocName+" does not exist"));
            }
        }

        return keyValuePairInDoc;
    }



    //This function will print the JSON object to the console in green.
    //It takes in a Map<K,V> object (can pass a HashMap<K,V>) and an indent factor.
    //If a null value is passed in the docMap parameter, this function will throw an exception.
    void printDocAsJSON(Map<String, Object> docMap, int indentFactor)
    {

        if((docMap == null))
        {
            //create formatted params string to display in console
            String params = "--- Arguments ---\n\tdocMap: "+docMap;

            throw new IllegalArgumentException("Cannot pass a null or empty string value to this function\nCheck Params:\n\n"+params);
        }else{
            JSONObject dbAsJSON = new JSONObject(docMap);
            System.out.println(ConsoleColors.greenText(dbAsJSON.toString(indentFactor)));
        }


    }

    //This function permanently removes a document from the database, and returns the removed document to the caller.
    //It takes in a document name. This function can throw if it passes null or an empty string for
    //the withDocName parameter, or incurs a database error.
    Map<String, Object> permenantlyRemoveDoc(String withDocName)
    {
        Document removedDoc = null;
        if((withDocName == "")||(withDocName == null))
        {
            //create formatted params string to display in console
            String params = "--- Arguments ---\n\twithDocName: "+withDocName;

            throw new IllegalArgumentException("Cannot pass a null or empty string value to this function\nCheck Params:\n\n"+params);
        }
        else{

            Document currentDoc = allDB.getExistingDocument(withDocName);
            removedDoc = currentDoc ;
            if(currentDoc == null)
            {
                System.out.println(ConsoleColors.yellowText("A CouchBase Document with the name: "+withDocName+" already exists\nreturning null"));



                Map<String, Object> mutableCopy = new HashMap<String, Object>();

                mutableCopy.putAll(removedDoc.getProperties());
                return mutableCopy;
            }
            else
            {
                try {
                    currentDoc.purge();
                }
                catch (CouchbaseLiteException e)
                {
                    Log.e(Log.TAG, "Cannot save document **FROM DB CLASS**", e);
                }
            }


        }

        return  null;
    }



    //This function looks for a document based on the provided name passed into the
    //param, if no argument is specified this function will throw. If successfully found,
    //a document will return the Map<K,V> form of the document in the database
    Map<String, Object> readDocInDB(String withDocName) {
        Document removedDoc = null;
        if ((withDocName == "") || (withDocName == null)) {
            //create formatted params string to display in console
            String params = "--- Arguments ---\n\twithDocName: " + withDocName;

            throw new IllegalArgumentException("Cannot pass a null or empty string value to this function\nCheck Params:\n\n" + params);
        } else {

            Document currentDoc = allDB.getExistingDocument(withDocName);

            if (currentDoc == null) {
                System.out.println(ConsoleColors.yellowText("A CouchBase Document with the name: " + withDocName + " already exists\nreturning null"));
                return removedDoc.getProperties();
            }
            else
            {
                Map<String, Object> mutableCopy = new HashMap<String, Object>();

                mutableCopy.putAll(currentDoc.getProperties());
                return mutableCopy;
            }


        }

    }
}
