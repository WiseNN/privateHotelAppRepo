package kots

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ClientUsers(name: String)
{
    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty

}