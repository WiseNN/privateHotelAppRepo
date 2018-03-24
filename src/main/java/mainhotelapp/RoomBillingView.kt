package mainhotelapp

import couchdb.Room
import javafx.collections.ObservableSet
import javafx.concurrent.Worker
import javafx.print.Printer
import javafx.print.PrinterJob
import javafx.scene.Parent
import netscape.javascript.*
import javafx.scene.layout.AnchorPane
import javafx.scene.web.WebView


import tornadofx.*
import java.awt.event.ActionEvent
import java.text.SimpleDateFormat
import java.util.*
import java.awt.SystemColor.window



class RoomBillingView(room : Room) : Fragment()
{
    private val rootFXML: AnchorPane by fxml("/fxml/RoomBillingUI.fxml")
    private val billingWebView : WebView by fxid()

    override val root =  rootFXML

    val room = room

    init{

        println("RoomBilling Room Number: ${room.roomNumber}")
//        room.roomNumber
//        room.notes
//        room.stayDuration
//        room.hasPetProperty
//        room.isSmoking
//        room.bedType
//        room.additionalPackages

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

                val billingInfo = getBillingInfoForRoom(room)


                val compliant_HTML_Notes = parseNotesToHTML(billingInfo[1]);

                billingWebView.engine.executeScript("bill.setRoomDescriptionTextBlock(\"${billingInfo[0]}\")")
                billingWebView.engine.executeScript("bill.setNotesTextBlock(\"${compliant_HTML_Notes}\")")
                billingWebView.engine.executeScript("bill.addRowToTable(\"${billingInfo[2]}\", \"${billingInfo[3]}\", \"${billingInfo[4]}\", \"${billingInfo[5]}\")")
                billingWebView.engine.executeScript("bill.addRowToTable(\"Taxes\", \"\", \"\", \"$12.34\")")

            }
        }

        }

    //list values
    //0 -> descriptionBlock
    //1 -> notesBlock
    //2 -> descrLineItem
    //3 -> Length
    //4 -> Rate
    //5 -> Amount
    fun getBillingInfoForRoom(forRoom:Room) : List<String>
    {

        val billingInfoList = mutableListOf<String>()

        var roomNumberStr = "<br><br><u><b>Room Number</b></u>: ${forRoom.roomNumber}<br>"
        var roomDescrBlock = "One Beautiful ${forRoom.roomType.toString().capitalize()} Master Bedroom, with one ${forRoom.bedType.toString().capitalize()} bed."
        var notesBlock = forRoom.notes
        val roomDescrLineItem = "Master ${forRoom.bedType} ${forRoom.roomType} Bedroom"
        val stayLengthList = forRoom.stayDuration.split(";")

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
        billingInfoList.add(roomDescrLineItem)
        billingInfoList.add(formattedDate)
        billingInfoList.add(nightlyRate)
        billingInfoList.add(lineItemTotal)


        return billingInfoList
    }

    fun parseNotesToHTML(notes : String) : String
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