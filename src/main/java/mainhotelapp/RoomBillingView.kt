package mainhotelapp

import couchdb.Room
import javafx.scene.Parent
import javafx.scene.layout.AnchorPane
import tornadofx.*

class RoomBillingView(room : Room) : Fragment()
{
    private val rootFXML: AnchorPane by fxml("/fxml/RoomBillingUI.fxml")


    override val root =  rootFXML

    val room = room

    init{

        println("RoomBilling Room Number: ${room.roomNumber}")
    }
}