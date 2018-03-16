package mainhotelapp

import javafx.beans.Observable
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue

class AvailableRooms(roomNumber:Int = 0, stayDuration : String = "defaultValue",notes: String = "defaultValue",hasPet: String="defaultValue",
                     roomType: String="defaultValue", bedType: String="defaultValue", isSmoking: String="defaultValue",
                     amenities: MutableList<String> = mutableListOf("defaultValue"),
                     addPackages : MutableList<String> = mutableListOf("defaultValue","defaultValue","defaultValue","defaultValue","defaultValue","defaultValue","defaultValue","defaultValue","defaultValue"))
{
//    val roomNumberProperty = SimpleIntegerProperty(roomNumber)
//    fun roomNumberProperty() = roomNumberProperty
//    var roomNumber by roomNumberProperty
    var roomNumber by property(roomNumber)
    fun roomNumberProperty() = getProperty(AvailableRooms::roomNumber)

    val stayDurationProperty = SimpleStringProperty(stayDuration)
    var stayDuration by stayDurationProperty

    var hasPet by property(hasPet)
    val hasPetProperty = getProperty(AvailableRooms::hasPet)

    val notesProperty = SimpleStringProperty(notes)
    var notes by notesProperty

//    var amenities by property(amenities)
//    val amenitiesProperty = getProperty(AvailableRooms::amenities)

    var amenities by property(amenities)
    val amenitiesProperty : ObservableList<String> = amenities.observable()

    var addPackagesStringProperty = SimpleStringProperty()
    var addPackagesString by addPackagesStringProperty


    val roomTypeProperty = SimpleStringProperty(roomType)
    var roomType by roomTypeProperty

    val bedTypeProperty = SimpleStringProperty(bedType)
    var bedType by bedTypeProperty

    val isSmokingProperty = SimpleStringProperty(isSmoking)
    var isSmoking by isSmokingProperty


    init {
            val pkgsString = addPackages.joinToString(";")
        println("value of pkgsStrring from Available Rooms: $pkgsString")
            addPackagesStringProperty = SimpleStringProperty(pkgsString)

    }


}