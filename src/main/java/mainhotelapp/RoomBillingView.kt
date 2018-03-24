package mainhotelapp

import com.sun.javafx.property.adapter.PropertyDescriptor
import couchdb.Room
import devutil.ConsoleColors
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableSet
import javafx.concurrent.Worker
import javafx.print.Printer
import javafx.print.PrinterJob
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import netscape.javascript.*
import javafx.scene.layout.AnchorPane
import javafx.scene.web.WebView
import javafx.stage.StageStyle


import tornadofx.*
import java.awt.event.ActionEvent
import java.text.SimpleDateFormat
import java.util.*
import java.awt.SystemColor.window



class RoomBillingView(room : Room) : Fragment()
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
    private var billingInfo : MutableList<String>? = null


    override val root =  rootFXML

    val room = room

    init{

        println("RoomBilling Room Number: ${room.roomNumber}")




        //add textField validation listeners
        firstNameTextField.textProperty().addListener { obs,o,n ->  validateText(obs,o,n,firstNameTextField,"Name") }
        lastnameTextField.textProperty().addListener { obs,o,n ->  validateText(obs,o,n,lastnameTextField,"Name")}
        cityTextField.textProperty().addListener { obs,o,n ->  validateText(obs,o,n,cityTextField,"City")}
        stateTextField.textProperty().addListener { obs,o,n ->  validateText(obs,o,n,stateTextField,"State")}
        zipcodeTextField.textProperty().addListener { obs,o,n ->  validateText(obs,o,n,zipcodeTextField,"ZipCode")}
        firstFourCCTextField.textProperty().addListener { obs,o,n ->  validateText(obs,o,n,firstFourCCTextField,"CC")}
        secondFourCCTextField.textProperty().addListener { obs,o,n ->  validateText(obs,o,n,secondFourCCTextField,"CC")}
        thirdFourCCTextField.textProperty().addListener { obs,o,n ->  validateText(obs,o,n,thirdFourCCTextField,"CC")}
        lastFourCCTextField.textProperty().addListener { obs,o,n ->  validateText(obs,o,n,lastFourCCTextField,"CC")}
        secCodeTextField.textProperty().addListener { obs,o,n ->  validateText(obs,o,n,secCodeTextField,"SEC")}

        monthComboBox.items = listOf(1,2,3,4,5,6,7,8,9,12,11,12).observable()
        yearComboBox.items = listOf(2018,2019,2020,2021,2022,2023).observable()
        cardTypeComboBox.items = listOf("VISA", "MASTERCARD","AMX").observable()

        proceedBillingBtn.setOnMouseClicked {
            //list values
            //0 -> descriptionBlock
            //1 -> notesBlock
            //2 -> descrLineItem
            //3 -> Length
            //4 -> Rate
            //5 -> Amount
            //6 -> Billing First Name
            //7 -> Billing Last Name
            //8 -> Address 1
            //9 -> Address 2
            //10 -> Billing City
            //11 -> Billing State
            //12 -> Billing ZipCode
            //13 -> first4CC
            //14 -> second4CC
            //15 -> third4CC
            //16 -> last4CC
            //17 -> SEC_CC
            //18 -> exprMonth
            //19 -> exprYr
            //20 -> cardType

            billingInfo!!.add(firstNameTextField.text)
            billingInfo!!.add(lastnameTextField.text)
            billingInfo!!.add(addressOneTextField.text)
            billingInfo!!.add(addressTwoTextField.text)
            billingInfo!!.add(cityTextField.text)
            billingInfo!!.add(stateTextField.text)
            billingInfo!!.add(zipcodeTextField.text)
            billingInfo!!.add(firstFourCCTextField.text)
            billingInfo!!.add(secondFourCCTextField.text)
            billingInfo!!.add(thirdFourCCTextField.text)
            billingInfo!!.add(lastFourCCTextField.text)
            billingInfo!!.add(secCodeTextField.text)
            billingInfo!!.add("${monthComboBox.value}")
            billingInfo!!.add("${yearComboBox.value}")
            billingInfo!!.add("${cardTypeComboBox.value}")



            val finalBillView = FinalBilling(billingInfo!!)
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


                billingInfo = getBillingInfoForRoom(room)

                //change \n -> <br> for HTML compliance, else will throw exception
                billingInfo!![1] = parseNotesToHTML(billingInfo!![1])
                billingWebView.engine.executeScript("bill.setDocumentTitle(\"Reservation Summary\")")
                billingWebView.engine.executeScript("bill.setRoomDescriptionTextBlock(\"${billingInfo!![0]}\")")
                billingWebView.engine.executeScript("bill.setNotesTextBlock(\"${billingInfo!![1]}\")")
                billingWebView.engine.executeScript("bill.addRowToTable(\"${billingInfo!![2]}\", \"${billingInfo!![3]}\", \"${billingInfo!![4]}\", \"${billingInfo!![5]}\")")
                billingWebView.engine.executeScript("bill.addRowToTable(\"Taxes\", \"\", \"\", \"$12.34\")")

                //hide the final out billing table
                billingWebView.engine.executeScript("bill.showfinalOutBillingTable(false)")

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

    fun getBillingInfoForRoom(forRoom:Room) : MutableList<String>
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


    //validation
    fun validateText(observable: ObservableValue<out String>, oldValue:String, newValue:String, textField: TextField,isWhat : String )
    {

        when(isWhat)
        {
            "Name" -> {
                val regExpr = Regex("([a-z]|[A-Z])*")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)
            }

            "City" -> {
                //allow for letters and 1 space
                val regExpr = Regex("([a-z]|[A-Z])*\\s?([a-z]|[A-Z])*")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)
            }
            "State" -> {

                //Allow for 2 capital letters, between 0 and 2 occurrences
                val regExpr = Regex("([A-Z]){0,2}")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)
            }
            "ZipCode" -> {
                //matches: 12345 or 12345-12345 (is forgiving, does not check if : 12345-12)
                val regExpr = Regex("^([0-9]{0,5})(-[0-9]{0,5})?\$")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)

            }
            "CC" -> {
                //matches: 4 digit number for credit card partial
                val regExpr = Regex("([0-9]{0,4})")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)

            }
            "SEC" ->{
                //matches: up to 12345, for credit card security code
                val regExpr = Regex("([0-9]{0,5})")
                val bool = regExpr.matches(newValue)

                println("textfield changed from $oldValue to $newValue regexBool: $bool")

                if (bool) textField.textProperty().set(newValue) else textField.textProperty().set(oldValue)

            }
            else -> System.out.println(ConsoleColors.yellowText("ERROR: The validation type: \"isWhat\" " +
                    "param: $isWhat is not accounted for in this when (switch) statement. \n " +
                    "See validateText() in Class: RoomBillingView)"))
            }

        }






}