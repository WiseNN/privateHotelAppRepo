package mainhotelapp

import couchdb.Room
import javafx.scene.Parent
import javafx.scene.layout.AnchorPane
import javafx.scene.web.WebView
import tornadofx.*

class RoomBillingView(room : Room) : Fragment()
{
    private val rootFXML: AnchorPane by fxml("/fxml/RoomBillingUI.fxml")
    private val billingWebView : WebView by fxid()

    override val root =  rootFXML

    val room = room



    init{

        println("RoomBilling Room Number: ${room.roomNumber}")
//        room.roomNumber
//        room.notes
//        room.stayDuration
//        room.hasPetProperty
//        room.isSmoking
//        room.bedType
//        room.additionalPackages


        billingWebView.engine.executeScript("")
    }
}