package mainhotelapp

import couchdb.Reservation
import hotelbackend.HotelBackEndNorris
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.stage.StageStyle
import tornadofx.*
import java.sql.Date
import java.time.LocalTime

class RestaurantReservationView(parentView : MyButtonBarView) : View()
{
     val restaurantTableFXML: AnchorPane by fxml("/fxml/RestuarantReservationUI.fxml")
    val firstNameTextField : TextField by fxid()
    val lastNameTextField: TextField by fxid()
    val emailTextField : TextField by fxid()
    val phoneNumberTextField : TextField by fxid()
    val reservationFromDatePicker : DatePicker by fxid()
    val specialRequestsTextArea : TextArea by fxid()
    val reservationTimesComboBox : ComboBox<String> by fxid()
    val partySizeComboBox : ComboBox<String> by fxid()
    val bookNowBtn : Button by fxid()


    override val root = restaurantTableFXML

    init {
        val uiUtil = UIUtil()

        firstNameTextField.textProperty().addListener { obs,o,n ->  uiUtil.validateText(obs,o,n,firstNameTextField,"Name") }
        lastNameTextField.textProperty().addListener { obs, o, n ->  uiUtil.validateText(obs,o,n, lastNameTextField,"Name")}
        emailTextField.textProperty().addListener { obs, o, n ->  uiUtil.validateText(obs,o,n, emailTextField,"Email")}
        phoneNumberTextField.textProperty().addListener { obs, o, n ->  uiUtil.validateText(obs,o,n, phoneNumberTextField,"Phone")}


        val reservationTimesList = mutableListOf<String>().observable()
        for(i in 8..12)
        {
            reservationTimesList.add("$i:${ if(i%2 == 0) "00" else "30"} ${if(i!=12) "AM" else "PM"} ")
        }

        for(i in 13..22)
        {
            reservationTimesList.add("${(i % 12)}:${ if(i%2 == 0) "00" else "30"} PM")
        }

        println("reservationTimes List: ${reservationTimesList}")

        reservationTimesComboBox.items = reservationTimesList

        for(i in 5..70)
        {
            partySizeComboBox.items.add("$i")
        }

        bookNowBtn.setOnMouseClicked {
            val timeString = reservationTimesComboBox.value
            var hr = if(timeString.substring(0,1) != "8" && timeString.substring(0,1) != "9")timeString.substring(0,2).toInt() else timeString.substring(0,1).toInt()
            val mins = if(hr == 8 || hr == 9) timeString.substring(2,4).toInt() else timeString.substring(3,5).toInt()
            val period = reservationTimesComboBox.value.substring(5)

            if(period.equals("PM")) hr += 12
            val reservationTime = LocalTime.of(hr,mins)

            val partySize = partySizeComboBox.value.toInt()
            val specialReq = specialRequestsTextArea.text
            val name = "${firstNameTextField.text} ${lastNameTextField.text}"
            val emailAdr = emailTextField.text
            val phoneNumber = phoneNumberTextField.text


            val tableRezList = HotelBackEndNorris().bookTable(Date.valueOf(reservationFromDatePicker.value),reservationTime,partySize,specialReq, name,emailAdr,phoneNumber)
            println("tableRezList: $tableRezList")

            val billingView = ReservationSummaryFragment(tableRezList, Reservation.serviceTypes.restuarantBar)

            billingView.openWindow(stageStyle = StageStyle.DECORATED)



        }

    }






}