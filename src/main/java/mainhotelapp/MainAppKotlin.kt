package mainhotelapp


import couchdb.DB
import couchdb.DBNames
import couchdb.Room
import hotelbackend.HotelBackEndNorris
//import hotelbackend.HotelBackend
import javafx.application.Application
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.geometry.Orientation
import mainhotelapp.customCells.NotesTableCell
import mainhotelapp.customCells.PackagesTableViewCell
import mainhotelapp.customCells.StayDurationTableCell
import mainhotelapp.customCells.SubmitBtnTableViewCell
import tornadofx.*;
import java.sql.Date
import java.time.LocalDate
import kotlin.collections.HashMap


//UI constants
val middleLayerSplit = 3.0
val calenderBtnSizeDiviser = 8
val calenderBtnHeightSize = 35.0
val numOfCalenderDaysInOneRow = 5.0
val calenderMaxWidth = 400.0
val calenderMinWidth = 255.0
val calenderMaxHeight = 255.0
val calenderFramePadding = 5.0

val barBtnSizeDiviser = 8

//mainView for Reservation Screen
class ReservationView : View()
{

    override val root = VBox()

    //------- fxml --------- fxml --------- fxml --------- fxml ---------
    val rootFXML: VBox by fxml("/fxml/ReservationSystemUI.fxml")
    val bookingContainer: VBox by fxid()
    val fromBookingDatePicker: DatePicker by fxid()
    val toBookingDatePicker: DatePicker by fxid()
    val roomBookingTypeComboBox: ComboBox<Room.allRoomTypes> by fxid()
    val numOfBookedRoomsComboBox: ComboBox<Int> by fxid()
    val searchBookingBtn: Button by fxid()
    //---------fxml ---------fxml ---------fxml ---------fxml ---------

//------ fxmlControllerLogic include ------- fxmlControllerLogic include -------

    val fxmlControllerRef = ReservationFXMLControllerLogic()

    //------ fxmlControllerLogic include ------- fxmlControllerLogic include -------



    val availableRoomsLabelWithDivider: HBox by fxid()


    init {


        //create sample data
        val roomsClass = Room()
        val db = DB()
        db.permenantlyRemoveDoc(DBNames.rooms)
        db.permenantlyRemoveDoc(DBNames.reservations)
        roomsClass.createRooms()
        db.createDoc(DBNames.reservations, HashMap<String, Any>())

        root.minWidth = 699.0
        root.minHeight = 714.0

        add(MyButtonBarView(root))

        //seperates the top Menu button bar and the middleLayer components
        separator {

            //            paddingProperty().set(Insets(5.0,0.0,5.0,0.0))
//            isVisible = true
            orientation = Orientation.HORIZONTAL

            prefHeightProperty().set(10.0)
            prefWidthProperty().bind(root.widthProperty())
        }

        // middleLayer that holds booking datePicker, calender, and the webview
        splitpane {

            orientation = Orientation.HORIZONTAL
            this.vgrow = Priority.ALWAYS



            add(bookingContainer)


            val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31)


            //add calender to the HBox
            add(CalenderView(list, this))


            webview {

                //this is referring to the constructor that I am currently in (webview)
                //this@vbox is referring to a hbox that I have created above it
                //the bind is saying make my width property the same as the width property in
                //  put in between the parentheses ()

                this.prefWidthProperty().bind(this@splitpane.widthProperty().divide(middleLayerSplit))
                this.prefHeightProperty().bind(this@splitpane.heightProperty())

                engine.load("http://google.com")

            }
        }

        //from FXML
        add(availableRoomsLabelWithDivider)







        var listOfAvailableRooms = mutableListOf<Room>().observable()






        tableview<Room>(listOfAvailableRooms){



            //            column<AvailableRooms, Int>("Room No.", AvailableRooms::roomNumberProperty)

            val roomNumCol = TableColumn<Room, String>("Room Number")

            roomNumCol.setCellValueFactory { it.value.roomNumberProperty }
            roomNumCol.minWidthProperty().set(100.0)


            val durationColumn = TableColumn<Room, String>("Duration")
            println("date: "+fromBookingDatePicker.value)
            val durationDateString : String = (fromBookingDatePicker.valueProperty().toString()+"-"+toBookingDatePicker.valueProperty().toString())

            durationColumn.prefWidthProperty().set(120.0)
            durationColumn.minWidthProperty().set(durationColumn.prefWidth)
            durationColumn.setCellFactory { StayDurationTableCell(durationColumn) }
            durationColumn.setCellValueFactory {it.value.stayDurationProperty}


            val notesColumn = TableColumn<Room, String>("Notes")
            notesColumn.prefWidthProperty().set(100.0)
            notesColumn.minWidthProperty().set(notesColumn.prefWidth)
            notesColumn.resizableProperty().set(true)
            notesColumn.setCellFactory { NotesTableCell("WiseNN", notesColumn) }
            notesColumn.setCellValueFactory { it.value.notesProperty }

            val addPackagesColumn = TableColumn<Room, String>("Add. Packages")
            addPackagesColumn.prefWidthProperty().set(135.0)
            addPackagesColumn.minWidthProperty().set(135.0)

//            addPackagesColumn.minWidthProperty().set(addPackagesColumn.prefWidth)

//            addPackagesColumn.resizableProperty().set(true)
            addPackagesColumn.setCellFactory { PackagesTableViewCell(addPackagesColumn) }
            addPackagesColumn.setCellValueFactory { it.value.additionalPackagesProperty }




            val submitButtonColumn = TableColumn<Room, String>("Submit")
            submitButtonColumn.setCellFactory { SubmitBtnTableViewCell(submitButtonColumn) }
//            submitButtonColumn.prefWidthProperty().set(150.0)
            submitButtonColumn.setCellValueFactory { SimpleStringProperty("Submit") }

            this.columns.setAll(roomNumCol, durationColumn, notesColumn, addPackagesColumn, submitButtonColumn)

        }

        //room booking request params: fromDate, toDate, roomType, numOfRooms
        roomBookingTypeComboBox.items.addAll(Room.allRoomTypes.reg,Room.allRoomTypes.handi,Room.allRoomTypes.suite)
        numOfBookedRoomsComboBox.items.addAll(1,2,3,4,5)
        searchBookingBtn.setOnMouseClicked {
            val rez =  HotelBackEndNorris().bookRoomNorris(Date.valueOf(fromBookingDatePicker.value), Date.valueOf(toBookingDatePicker.value), roomBookingTypeComboBox.value, numOfBookedRoomsComboBox.value)
            println("rez: $rez")

            rez.forEach {

                it.roomNumberProperty = SimpleStringProperty(""+it.roomNumber)
                it.roomTypeProperty = SimpleObjectProperty<Room.allRoomTypes>(it.roomType)
                it.stayDurationProperty = SimpleStringProperty(it.stayDuration)
                it.bedTypeProperty = SimpleObjectProperty<Room.allBedTypes>(it.bedType)
                it.isSmokingProperty = SimpleBooleanProperty(it.isSmoking)
                it.hasPetProperty = SimpleBooleanProperty(it.hasPet)
                it.isAvailableForOccupancyProperty = SimpleBooleanProperty(it.isAvailableForOccupancy)
                it.notesProperty = SimpleStringProperty(it.notes)
                it.amenitiesProperty  = SimpleObjectProperty(it.amenities)
                it.notesProperty = SimpleStringProperty(it.notes)

                it.additionalPackagesProperty = SimpleStringProperty(it.additionalPackages)




            }.apply {
                listOfAvailableRooms.removeAll()

                listOfAvailableRooms.addAll(rez)
            }





        }

    }



}




    class MainAppKotlin : App(ReservationView::class)
    {

        init {
            importStylesheet("/styles/RectBtnStyle.css")
            reloadStylesheetsOnFocus()

        }
    }


fun main(args: Array<String>) {

        Application.launch(MainAppKotlin::class.java, *args)


    }

