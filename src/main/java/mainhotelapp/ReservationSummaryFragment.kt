package mainhotelapp

import couchdb.RestaurantTable
import couchdb.Room
import couchdb.Reservation
import javafx.concurrent.Worker
import javafx.print.Printer
import javafx.print.PrinterJob
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import netscape.javascript.*
import javafx.scene.layout.AnchorPane
import javafx.scene.web.WebView


import tornadofx.*
import java.text.SimpleDateFormat
import java.time.LocalDate


class ReservationSummaryFragment(dataModelList : List<Any>, serviceType : Reservation.serviceTypes) : Fragment()
{
    private val rootFXML: AnchorPane by fxml("/fxml/RoomBillingUI.fxml")
    private val billingWebView : WebView by fxid()

    //address fields

    private val firstNameTextField : TextField by fxid()
    private val lastnameTextField : TextField by fxid()
    private val addressOneTextField : TextField by fxid()
    private val addressTwoTextField : TextField by fxid()
    private val cityTextField : TextField by fxid()
    private val stateTextField : TextField by fxid()
    private val zipcodeTextField : TextField by fxid()


    //cardInfo fields
    private val firstFourCCTextField : TextField by fxid()
    private val secondFourCCTextField : TextField by fxid()
    private val thirdFourCCTextField : TextField by fxid()
    private val lastFourCCTextField : TextField by fxid()

    private val monthComboBox : ComboBox<Int> by fxid()
    private val yearComboBox : ComboBox<Int> by fxid()
    private val cardTypeComboBox : ComboBox<String> by fxid()

    private val secCodeTextField : TextField by fxid()

    //submit button
    private val proceedBillingBtn : Button by fxid()

    //billingInfoList
    private var billingInfoForTables: MutableList<String>? = null
    //tableRowList
    private var tableRowLineItems : MutableList<MutableList<String>>? = null

    //data model list
    private var dataModelList : List<Any>? = null


    override val root =  rootFXML



    init{


        val uiUtil = UIUtil()

        //store dataModel list
        this.dataModelList = dataModelList

        //add textField validation listeners
        firstNameTextField.textProperty().addListener { obs,o,n ->  uiUtil.validateText(obs,o,n,firstNameTextField,"Name") }
        lastnameTextField.textProperty().addListener { obs,o,n ->  uiUtil.validateText(obs,o,n,lastnameTextField,"Name")}
        cityTextField.textProperty().addListener { obs,o,n ->  uiUtil.validateText(obs,o,n,cityTextField,"City")}
        stateTextField.textProperty().addListener { obs,o,n ->  uiUtil.validateText(obs,o,n,stateTextField,"State")}
        zipcodeTextField.textProperty().addListener { obs,o,n ->  uiUtil.validateText(obs,o,n,zipcodeTextField,"ZipCode")}
        firstFourCCTextField.textProperty().addListener { obs,o,n ->  uiUtil.validateText(obs,o,n,firstFourCCTextField,"CC")}
        secondFourCCTextField.textProperty().addListener { obs,o,n ->  uiUtil.validateText(obs,o,n,secondFourCCTextField,"CC")}
        thirdFourCCTextField.textProperty().addListener { obs,o,n ->  uiUtil.validateText(obs,o,n,thirdFourCCTextField,"CC")}
        lastFourCCTextField.textProperty().addListener { obs,o,n ->  uiUtil.validateText(obs,o,n,lastFourCCTextField,"CC")}
        secCodeTextField.textProperty().addListener { obs,o,n ->  uiUtil.validateText(obs,o,n,secCodeTextField,"SEC")}

        monthComboBox.items = listOf(1,2,3,4,5,6,7,8,9,12,11,12).observable()
        yearComboBox.items = listOf(2018,2019,2020,2021,2022,2023).observable()
        cardTypeComboBox.items = listOf("VISA", "MASTERCARD","AMX").observable()

        proceedBillingBtn.setOnMouseClicked {
            //list values
            //0 -> descriptionBlock
            //1 -> notesBlock
            //2 -> Reservation ID
            // -> descrLineItem--REMOVED--
            // -> Length--REMOVED--
            // -> Rate--REMOVED--
            // -> Amount--REMOVED--
            //3 -> Billing First Name
            //4 -> Billing Last Name
            //5 -> Address 1
            //6 -> Address 2
            //7 -> Billing City
            //8 -> Billing State
            //9 -> Billing ZipCode
            //10 -> first4CC
            //11 -> second4CC
            //12 -> third4CC
            //13 -> last4CC
            //14 -> SEC_CC
            //15 -> exprMonth
            //16 -> exprYr
            //17 -> cardType

            billingInfoForTables!!.add(firstNameTextField.text)
            billingInfoForTables!!.add(lastnameTextField.text)
            billingInfoForTables!!.add(addressOneTextField.text)
            billingInfoForTables!!.add(addressTwoTextField.text)
            billingInfoForTables!!.add(cityTextField.text)
            billingInfoForTables!!.add(stateTextField.text)
            billingInfoForTables!!.add(zipcodeTextField.text)
            billingInfoForTables!!.add(firstFourCCTextField.text)
            billingInfoForTables!!.add(secondFourCCTextField.text)
            billingInfoForTables!!.add(thirdFourCCTextField.text)
            billingInfoForTables!!.add(lastFourCCTextField.text)
            billingInfoForTables!!.add(secCodeTextField.text)
            billingInfoForTables!!.add("${monthComboBox.value}")
            billingInfoForTables!!.add("${yearComboBox.value}")
            billingInfoForTables!!.add("${cardTypeComboBox.value}")



            val finalBillView = FinalBillingFragment(billingInfoForTables!!,tableRowLineItems!!)
            finalBillView.openWindow()

        }

        //get the billing Summary's URL
        val url = this.javaClass.getResource("/html/billing/BillingSummary.html")



        billingWebView.engine.load(url.toString())

        billingWebView.engine.loadWorker.stateProperty().onChange {



            println("web source data: "+it.toString())
            if(it == Worker.State.SUCCEEDED)
            {

                //when page loads, attach this java class to the window as an object
                val window = billingWebView.engine.executeScript("window") as JSObject


                window.setMember("javaApp", this)

                //hide the final out billing table
                billingWebView.engine.executeScript("bill.showFinalOutBillingTable(false)")




                if(serviceType == Reservation.serviceTypes.hotelRoom)
                {
                    //get reservation billing list for Rooms
                    billingInfoForTables = getBillingInfoForRoom(this.dataModelList!![0] as Room)
                    billingWebView.engine.executeScript("bill.setDocumentTitle(\"Hotel Room Reservation Summary\")")
                    tableRowLineItems = createLineItemsForRoomReservation(dataModelList as List<Room>)
                }
                else if(serviceType == Reservation.serviceTypes.restuarantBar)
                {
                    //get reservation billing information list for tables
                    billingInfoForTables = getBillingInfoForTable(dataModelList[0] as RestaurantTable)
                    billingWebView.engine.executeScript("bill.setDocumentTitle(\"Restaurant Table Reservation Summary\")")
                    tableRowLineItems = createLineItemsForTableReservation(dataModelList as List<RestaurantTable>)

                }

                billingWebView.engine.executeScript("bill.setDateText(\"${LocalDate.now()}\")")
                billingWebView.engine.executeScript("bill.setReservationNumberText(\"${billingInfoForTables!![2]}\")")
                billingWebView.engine.executeScript("bill.setRoomDescriptionTextBlock(\"${billingInfoForTables!![0]}\")")
                billingWebView.engine.executeScript("bill.setNotesTextBlock(\"${billingInfoForTables!![1]}\")")



                //create the table rows for HTML document


                tableRowLineItems!!.forEach {

                    billingWebView.engine.executeScript("bill.addRowToTable(\"${it[0]}\", \"${it[1]}\", \"${it[2]}\", \"${it[3]}\")")
                }





            }
        }

        }


    //For table reservations
    //for room reservations
    //list values
    //0 -> descriptionBlock
    //1 -> notesBlock
    //2 -> reservationNumber
    private fun getBillingInfoForRoom(forRoom:Room) : MutableList<String>
    {

        val billingInfoList = mutableListOf<String>()


        var roomNumberStr = "<br><br><u><b>Room Number</b></u>: ${forRoom.roomNumber}<br>"
        var roomDescrBlock = "One Beautiful ${forRoom.roomType.toString().capitalize()} Master Bedroom, with one ${forRoom.bedType.toString().capitalize()} bed."
        var notesBlock = parseNotesToHTML(forRoom.notes)

        //get room amenities
        val amenitiesList = if(forRoom.amenities != null) forRoom.amenities.split(";".toRegex()) else listOf()

        //put all amenities in a formatted string with out truthy value
        var amenitiesStr = "<u>Room Amenitites:</u><br>"
        amenitiesList.forEach {
            amenitiesStr += it.substring(0,it.indexOf("="))+", "
        }

        //ad roomNumber string to description block
        roomDescrBlock += roomNumberStr
        //add amenitites to description Block String
        roomDescrBlock += "<br>$amenitiesStr"


        //store string values in list
        billingInfoList.add(roomDescrBlock)
        billingInfoList.add(notesBlock)
        billingInfoList.add(forRoom.activeReservation.reservationID)



        return billingInfoList
    }


    //For table reservations
    //for room reservations
    //list values
    //0 -> descriptionBlock
    //1 -> notesBlock
    //2 -> reservationNumber
    private fun getBillingInfoForTable(forTable:RestaurantTable) : MutableList<String>
    {

        val billingInfoList = mutableListOf<String>()

        var tableNumberStr = "<br><br><u><b>Table Number</b></u>: ${forTable.tableNumber}<br>"
        var roomDescrBlock = "Restaurant Reservation with ${parseNotesToHTML(forTable.activeReservation.printReservation())}"
        var notesBlock = parseNotesToHTML(forTable.activeReservation.specialRequests)
        val reservationDate = forTable.activeReservation.fromDate

//        formattedDate += " - "+SimpleDateFormat("dd/MM/yyyy").parse("$day/$month/$yr").toString().substring(0,10)

//        println("formatted date: "+formattedDate)

        //append to date

        //ad roomNumber string to description block
        roomDescrBlock += tableNumberStr

        //store string values in list
        billingInfoList.add(roomDescrBlock)
        billingInfoList.add(notesBlock)
        billingInfoList.add(forTable.activeReservation.reservationID)


        return billingInfoList
    }

    //list of line items for when adding rows to the table
    //0 -> table1
        //0 -> DescrLineItem
        //1 -> length / date
        //2 -> rate
        //3 -> lineItemTotal
    //1 -> table2
        //0 -> DescrLineItem
        //1 -> length / date
        //2 -> rate
        //3 -> lineItemTotal
    //...
    private fun createLineItemsForTableReservation(forTables:List<RestaurantTable>) : MutableList<MutableList<String>>
    {

        val listOfLineItems = mutableListOf<MutableList<String>>()
        forTables.forEach {

            val tableDescrLineItem = "Table # ${it.tableNumber} with ${it.numberOfSeats} seats"

            val nightlyRate = "$129.37"
            val lineItemTotal = "$"+(129.37*4).toString()
            val lineItemList = mutableListOf<String>()
            lineItemList.add(tableDescrLineItem)
            lineItemList.add(it.activeReservation.fromDate.toString())
            lineItemList.add(nightlyRate)
            lineItemList.add(lineItemTotal)

            listOfLineItems.add(lineItemList)
        }

        return listOfLineItems
    }

    //list of line items for when adding rows to the table
    //0 -> table1
        //0 -> DescrLineItem
        //1 -> length / date
        //2 -> rate
        //3 -> lineItemTotal
    //1 -> table2
        //0 -> DescrLineItem
        //1 -> length / date
        //2 -> rate
        //3 -> lineItemTotal
    //...
    private fun createLineItemsForRoomReservation(forRooms:List<Room>) : MutableList<MutableList<String>>
    {
        val lineItemList = mutableListOf<String>()
        val listOfLineItems = mutableListOf<MutableList<String>>()
        forRooms.forEach {

            val roomDescrLineItem = "Master ${it.bedType} ${it.roomType} Bedroom"
            val stayLengthList = it.stayDuration.split(";")

            //create Date
            var month = stayLengthList[0].substring(5,7)
            var day = stayLengthList[0].substring(8)
            var yr = stayLengthList[0].substring(0,4)


            var formattedDate = SimpleDateFormat("dd/MM/yyy").parse("$day/$month/$yr").toString().substring(0,10)


            month = stayLengthList[1].substring(5,7)
            day = stayLengthList[1].substring(8)
            yr = stayLengthList[1].substring(0,4)

            formattedDate += " - "+SimpleDateFormat("dd/MM/yyyy").parse("$day/$month/$yr").toString().substring(0,10)


            println("formatted date: "+formattedDate)

            //append to date



            val nightlyRate = "$129.37"
            val lineItemTotal = "$"+(129.37*4).toString()


            lineItemList.add(roomDescrLineItem)
            lineItemList.add(formattedDate)
            lineItemList.add(nightlyRate)
            lineItemList.add(lineItemTotal)
            listOfLineItems.add(lineItemList)
        }

        return listOfLineItems
    }

    private fun parseNotesToHTML(notes : String) : String
    {
        val valid_HTML_notes = notes.replace("\n","<br>")
        return valid_HTML_notes
    }


    fun print(event : javafx.event.ActionEvent)
    {
        print("event: ${event.source}")

        val printers = Printer.getAllPrinters()
        println("printers: "+printers.toString())

        val job = PrinterJob.createPrinterJob()
        println("job: $job")
        if (job != null) {
            job.showPrintDialog(this.primaryStage) // Window must be your main Stage
            job.printPage(root)
            job.endJob()
        }
    }

    /*SECOND PRINT FUNCTION
         public void print(final Node node)
         {
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
        double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
        double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
        node.getTransforms().add(new Scale(scaleX, scaleY));

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean success = job.printPage(node);
            if (success) {
                job.endJob();
            }
        }
}*/









}