package couchdb;

import com.couchbase.lite.*;
import com.couchbase.lite.replicator.Replication;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//DO NOT MODIFY THE CouchbaseSingleton.java file WHILE LEARNING HOW TO USE THE DATABASE!

public class CouchbaseSingleton {


    private Manager manager;
    private Database database;
    private Replication pushReplication;
    private Replication pullReplication;

    private static CouchbaseSingleton instance = null;

    private CouchbaseSingleton()
    {
        try
        {
            this.manager = new Manager(new JavaContext("data"), Manager.DEFAULT_OPTIONS);
            this.database = this.manager.getDatabase("reservationsystemdb");

            View todoView = database.getView("rooms");


            todoView.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    emitter.emit(document.get("_id"), document);

                    // Map photosObj = document.get("photos");
                }
            }, "1");

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }






    public static CouchbaseSingleton getInstance()
    {
        System.out.println("GETTING INSTANCE");
        if(instance == null) {
            System.out.println("CREATING NEW COUCHBASE INSTANCE");
            instance = new CouchbaseSingleton();
        }
        return instance;
    }

    public Database getDatabase() {
        return this.database;
    }

    public void startReplication(URL gateway, boolean continuous) {
        this.pushReplication = this.database.createPushReplication(gateway);
        this.pullReplication = this.database.createPullReplication(gateway);
        this.pushReplication.setContinuous(continuous);
        this.pullReplication.setContinuous(continuous);
        this.pushReplication.start();
        this.pullReplication.start();
    }

    public void stopReplication() {
        this.pushReplication.stop();
        this.pullReplication.stop();
    }


    public void test() {
        System.out.println("This is a test");

    }


}
