package mainhotelapp

import javafx.scene.layout.AnchorPane
import tornadofx.*

class RestaurantReservationView(parentView : MyButtonBarView) : View()
{
     val restaurantTableFXML: AnchorPane by fxml("/fxml/RestuarantReservationUI.fxml")
    override val root = restaurantTableFXML


}