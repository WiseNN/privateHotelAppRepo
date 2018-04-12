package kots

import couchdb.DB
import couchdb.DBNames
import dataProcessing.Encryption3
import devutil.ConsoleColors
import javafx.collections.ObservableList
import tornadofx.*

class KOTS_EmployeeManager
{
    enum class kotsUserType  {
         CLIENT, SYSTEM
}

    fun createKotsUsersDB()
    {
        val db = DB()
        db.createDoc(DBNames.kotsClientUsers, HashMap<String, Any>())
        db.createDoc(DBNames.kotsSystemUsers, HashMap<String, Any>())
    }

    fun deleteKotsUsersDB()
    {
        val db = DB()
        db.permenantlyRemoveDoc(DBNames.kotsClientUsers)
        db.permenantlyRemoveDoc(DBNames.kotsSystemUsers)
    }


    fun createEmployeesDB()
    {
        val db = DB()
        db.createDoc(DBNames.systemEmployees, HashMap<String, Any>())
    }

    fun createKotsUser(userType : kotsUserType, username : String, password : String) : ObservableList<String>?
    {


        val db = DB()
        //get correct DBName based on userType
        val dbName = if(userType == kotsUserType.CLIENT) DBNames.kotsClientUsers else DBNames.kotsSystemUsers
        //get clientDB or systemDB for kots users
        val kotsDBMap = db.readDocInDB(dbName)
        val separator = "OR"

        //if username exists, return false for duplicate username
        if(kotsDBMap.containsKey(username))
        {
            //username exists
            return null
        }

        val result = Encryption3().encryptValue("encrypt", password)


        //set username equal to encrypted password
        kotsDBMap.put(username, "${result[0]}$separator${result[1]}")
        val updatedUsersMap = db.updateDocInDB(dbName,kotsDBMap)

        //return updated clientUsernamesList for table
        return  updatedUsersMap.keys.toList().observable()
    }


    fun deleteKotsUser(userType: kotsUserType, username : String) : ObservableList<String>?
    {

        val db = DB()
        //get correct DBName based on userType
        val dbName = if(userType == kotsUserType.CLIENT) DBNames.kotsClientUsers else DBNames.kotsSystemUsers
        val kotsDBMap = db.readDocInDB(dbName)


        //if username exists, return false for duplicate username
        if(kotsDBMap.containsKey(username))
        {
            //username exists, delete
            kotsDBMap.remove(username)
            val updatedUsersMap = db.updateDocInDB(dbName,kotsDBMap)

            //return updated clientUsernamesList for table
            return updatedUsersMap.keys.toList().observable()
        }
        else
        {
            //username does not exist
            return null
        }
    }


    fun changePasswordForUser(userType: kotsUserType,username: String, password: String) : ObservableList<String>
    {

        val db = DB()
        //get correct DBName based on userType
        val dbName = if(userType == kotsUserType.CLIENT) DBNames.kotsClientUsers else DBNames.kotsSystemUsers
        val kotsDBMap = db.readDocInDB(dbName)
        val separator = "OR"


        val result = Encryption3().encryptValue("encrypt", password)


        //set username equal to encrypted password
        kotsDBMap.put(username, result[0]+separator+result[1])
        val updatedUsersMap = db.updateDocInDB(dbName,kotsDBMap)

        //update clientUsernamesList for table
        return updatedUsersMap.keys.toList().observable()

    }
    fun getKOTS_User(userType: kotsUserType,username: String) : String?
    {

        val db = DB()
        //get correct DBName based on userType
        val dbName = if(userType == kotsUserType.CLIENT) DBNames.kotsClientUsers else DBNames.kotsSystemUsers
        val kotsDBMap = db.readDocInDB(dbName)
        if(kotsDBMap.contains(username))
        {
            return kotsDBMap[username] as String
        }else{
            return null
        }
    }

    fun signIntoKOTS(userType: kotsUserType, username: String, password: String) : List<Boolean>
    {
        val db = DB()
        //get correct DBName based on userType
        val dbName = if(userType == kotsUserType.CLIENT) DBNames.kotsClientUsers else DBNames.kotsSystemUsers
        val kotsDBMap = db.readDocInDB(dbName)

        //elm 1 -> true //pass false//failed login
        //elm2 -> true//user exist failed password false//user doesnt exist
        val returnValList = mutableListOf<Boolean>()

        if(!kotsDBMap.containsKey(username))
        {
            returnValList.add(false)
            returnValList.add(false)
            return returnValList
        }



        val passwordWithDecryptKey = kotsDBMap[username] as String

        val resultList = passwordWithDecryptKey.split("OR")

        if(password == Encryption3().decryptValue("decrypt", resultList[0], resultList[1]))
        {
            returnValList.add(true)

        }else{
            returnValList.add(false)
            returnValList.add(true)
        }


        return returnValList
    }

    fun readClientUsersAsList() : List<String>
    {
        val db = DB()
        val kotsDBMap = db.readDocInDB(DBNames.kotsClientUsers)

        return kotsDBMap.keys.toList()

    }

    fun readSystemUsersAsList() : List<String>
    {
        val db = DB()
        val kotsDBMap = db.readDocInDB(DBNames.kotsSystemUsers)

        return kotsDBMap.keys.toList()

    }



}