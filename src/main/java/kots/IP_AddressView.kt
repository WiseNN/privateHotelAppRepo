package kots

import devutil.ConsoleColors
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import mainhotelapp.RestaurantPOSView
import tornadofx.*

class IP_AddressView(parentView : RestaurantPOSView) : View()
{
    val ipAddressView : AnchorPane by fxml("/fxml/IPAddressView.fxml")
    val ipAddressTextField : TextField by fxid()
    val connectBtn : Button by fxid()

    override val root = ipAddressView

    init {

        connectBtn.setOnMouseClicked {
            if(ipAddressTextField.text != "" || ipAddressTextField.text != null)
            {

                parentView.ipAddressView.close()
                parentView.connectToServer(ipAddressTextField.text)


            }
            else{
                println(ConsoleColors.yellowText("INVALID IP ADDRESS INPUT"))
            }

        }
    }

}