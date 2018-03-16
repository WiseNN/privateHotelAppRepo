package mainhotelapp

import couchdb.Room
import hotelbackend.HotelBackend
import javafx.application.Application
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
    val roomBookingTypeComboBox: ComboBox<Room.roomType> by fxid()
    val numOfBookedRoomsComboBox: ComboBox<Int> by fxid()
    val searchBookingBtn: Button by fxid()
    //---------fxml ---------fxml ---------fxml ---------fxml ---------

//------ fxmlControllerLogic include ------- fxmlControllerLogic include -------

    val fxmlControllerRef = ReservationFXMLControllerLogic()

    //------ fxmlControllerLogic include ------- fxmlControllerLogic include -------



    val availableRoomsLabelWithDivider: HBox by fxid()


    init {

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

        //call BookRoom function to get available rooms for selected Date (currently using dummy data)
        val availableRooms = HotelBackend().bookRoom(LocalDate.now(), LocalDate.now(), Room.roomType.reg, 5)

        //create a listOfAvailableRooms, make observable for the TableView
        var listOfAvailableRooms = mutableListOf<AvailableRooms>().observable()

        //initialize oneRoom from AvailableRooms class so the amenities property can be accessed
        var oneAvailableRoom = AvailableRooms()

        //map each room in the availableRooms to the observable listOfAvailableRooms
        for (i in availableRooms.indices) {
            //get room Object from ArrayList, cast to HashMap
            val room = availableRooms[i] as HashMap<String, Object>

            //get room properties from the hashMap, cast them to their corresponding types
            val hasPet = (room["hasPet"] as String)
            val roomType = room["roomType"] as String
            val bedType = room["bedType"] as String
            val isSmoking = room["isSmoking"] as String
            val amenities = room["amenities"] as HashMap<String, Boolean>

            val listOfAmenitiesInRoom = mutableListOf<String>()

            //map Amenities for each room with index
            amenities.forEach { key, value ->


                listOfAmenitiesInRoom.add("$key : " + if (value) "yes" else "no")

            }


            //update oneAvailableRoom object from AvailableRooms class, and store in list
            oneAvailableRoom = AvailableRooms((i + 1), "defaultValue", "defaultValue", hasPet, roomType, bedType, isSmoking, listOfAmenitiesInRoom)

            listOfAvailableRooms.add(oneAvailableRoom)


        }
        val rooms: ObservableList<AvailableRooms> = listOfAvailableRooms

        println(availableRooms)



        tableview<AvailableRooms>(listOfAvailableRooms) {


            //            column<AvailableRooms, Int>("Room No.", AvailableRooms::roomNumberProperty)

            val roomNumCol = TableColumn<AvailableRooms, Int>("Room Number")

            roomNumCol.setCellValueFactory { it.value.roomNumberProperty() }


            val durationColumn = TableColumn<AvailableRooms, String>("Duration")
            durationColumn.prefWidthProperty().set(120.0)
            durationColumn.minWidthProperty().set(durationColumn.prefWidth)
            durationColumn.setCellFactory { StayDurationTableCell(durationColumn) }
            durationColumn.setCellValueFactory { it.value.stayDurationProperty }


            val notesColumn = TableColumn<AvailableRooms, String>("Notes")
            notesColumn.prefWidthProperty().set(100.0)
            notesColumn.minWidthProperty().set(notesColumn.prefWidth)
            notesColumn.resizableProperty().set(true)
            notesColumn.setCellFactory { NotesTableCell("WiseNN", notesColumn) }
            notesColumn.setCellValueFactory { it.value.notesProperty }

            val addPackagesColumn = TableColumn<AvailableRooms, String>("Add. Packages")
            addPackagesColumn.prefWidthProperty().set(130.0)

//            addPackagesColumn.minWidthProperty().set(addPackagesColumn.prefWidth)

//            addPackagesColumn.resizableProperty().set(true)
            addPackagesColumn.setCellFactory { PackagesTableViewCell(addPackagesColumn) }
            addPackagesColumn.setCellValueFactory { it.value.addPackagesStringProperty}




            val submitButtonColumn = TableColumn<AvailableRooms, String>("Submit")
            submitButtonColumn.setCellFactory { SubmitBtnTableViewCell(submitButtonColumn) }
//            submitButtonColumn.prefWidthProperty().set(150.0)
            submitButtonColumn.setCellValueFactory { SimpleStringProperty("Submit") }

            this.columns.setAll(roomNumCol, durationColumn, notesColumn, addPackagesColumn, submitButtonColumn)

        }

        roomBookingTypeComboBox.items.addAll(Room.roomType.reg,Room.roomType.handi,Room.roomType.suite)
        numOfBookedRoomsComboBox.items.addAll(1,2,3,4,5)
        searchBookingBtn.setOnMouseClicked {
            val rez =  HotelBackend().bookRoom(fromBookingDatePicker.value, toBookingDatePicker.value, roomBookingTypeComboBox.value, numOfBookedRoomsComboBox.value)
            println("rez: $rez")



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

