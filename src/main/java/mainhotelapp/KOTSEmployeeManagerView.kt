package mainhotelapp

import javafx.scene.layout.StackPane
import javafx.animation.FadeTransition
import javafx.scene.Node
import javafx.scene.control.*
import javafx.util.Duration
import kots.KOTS_EmployeeManager
import tornadofx.*

class KOTSEmployeeManagerView : View()
{
    val KOTS_EmployeeManagerViewAnchorPane: StackPane by fxml("/fxml/KOTSEmployeeManager.fxml")

    override val root = KOTS_EmployeeManagerViewAnchorPane
    val usernameTextField : TextField by fxid()
    val passwordTextField : TextField by fxid()
    val createUserBtn : Button by fxid()
    val deleteUserBtn : Button by fxid()
    val errorLabel : Label by fxid()
    val clientUsersTableView : TableView<String> by fxid()
    val systemUsersTableView : TableView<String> by fxid()
    var clientUsernamesList = mutableListOf("").observable()
    var systemUsernamesList = mutableListOf("").observable()
    val userTypeToggleGroup : ToggleGroup by fxid()

    init {

        //pass data model to tableView
        clientUsersTableView.items = clientUsernamesList
        systemUsersTableView.items = systemUsernamesList



        //hide error label
        errorLabel.opacity = 0.0


        createUserBtn.setOnMouseClicked {

            val employeeManager = KOTS_EmployeeManager()
            //if no blanks, create system user
            if(usernameTextField.text != "" ||usernameTextField.text != null
                    && passwordTextField.text != "" || passwordTextField.text != null)
            {
                val toggleValue = (userTypeToggleGroup.selectedToggleProperty().get() as RadioButton).text
                val userType = if(toggleValue == "client user") KOTS_EmployeeManager.kotsUserType.CLIENT else KOTS_EmployeeManager.kotsUserType.SYSTEM
                val updatedUsersList = employeeManager.createKotsUser(userType,usernameTextField.text, passwordTextField.text)
                if(updatedUsersList != null)
                {
                    //success handler
                    errorLabel.text = "User Created!"

                    fadeBlinkAnimation(errorLabel, 2.0, 2,true)
                    clientUsernamesList = updatedUsersList!!

                }
                else{
                    //duplicate username error
                    errorLabel.text = "User with that username has already been created. Sorry!"
                    fadeBlinkAnimation(errorLabel, 2.0, 2,true)
                }

            }
        }

        //complete later, figuring out how to get the row & username from the row in the table
        deleteUserBtn.setOnMouseClicked {

        }






    }




    fun fadeBlinkAnimation(forNode : Node, forSeconds : Double, howManyTimes: Int, setAutoReverse : Boolean) : FadeTransition
    {

        val ft = FadeTransition()
        ft.node = forNode
        ft.duration = Duration.seconds(forSeconds)
        ft.cycleCount = howManyTimes
        ft.isAutoReverse = setAutoReverse
        ft.fromValue = 0.0
        ft.toValue = 1.0

        return ft
    }

}