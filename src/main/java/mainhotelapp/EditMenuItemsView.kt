package mainhotelapp

import hotelbackend.HotelBackEndNorris
import javafx.scene.Parent
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import couchdb.*;
import devutil.MyUtil

import tornadofx.*
class EditMenuItemsView : View()
{

    val editPOS_MenuView: VBox by fxml("/fxml/Edit_POS_Menu_UI.fxml")
    val categoryTextField : TextField by fxid()
    val itemTextField : TextField by fxid()
    val priceTextField : TextField by fxid()

    override val root = editPOS_MenuView

    fun addItem(itemName: String, inCategory:String)
    {
        //get menu from database
        //search, retrieve category for item in menuDB
        //add item to menu
        //save menuDB

        val db = DB()
        val menuMap = db.readDocInDB(DBNames.restaurantMenu);
        val serializedMenu = menuMap.get("menu") as String
        val menu =MyUtil().deserializeObject(RestaurantItem::class.java, serializedMenu)

    }
    fun addCategory(categoryName: String)
    {

    }



}