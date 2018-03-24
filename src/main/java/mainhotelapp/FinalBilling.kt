package mainhotelapp


import javafx.concurrent.Worker
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import netscape.javascript.JSObject
import tornadofx.*

class FinalBilling (billingInfoList : MutableList<String>): Fragment()
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




//                val compliant_HTML_Notes = RoomBillingView().parseNotesToHTML(finalBillingList!![1]);
                finalOutBillingWebView.engine.executeScript("bill.setDocumentTitle(\"Billing Summary\")")
                finalOutBillingWebView.engine.executeScript("bill.setRoomDescriptionTextBlock(\"${finalBillingList!![0]}\")")
                finalOutBillingWebView.engine.executeScript("bill.setNotesTextBlock(\"${billingInfoList!![1]}\")")
                finalOutBillingWebView.engine.executeScript("bill.addRowToTable(\"${finalBillingList!![2]}\", \"${finalBillingList!![3]}\", \"${finalBillingList!![4]}\", \"${finalBillingList!![5]}\")")
                finalOutBillingWebView.engine.executeScript("bill.addRowToTable(\"Taxes\", \"\", \"\", \"$12.34\")")
                finalOutBillingWebView.engine.executeScript("bill.createFinalOutBillingAddress(\"${finalBillingList!![6]} ${finalBillingList!![7]}\", " +
                        "\"${finalBillingList!![8]}\",\"${finalBillingList!![9]}\",\"${finalBillingList!![10]} ${finalBillingList!![11]} ${finalBillingList!![12]}\")")
                finalOutBillingWebView.engine.executeScript("  bill.createFinalOutBillingCardInfo(\"${finalBillingList!![6]} ${finalBillingList!![7]}\", \"**** **** **** ${finalBillingList[16]}\",\"${finalBillingList[18]}/${finalBillingList[19]}\",\"${finalBillingList!![20]}\")")

                // bill.showfinalOutBillingTable(false);

                //hide the final out billing table
//                billingWebView.engine.executeScript("bill.showfinalOutBillingTable(false)")

            }
        }
    }






}