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


    fun createEmployeesDB()
    {
        val db = DB()
        db.createDoc(DBNames.systemEmployees, HashMap<String, Any>())
    }

    fun createKotsUser(userType : kotsUserType, username : String, password : String) : ObservableList<String>?
    {

        val db = DB()
        //get clientDB or systemDB for kots users
        val kotsDBMap = if(userType == kotsUserType.CLIENT) db.readDocInDB(DBNames.kotsClientUsers) else db.readDocInDB(DBNames.kotsSystemUsers)
        val separator = "OR"

        //if username exists, return false for duplicate username
        if(kotsDBMap.containsKey(username))
        {
            //username exists
            return null
        }

        val result = Encryption3().encryptValue("encrypt", password)


        //set username equal to encrypted password
        kotsDBMap.put(username, result[0]+separator+result[1])
        val updatedUsersMap = db.updateDocInDB(DBNames.kotsClientUsers,kotsDBMap)

        //return updated clientUsernamesList for table
        return  updatedUsersMap.keys.toList().observable()
    }


    fun deleteKotsUser(userType: kotsUserType, username : String, password : String) : ObservableList<String>?
    {

        val db = DB()
        val kotsDBMap = if(userType == kotsUserType.CLIENT) db.readDocInDB(DBNames.kotsClientUsers) else db.readDocInDB(DBNames.kotsSystemUsers)


        //if username exists, return false for duplicate username
        if(kotsDBMap.containsKey(username))
        {
            //username exists, delete
            kotsDBMap.remove(username)
            val updatedUsersMap = db.updateDocInDB(DBNames.kotsClientUsers,kotsDBMap)

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
        val kotsDBMap = if(userType == kotsUserType.CLIENT) db.readDocInDB(DBNames.kotsClientUsers) else db.readDocInDB(DBNames.kotsSystemUsers)
        val separator = "OR"


        val result = Encryption3().encryptValue("encrypt", password)


        //set username equal to encrypted password
        kotsDBMap.put(username, result[0]+separator+result[1])
        val updatedUsersMap = db.updateDocInDB(DBNames.kotsClientUsers,kotsDBMap)

        //update clientUsernamesList for table
        return updatedUsersMap.keys.toList().observable()

    }

    fun signIntoKOTS(userType: kotsUserType, username: String, password: String) : List<Boolean>
    {
        val db = DB()
        val kotsDBMap = if(userType == kotsUserType.CLIENT) db.readDocInDB(DBNames.kotsClientUsers) else db.readDocInDB(DBNames.kotsSystemUsers)

        //elm 1 -> true //pass false//failed login
        //elm2 -> true//user exist failed pass false//user doesnt exist
        val returnValList = mutableListOf<Boolean>()

        if(!kotsDBMap.containsKey(username))
        {
            returnValList.add(false)
            returnValList.add(false)
            return returnValList
        }
        else{

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

        System.out.println(ConsoleColors.redText("INTERNAL LOGIN ERROR! CHECK signIn() function in Class: PasswordManager"))
        return returnValList


    }



}