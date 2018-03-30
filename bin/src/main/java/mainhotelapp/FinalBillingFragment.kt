package mainhotelapp


import javafx.concurrent.Worker
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import netscape.javascript.JSObject
import tornadofx.*
import java.time.LocalDate

class FinalBillingFragment(billingInfoList : MutableList<String>, lineItems : MutableList<MutableList<String>>): Fragment()
{
    private val rootFXML: VBox by fxml("/fxml/FinalOutBillingUI.fxml")
    override val root = rootFXML


    //list values
    //0 -> descriptionBlock
    //1 -> notesBlock
    //2 -> descrLineItem
    //3 -> Length
    //4 -> Rate
    //5 -> Amount
    private val finalOutBillingWebView : WebView by fxid()

    private var finalBillingList = billingInfoList
    private var finalOutLineItems = lineItems

    init {



        //get the billing Summary's URL
        val url = this.javaClass.getResource("/html/billing/BillingSummary.html")



        finalOutBillingWebView.engine.load(url.toString())




        finalOutBillingWebView.engine.loadWorker.stateProperty().onChange {



            println("web source data: "+it.toString())
            if(it == Worker.State.SUCCEEDED)
            {

                //when page loads, attach this java class to the window as an object
                val window = finalOutBillingWebView.engine.executeScript("window") as JSObject


                window.setMember("javaApp", this)

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



//                val compliant_HTML_Notes = ReservationSummaryFragment().parseNotesToHTML(finalBillingList!![1]);
                finalOutBillingWebView.engine.executeScript("bill.setDocumentTitle(\"Billing Summary\")")
                finalOutBillingWebView.engine.executeScript("bill.setDateText(\"${LocalDate.now()}\")")
                finalOutBillingWebView.engine.executeScript("bill.setRoomDescriptionTextBlock(\"${finalBillingList!![0]}\")")
                finalOutBillingWebView.engine.executeScript("bill.setReservationNumberText(\"${finalBillingList!![2]}\")")
                finalOutBillingWebView.engine.executeScript("bill.setNotesTextBlock(\"${billingInfoList!![1]}\")")
                finalOutBillingWebView.engine.executeScript("bill.createFinalOutBillingAddress(\"${finalBillingList!![3]} ${finalBillingList!![4]}\", " +
                        "\"${finalBillingList!![5]}\",\"${finalBillingList!![6]}\",\"${finalBillingList!![7]} ${finalBillingList!![8]} ${finalBillingList!![9]}\")")
                finalOutBillingWebView.engine.executeScript("  bill.createFinalOutBillingCardInfo(\"${finalBillingList!![3]} ${finalBillingList!![4]}\", \"**** **** **** ${finalBillingList[13]}\",\"${finalBillingList[15]}/${finalBillingList[16]}\",\"${finalBillingList!![17]}\")")

                finalOutLineItems.forEach {
                    finalOutBillingWebView.engine.executeScript("bill.addRowToTable(\"${it[0]}\", \"${it[1]}\", \"${it[2]}\", \"${it[3]}\")")
                }

                // bill.showfinalOutBillingTable(false);

                //hide the final out billing table
//                billingWebView.engine.executeScript("bill.showfinalOutBillingTable(false)")

            }
        }
    }






}