package mainhotelapp

import javafx.scene.Parent
import javafx.scene.layout.StackPane
import tornadofx.*

class EditOrderPOSView(parentView: View) : View()
{
    val editOrderView : StackPane by fxml("/fxml/EditOrder_POS_Item_UI.fxml")
    override val root = editOrderView

}