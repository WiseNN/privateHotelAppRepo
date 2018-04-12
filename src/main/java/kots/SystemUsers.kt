package kots

import javafx.beans.property.SimpleStringProperty
import tornadofx.getValue
import tornadofx.setValue

class SystemUsers(name: String)
{
    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty

}

