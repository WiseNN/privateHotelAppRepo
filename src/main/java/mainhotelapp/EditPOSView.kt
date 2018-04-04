package mainhotelapp

import couchdb.DB
import couchdb.DBNames
import couchdb.RestaurantItem
import hotelbackend.HotelBackEndNorris
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.Button
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.VBox
import tornadofx.*

class EditPOSView(parentView : MyButtonBarView) : View()
{
    val editRestaurantPOS: VBox by fxml("/fxml/Edit_POS_Menu_UI.fxml")
    val editItemsToggleGroup : ToggleGroup by fxid()
    val categoryTextField : TextField by fxid()
    val itemTextField : TextField by fxid()
    val priceTextField : TextField by fxid()
    val submitBtn : Button by fxid()


//    var toggleState = (editItemsToggleGroup.selectedToggleProperty().get() as RadioButton).text
var toggleState : SimpleStringProperty = SimpleStringProperty("")



    override val root = editRestaurantPOS

    init {

        val selectedRadioBtn = (editItemsToggleGroup.selectedToggleProperty().get() as RadioButton)
        //make biDirectional to avoid using an Observable, can use another property instead
        toggleState.bindBidirectional(selectedRadioBtn.textProperty())



//        toggleState.bind(selectedRadioBtn.textProperty())


//        editItemsToggleGroup.selectedToggleProperty().addListener(ChangeListener { observable, oldValue, newValue ->
//            val selectedRadioTxt = (editItemsToggleGroup.selectedToggleProperty().get() as RadioButton).text
//            println("selected Toggle: "+selectedRadioTxt)
//
//                toggleState = selectedRadioTxt
//
//
//            when(toggleState)
//            {
//                "Add Item" -> {
//                    categoryTextField.disableProperty().set(false)
//                    itemTextField.disableProperty().set(false)
//                    priceTextField.disableProperty().set(false)
//                }
//                "Remove Item" -> {
//                    categoryTextField.disableProperty().set(false)
//                    itemTextField.disableProperty().set(false)
//                    priceTextField.disableProperty().set(true)
//                }
//                "Modify Item" -> {
//                    categoryTextField.disableProperty().set(false)
//                    itemTextField.disableProperty().set(false)
//                    priceTextField.disableProperty().set(false)
//                }
//
//            }
//        })


        submitBtn.setOnMouseClicked {
            val category = categoryTextField.text
            val item = if (!itemTextField.text.equals("")) itemTextField.text else ""
            val price = if (!priceTextField.text.equals("")) priceTextField.text.toDouble() else -1.0
            var updatedName = null
            var updatedPrice = -1
            var sendState = ""

            sendState = toggleState.value
            //silently add a category if the item is not passed
            if(toggleState.value.equals("Add Item") && item.equals("") && !category.equals(""))
            {
                sendState = "Add Category"
            }
            else if(toggleState.equals("Remove Item") && item.equals("") && !category.equals(""))
            {
                sendState = "Remove Category"
            }



            HotelBackEndNorris().updatePOSMenu(sendState, category, item, price,updatedName, updatedPrice)


            val restaurantView = (find(MyButtonBarView::class).restuarantPOSView as RestaurantPOSView)



        restaurantView.menuDB["menu"] = RestaurantItem().getDeserializedMenu(DB().readDocInDB(DBNames.restaurantMenu)).observable()

        println("map: "+restaurantView.menuDB.toString())

        }
    }
}