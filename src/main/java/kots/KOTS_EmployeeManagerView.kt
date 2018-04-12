package kots

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javax.swing.text.View
import tornadofx.*
import java.util.*

class KOTS_EmployeeManagerView : tornadofx.View()
{
    val kotsEmployeeManagerAnchorPane : AnchorPane by fxml("/fxml/KOTSEmployeeManager.fxml")
    val usernameTextField : TextField by fxid()
    val passwordTextField : TextField by fxid()
    val userTypeToggleGroup : ToggleGroup by fxid()
    val createUserBtn : Button by fxid()
    val deleteUserBtn : Button by fxid()
    val errorLabel : Label by fxid()
    val clientUsersTableView : TableView<ClientUsers> by fxid()
    val systemUsersTableView : TableView<SystemUsers> by fxid()
    val clientUsersColumn : TableColumn<ClientUsers, String> by fxid()
    val systemUsersColumn : TableColumn<SystemUsers, String> by fxid()



    var clientUsersList = mutableListOf<ClientUsers>().observable()
    var systemUsersList = mutableListOf<SystemUsers>().observable()


    //var to track table focus on delete
    var whichTable = ""


    override val root = kotsEmployeeManagerAnchorPane


    init{



        clientUsersColumn.setCellValueFactory { it.value.nameProperty }
        systemUsersColumn.setCellValueFactory { it.value.nameProperty }


        clientUsersTableView.items = clientUsersList
        systemUsersTableView.items = systemUsersList

        //load initial list for tableViews
        clientUsersList.addAll(getClientList(KOTS_EmployeeManager().readClientUsersAsList().observable()))
        systemUsersList.addAll(getSystemList(KOTS_EmployeeManager().readSystemUsersAsList().observable()))


//        //delete usersDB
//        KOTS_EmployeeManager().deleteKotsUsersDB()
//
//        //create usersDB
//        KOTS_EmployeeManager().createKotsUsersDB()



        //initially bind deleteButton to clientUsersTableView selected item property
        deleteUserBtn.disableProperty().bind(clientUsersTableView.selectionModel.selectedItemProperty().isNull)

        //set selected cell listener and enable delete user button when selected
        clientUsersTableView.selectionModel.selectedItemProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            //if cell is selected here, bind the property to this value
            deleteUserBtn.disableProperty().bind(clientUsersTableView.selectionModel.selectedItemProperty().isNull)
            whichTable = "Client"
        })

        systemUsersTableView.selectionModel.selectedItemProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            //if cell is selected here, bind the property to this value
            deleteUserBtn.disableProperty().bind(systemUsersTableView.selectionModel.selectedItemProperty().isNull)
            whichTable = "System"
        })






        createUserBtn.setOnMouseClicked {
                println("clicked...this button")
            //if username or password is blank, do not process result
            if((usernameTextField.text != "" || usernameTextField.text != null) &&
                    (passwordTextField.text != "" || passwordTextField.text != null))
            {
                val clientUserType : KOTS_EmployeeManager.kotsUserType = KOTS_EmployeeManager.kotsUserType.CLIENT
                val sysUserType : KOTS_EmployeeManager.kotsUserType = KOTS_EmployeeManager.kotsUserType.SYSTEM

                when((userTypeToggleGroup.selectedToggleProperty().get() as RadioButton).text)
                {
                    "client user" -> {

                        val updatedList = KOTS_EmployeeManager().createKotsUser(clientUserType,usernameTextField.text, passwordTextField.text)
                        //isDeleted is not null update the clientUsersList , else, this user does not exist
                        if(updatedList != null)
                        {
                            clientUsersList.clear()
                            clientUsersList.addAll(getClientList(updatedList))
                            errorLabel.text = "Client User Created!"

                        }
                        else{
                            errorLabel.text = "Client User with that username already exist"
                        }
                    }
                    "system user" -> {
                        val updatedList = KOTS_EmployeeManager().createKotsUser(sysUserType,usernameTextField.text, passwordTextField.text)
                        //isDeleted is not null update the clientUsersList , else, this user does not exist
                        if(updatedList != null)
                        {
                            systemUsersList.clear()
                            systemUsersList.addAll(getSystemList(updatedList))
                            errorLabel.text = "System User Created!"
                        }
                        else{
                            errorLabel.text = "System User with that username already exist"
                        }
                    }
                }



            }
            clientUsersTableView.selectionModel.clearSelection()
            systemUsersTableView.selectionModel.clearSelection()
        }






        deleteUserBtn.setOnMouseClicked {

            println("delete")
            if((usernameTextField.text != "" || usernameTextField.text != null) &&
                    (passwordTextField.text != "" || passwordTextField.text != null))
            {
                val clientUserType = KOTS_EmployeeManager.kotsUserType.CLIENT
                val sysUserType  = KOTS_EmployeeManager.kotsUserType.SYSTEM


                when(whichTable)
                {
                    "Client" -> {


                        val username = clientUsersTableView.selectionModel.selectedItem.name
                        val updatedList = KOTS_EmployeeManager().deleteKotsUser(clientUserType,username )
                        //isDeleted is not null update the clientUsersList , else, this user does not exist
                        if(updatedList != null)
                        {
                            clientUsersList.clear()
                            clientUsersList.addAll(getClientList(updatedList))
                            errorLabel.text = "$whichTable User Deleted!"
                            clientUsersTableView.refresh()
                        }
                        else{
                            errorLabel.text = "No User with that username exist"
                        }
                    }
                    "System" -> {
                        val username = systemUsersTableView.selectionModel.selectedItem.name
                        val updatedList = KOTS_EmployeeManager().deleteKotsUser(sysUserType,username)
                        //isDeleted is not null update the clientUsersList , else, this user does not exist
                        if(updatedList != null)
                        {
                            systemUsersList.clear()
                            systemUsersList.addAll(getSystemList(updatedList))
                            errorLabel.text = "$whichTable User Deleted!"
                        }
                        else{
                            errorLabel.text = "No User with that username exist"
                        }
                    }
                }

            }
            clientUsersTableView.selectionModel.clearSelection()
            systemUsersTableView.selectionModel.clearSelection()
        }









    }


    fun getClientList(stringList : ObservableList<String>) : ObservableList<ClientUsers>
    {
        val list = mutableListOf<ClientUsers>().observable()

        stringList.forEach {
            //if not db id or revision add
            if("_rev" == it)
            {}
            else if("_id" == it)
            {}
            else
            {
                list.add(ClientUsers(it))
            }


        }

        return list

    }

    fun getSystemList(stringList : ObservableList<String>) : ObservableList<SystemUsers>
    {
        val list = mutableListOf<SystemUsers>().observable()
        stringList.forEach {
            //if not db id or revision add
            if("_rev" == it)
            {}
            else if("_id" == it)
            {}
            else
            {
                list.add(SystemUsers(it))
            }

        }

        return list
    }





}