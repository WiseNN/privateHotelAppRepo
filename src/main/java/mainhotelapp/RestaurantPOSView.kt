package mainhotelapp

import couchdb.DB
import couchdb.DBNames
import couchdb.RestaurantItem
import devutil.ConsoleColors
import devutil.MyUtil
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.Text
import tornadofx.*
import java.util.*
import javafx.animation.TranslateTransition
import javafx.scene.Node
import javafx.scene.shape.Rectangle
import javafx.stage.StageStyle
import javafx.util.Duration

import javafx.animation.FadeTransition
import javafx.geometry.Insets
import javafx.scene.control.ListView
import javafx.scene.image.ImageView
import kots.EventNames
import kots.IP_AddressView
import kots.KOTS_EmployeeManager
import kots.KOTS_Order
import org.json.JSONObject


class RestaurantPOSView(parentView : MyButtonBarView) : View()
{

    //utility enum/static members (objects)
    object orderConstants{
        val E_ID = "E-ID"
    }

    val restuarantPOS: StackPane by fxml("/fxml/RestuarantPosUI.fxml")
    val editView = EditOrderPOSView(this)
//    val sideBarBtn : Button by fxid()
    val sideBar : VBox by fxid()
//    val menuBtn : Button by fxid()
    val menuRow : HBox by fxid()
    val itemsVbox : VBox by fxid()
    val rowItem : HBox by fxid()
    val rowLabel : Label by fxid()
    val rowExitImg : javafx.scene.image.ImageView by fxid()
    val rowKeyboardImg : javafx.scene.image.ImageView by fxid()
    val outputScreen : VBox by fxid()
    val viewStackPane : StackPane by fxid()
    val miniStickyImgView : javafx.scene.image.ImageView by fxid()
    val newOrderBtn : Button by fxid()
    val killOrderBtn : Button by fxid()
    var orderListItems: ObservableList<Any>? = null
    val orderIDTextField : TextField by fxid()
    val kotsLoginPanel : AnchorPane by fxid("kotsLoginPanel")
    val loginAnchorPane: AnchorPane by fxid()
    val eidLabel : Label by fxid()
    val posVBox : VBox by fxid()
    val loginBtn : Button by fxid()
    val usernameTextField : TextField by fxid()
    val passwordTextField : TextField by fxid()
    val employeeIdLabel : Label by fxid()
    val adminFlagImg : ImageView by fxid()


    //ordersOverlayView
    val ordersOverlayAnchorPaneContainer : AnchorPane by fxid()
    val ordersOverlayHBoxView : HBox by fxid()
    val ordersListVBox : VBox by fxid()
    val ordersListRowHBox : HBox by fxid()
    val orderInfoLabel : Label by fxid()
    val stopOrderBtn : ImageView by fxid()
    val resolveStopOrderBtn : ImageView by fxid()
    val ordersViewSlider : Button by fxid()
    val payoutOrderListScreen : ListView<String> by fxid()

    //orders observable list
    var KOTS_OrdersList = mutableListOf<KOTS_Order>().observable()




    //retain IP Address view instance
    val ipAddressView = IP_AddressView(this)

    //socketIO Client
    var socket : Socket? = null

    //get animator translation distances for show/ hiding
    val yAnimatorDistance = -kotsLoginPanel.parentToLocal(kotsLoginPanel.boundsInParent.maxX,kotsLoginPanel.boundsInParent.maxY).y-125
    val yAnimatorDistance2 = +kotsLoginPanel.parentToLocal(kotsLoginPanel.boundsInParent.maxX,kotsLoginPanel.boundsInParent.maxY).y+125







    override val root = restuarantPOS
    var menuDB : ObservableMap<String, Any> = (HashMap<String, Object>()).observable()

    init{



        //everytime the screen changes update us
        outputScreen.getChildList().toProperty().addListener(ChangeListener { observable, oldValue, newValue ->

            slideOverlayOrderPaneOffScreen()

            if(newValue.size <= 0)
            {

            }

        })



        //place posVbox in last index, will end up as next to last because kotslogin panel
        //will be in front of it, this is done by design
        root.getChildList()!!.move(posVBox,root.getChildList()!!.lastIndex)

        //show loginAnchorPane without animation on initial load, should be called after posVbox is moved to last index
        //eidLabel should be called later in the init stage to avoid a race condition (currently @ bottom of init)
        animateShow(kotsLoginPanel,1,0.0,yAnimatorDistance2)

        //initialize employeeID text label
        employeeIdLabel.text = "--"
        employeeIdLabel.textProperty().addListener(ChangeListener { observable, oldValue, newValue ->

            val isAdmin = KOTS_EmployeeManager().isAdminUser(newValue)
            //if user is admin user, animate flag into display
            if( (newValue != "" || newValue != "--") && isAdmin)
            {

//                adminFlagImg.opacity = 0.0
                adminFlagImg.visibleProperty().set(true)

                //blink animation for flag
                FadeTransition().apply {
                    fromValue = 0.0
                    toValue = 1.0
                    duration = 2.seconds
                    isAutoReverse = true
                    cycleCount = 5
                    play()

                    setOnFinished {
                        FadeTransition().apply {
                            fromValue = 0.0
                            toValue = 1.0
                            duration = 2.seconds
                            isAutoReverse = false
                            cycleCount = 1
                            play()
                        }
                    }
                }


            }


        })
        //set padding for payout overlay slide view
        payoutOrderListScreen.padding = Insets(10.0,10.0,10.0,10.0)

        //disable, hide ordersViewSlider
        ordersViewSlider.disableProperty().set(true)
        ordersViewSlider.visibleProperty().set(false)

        //hide admin flag onScreen
        adminFlagImg.visibleProperty().set(false)








        //disable eidLabel
        eidLabel.disableProperty().set(true)


//        loginAnchorPane.visibleProperty().set(false)


        //clear ordersVBox prototype HBox
        ordersListVBox.getChildList()!!.clear()



        loginBtn.setOnMouseClicked {


            if((usernameTextField.text != "" || usernameTextField.text != null) &&
                    (passwordTextField.text != "" || passwordTextField.text != null))
            {

                ipAddressView.openWindow(stageStyle = StageStyle.UTILITY)
            }
        }

        eidLabel.setOnMouseClicked {
            println("can see: ${loginAnchorPane.visibleProperty().get()}")
            val cycleCount = 1
            val duration = 0.5
            if(loginAnchorPane.visibleProperty().get() && eidLabel.disableProperty().value != true)
            {
                eidLabel.disableProperty().set(true)
                animateHide(kotsLoginPanel,cycleCount,duration,yAnimatorDistance)


            }
            else if(eidLabel.disableProperty().value != true)
            {
                eidLabel.disableProperty().set(true)
                loginAnchorPane.visibleProperty().set(true)
                animateShow(kotsLoginPanel,cycleCount,duration,yAnimatorDistance2)

            }

        }





            //disable viewStackPane when the newOrder Button is visible, enable when the visibleProperty is not visible
            viewStackPane.disableProperty().set(true)

            //disable orderID TextField, when new orderBtn is pressed enable textfield
            orderIDTextField.disableProperty().set(true)

            //make newOrderBtn invisible
            newOrderBtn.visibleProperty().set(false)

            //when newOrder is clicked, hide btn and enable orderId textField
            newOrderBtn.setOnMouseClicked {
                //hide this
                newOrderBtn.visibleProperty().set(false)
                //show killOrder Btn
                killOrderBtn.visibleProperty().set(true)

                //enable orderTextField
                orderIDTextField.disableProperty().set(false)

            }

            //hide killOrderBtn
            killOrderBtn.visibleProperty().set(false)

            //when killOrder is clicked, hide btn and disable orderId textField, and viewStackPane, and dump orderListItems
            killOrderBtn.setOnMouseClicked {
                //hide this
                killOrderBtn.visibleProperty().set(false)
                //show newOrder Btn
                newOrderBtn.visibleProperty().set(true)

                //disable & clear textField
                orderIDTextField.text = ""
                orderIDTextField.disableProperty().set(true)

                //disable viewStackPane
                viewStackPane.disableProperty().set(true)

                //clear the orderListItems
                orderListItems = null

                //clear the outputScreen orderItems
                if(outputScreen.getChildList() != null)
                {
                    outputScreen.getChildList()!!.clear()
                }
            }


            //On orderIDTextField press Enter, we will create a new Order List with ID @ index: 0,
            //1. create an intelligent way to change the order name
            //-> by checking on enter if a list exists, if not we will create one, if so, we will change index: 0
            //-> to the new orderID
            //2. enable the viewStackPane
            orderIDTextField.setOnKeyPressed {

                println("Value: "+it.code.toString())
                if(it.code.toString() == "ENTER" && (orderIDTextField.text != "" || orderIDTextField.text != null))
                {
                    //check if order list exist...
                    if(orderListItems == null)
                    {

                        orderListItems = mutableListOf<RestaurantItem>().observable()
                        orderListItems!!.add(0,orderIDTextField.text)

                    }else{
                        //else if list exists, replace the orderID
                        if(orderIDTextField.text != "" || orderIDTextField.text != null)
                        {
                            orderListItems!!.remove(0,0)
                            orderListItems!!.add(0, orderIDTextField.text)
                        }

                    }
                    viewStackPane.disableProperty().set(false)
                }

            }


        root.layout()
        menuDB.addListener(MapChangeListener {
            println("changed!!")
            layoutMenu()
        })
            root.vgrow = Priority.ALWAYS
            menuDB.put("menu", RestaurantItem().getDeserializedMenu(DB().readDocInDB(DBNames.restaurantMenu)))



            outputScreen.getChildList()!!.clear()


        }

    fun slideOverlayOrderPaneOffScreen()
    {

    }


    fun createSlideOrdersRow(withOrderID : String, isStopOrder: Boolean)
    {

        var row = HBox()

        val label = Label()
        val stopOrder = javafx.scene.image.ImageView()
        val resolvedStopOrder = javafx.scene.image.ImageView()
        val keyboardImgView = javafx.scene.image.ImageView()


        row.prefHeight = ordersListRowHBox.prefHeight
        row.prefWidth = ordersListRowHBox.prefWidth
        row.maxHeight = ordersListRowHBox.maxHeight
        row.maxWidth = ordersListRowHBox.maxWidth
        row.minHeight = ordersListRowHBox.minHeight
        row.minWidth = ordersListRowHBox.minWidth
        row.spacing = ordersListRowHBox.spacing
        row.hgrow = ordersListRowHBox.hgrow
        row.scaleX = ordersListRowHBox.scaleX
        row.scaleY = ordersListRowHBox.scaleY
        row.padding = ordersListRowHBox.padding
        row.styleClass.addAll(ordersListRowHBox.styleClass)




        label.prefHeight = orderInfoLabel.prefHeight
        label.prefWidth = orderInfoLabel.prefWidth
        label.maxWidth = orderInfoLabel.maxWidth
        label.maxHeight = orderInfoLabel.height
        label.minHeight = orderInfoLabel.minHeight
        label.minWidth = orderInfoLabel.minWidth
        label.styleClass.addAll(orderInfoLabel.styleClass)
        label.textAlignment = orderInfoLabel.textAlignment
        label.textOverrun = orderInfoLabel.textOverrun
        label.ellipsisString = orderInfoLabel.ellipsisString
        label.lineSpacing = orderInfoLabel.lineSpacing
        label.hgrow = orderInfoLabel.hgrow
        label.padding = orderInfoLabel.padding
        label.text = orderInfoLabel.text
        label.fontProperty().bind(orderInfoLabel.fontProperty())

        label.setOnMouseClicked {

            val index = ((it.source as javafx.scene.image.ImageView).parent as HBox).indexInParent

            val displayOrderList = mutableListOf("").observable()

            val kotsOrder = KOTS_OrdersList[index]

            displayOrderList.add("Order ID: "+kotsOrder.orderID)
            displayOrderList.add("Created: "+kotsOrder.creationTime)


            if(kotsOrder.isStopOrder)
            {
                displayOrderList.add("Status: STOP ORDER")

                if(!kotsOrder.isStopOrderResolved)
                {
                    displayOrderList.add("Resolution Status: Needs Resolution")
                }else{
                    displayOrderList.add("Resolution Status: Resolved")
                }
            }else{
                displayOrderList.add("Status: Enqueued")
            }

            displayOrderList.add("Menu Items: ")

            kotsOrder.itemsList.forEachIndexed { index, restaurantItem ->

                displayOrderList.add("$index) ${restaurantItem.name}  -- $${restaurantItem.price}")

            }

            displayOrderList.add("TOTAL: ${kotsOrder.total}")

            payoutOrderListScreen.items = displayOrderList
        }


        //create removeIcon
        stopOrder.image = stopOrderBtn.image
        stopOrder.fitWidth = stopOrderBtn.fitWidth
        stopOrder.fitHeight = stopOrderBtn.fitHeight
        stopOrder.translateX = stopOrderBtn.translateX
        stopOrder.translateY = stopOrderBtn.translateY
        stopOrder.isPreserveRatio = true
        stopOrder.isSmooth = true

        //send stop order result back to server
        resolveStopOrderBtn.setOnMouseClicked {


            hbox {
             button {
                 text = "Cancel"
                setOnMouseClicked {

                    val index = ((it.source as javafx.scene.image.ImageView).parent as HBox).indexInParent
                    //remove list KOTSOrder from KOTS_ORDERS list
                    KOTS_OrdersList.remove(index,index+1)
                    //remove KOTSOrder from the View stack because of cancel
                    ordersListVBox.getChildList()!!.remove(ordersListVBox.getChildList()!![index])
                    ordersListVBox.getChildList()!!.remove(this@hbox)
                }
             }
               button { text = "Re-Submit Order"
                //call create KOTS_ORDER again

                   setOnMouseClicked {

                       val index = ((it.source as javafx.scene.image.ImageView).parent as HBox).indexInParent

                       //re-attempt to add KOTS Order to the Queue
                       addKOTS_OrderToQueue(KOTS_OrdersList[index])
                       //remove KOTSOrder from the View stack because resubmitting will create a new row
                       ordersListVBox.getChildList()!!.remove(this@hbox)
                   }
               }

                ordersListVBox.add(this)
            }
            socket!!.emit(EventNames.requestClientSignOn, usernameTextField.text, Ack{
                arrayOfAnys ->

            })
        }


        //create removeIcon
        resolvedStopOrder.image = resolveStopOrderBtn.image
        resolvedStopOrder.fitWidth = resolveStopOrderBtn.fitWidth
        resolvedStopOrder.fitHeight = resolveStopOrderBtn.fitHeight
        resolvedStopOrder.translateX = resolveStopOrderBtn.translateX
        resolvedStopOrder.translateY = resolveStopOrderBtn.translateY
        resolvedStopOrder.isPreserveRatio = true
        resolvedStopOrder.isSmooth = true


        row.add(label)
        row.add(stopOrder)
        row.add(resolvedStopOrder)

        runAsync { ui { ordersListVBox.add(row) } }

    }

    fun connectToServer(ipAddress : String)
    {
        socket = IO.socket("$ipAddress")


//        socket!!.on(Socket.EVENT_DISCONNECT, object : Emitter.Listener {
//
//            override fun call(vararg args: Any) {}
//
//        })
                socket!!.on(Socket.EVENT_ERROR, object : Emitter.Listener {

            override fun call(vararg args: Any) {
                System.out.checkError()
                println("ERROR: $args")
            }

        })


        socket!!.connect()
        socket!!.open()

//        socket!!.emit(Socket.EVENT_CONNECT, "Hello!")



//       socket!!.emit(EventNames.requestClientSignOn, usernameTextField.text)

//        val d = AckCallback("")
        socket!!.emit(EventNames.requestClientSignOn, usernameTextField.text, Ack{
                arrayOfAnys ->

            println("response: "+arrayOfAnys[0])

            //cast value to string from server, hope for encrypted password
            val encryptedPassword = arrayOfAnys[0] as String


            when(encryptedPassword)
            {
                "no user" -> {
                    //if no user, show a message telling user that username doesnt exist, shutdown and disconnect from server


                }

                else -> {

                    val result = encryptedPassword!!.split("OR")
                    val isMatch = passwordTextField.text == dataProcessing.Encryption3().decryptValue("decrypt", result[0],result[1])

                    if(isMatch)
                    {
                        socket!!.emit(EventNames.signOn, usernameTextField.text, Ack{
                            arrayOfAnys ->

                            //get signed in bool, if true enable UI
                            val isSignedIn = arrayOfAnys[0] as Boolean
                            if(isSignedIn)
                            {

                                //instantiate KOTS_Orders lists
                                KOTS_OrdersList = mutableListOf<KOTS_Order>().observable()


                                val cycleCount = 1
                                val duration = 0.5
                                runAsync {
                                    ui {
                                        newOrderBtn.visibleProperty().set(true)
                                        employeeIdLabel.text = usernameTextField.text
                                        usernameTextField.text = ""
                                        passwordTextField.text = ""
                                        loginBtn.text = "Logout"

                                    }
                                }

                                //sign out of KOTS Server
                                loginBtn.setOnMouseClicked { signOut() }
                                animateHide(kotsLoginPanel,cycleCount,duration,yAnimatorDistance)
                                //enable new order button
                                newOrderBtn.disableProperty().set(false)

                                //enable,show ordersViewSlider
                                ordersViewSlider.disableProperty().set(false)
                                ordersViewSlider.visibleProperty().set(true)

                                //attach ordersViewSlider listener to send order events to the server
                                ordersViewSlider.setOnMouseClicked {
                                    if(it.clickCount == 3)
                                    {
                                        //create the an original kots order from the orderItemsList
                                        val newKOTS_Order = createKOTS_Order()

                                        //if null return from click listener
                                        if(newKOTS_Order == null) {return@setOnMouseClicked}

                                        addKOTS_OrderToQueue(newKOTS_Order)



                                    }
                                }
                            }else{
                                println(ConsoleColors.yellowText(("ERROR: 500, PLEASE CONTACT IT SUPPORT IMMEDIATELY")))
                            }
                        })



                    }else{

                        val response = "This password does not match our records"
                        println(response)

                    }



                }
            }


        })



    }

    fun addKOTS_OrderToQueue(newKOTS_Order : KOTS_Order)
    {
        //serialize order before sending
        val serializedKOTS_ORDER = MyUtil().serializeObject(newKOTS_Order)

        var response = ""
        //call server with a new KOTS_Order
        socket!!.emit(EventNames.addOrder, serializedKOTS_ORDER, Ack{
            arrayOfAnys ->
            when(arrayOfAnys[0] as Int)
            {
                500 -> {
                    response = "Sorry, we are having internal server errors"
                    newKOTS_Order.isStopOrder = true
                }
                200 -> {
                    response = "Great Order has been added!"
                    newKOTS_Order.isStopOrder = false
                }
                400 -> {
                    response = "Please report to the Kitchen for further assistence"
                    newKOTS_Order.isStopOrder = true
                }
            }


            //add new order to list of stop orders
            KOTS_OrdersList.add(newKOTS_Order)

            //onAdd, update ordersListVBox
            createSlideOrdersRow(newKOTS_Order.orderID,newKOTS_Order.isStopOrder)

        })
    }


    fun createKOTS_Order() : KOTS_Order?
    {
        val jsonObj = JSONObject()
        if(orderListItems != null)
        {
            //put E-ID at first index

            val orderId = orderListItems!![0] as String


            val restuarantItemsList = orderListItems!!.toList().subList(1,orderListItems!!.toList().lastIndex) as List<RestaurantItem>
            val list = mutableListOf<RestaurantItem>()
            list.addAll(restuarantItemsList)

            val newKOTS_Order = KOTS_Order(employeeIdLabel.text, orderId, list)

            return  newKOTS_Order

        }else{
            System.out.println(ConsoleColors.redText("PLEASE DO NOT TRY TO SEND NULL OBJECTS TO THE SERVER!"))
            return  null
        }

    }

    fun signOut()
    {
        socket!!.disconnect()

        //disable, hide ordersViewSlider
        ordersViewSlider.disableProperty().set(true)
        ordersViewSlider.visibleProperty().set(false)

        //hide kill order Btn
        killOrderBtn.visibleProperty().set(false)
        //hide show order Btn
        newOrderBtn.visibleProperty().set(false)

        //disable & clear textField
        orderIDTextField.text = ""

        //clear eid label
        employeeIdLabel.text = "--"
        orderIDTextField.disableProperty().set(true)

        //disable viewStackPane
        viewStackPane.disableProperty().set(true)

        //clear the orderListItems
        orderListItems = null




        //clear the outputScreen orderItems
        if(outputScreen.getChildList() != null)
        {
            outputScreen.getChildList()!!.clear()
        }


        //reset login button text
        loginBtn.text = "Login"

        loginBtn.setOnMouseClicked {


            if((usernameTextField.text != "" || usernameTextField.text != null) &&
                    (passwordTextField.text != "" || passwordTextField.text != null))
            {

                ipAddressView.openWindow(stageStyle = StageStyle.UTILITY)
            }
        }





        //dont hide on signOut, disable orderUI
//        val cycleCount = 1
//        val duration = 0.5
//        animateHide(kotsLoginPanel,cycleCount,duration,yAnimatorDistance)



    }

    fun flashLoginTextNotifier(forNode: Label, state:String)
    {
        val fadeTrans = FadeTransition()
        fadeTrans.node = forNode
        fadeTrans.fromValue = 0.0
        fadeTrans.toValue = 1.0
        fadeTrans.duration = 2.seconds

        when(state)
        {
            "noAuth" -> {
                fadeTrans.setOnFinished {
                    //disconnect from server, & disable UI
                }
            }

            "hasAccess" -> {
                //slide login Dialog up and enable UI (@ create Order level)
            }



        }


    }
    fun animateHide(forNode : Node, myCycleCount : Int, myDuration : Double,animatorDistance : Double )
    {
        val clipRect = Rectangle(kotsLoginPanel.width, kotsLoginPanel.height)

        // changes according pane's width and height
        clipRect.heightProperty().bind(kotsLoginPanel.heightProperty().plus(50))
        clipRect.widthProperty().bind(kotsLoginPanel.widthProperty().plus(50))


        clipRect.yProperty().set(51.0)

        // set rect as clip rect
        loginAnchorPane.clip = clipRect

        val tr = TranslateTransition()

        tr.setOnFinished {
            root.getChildList()!!.move(posVBox,root.getChildList()!!.lastIndex)
            loginAnchorPane.visibleProperty().set(false)
            eidLabel.disableProperty().set(false)
            println("removed dialog from focus view")
        }
        tr.node = kotsLoginPanel
        tr.byY = animatorDistance
        tr.duration = Duration.seconds(myDuration)
        tr.cycleCount = myCycleCount
        tr.play()
    }

    fun animateShow(forNode : Node, myCycleCount : Int, myDuration : Double, animatorDistance: Double)
    {

        //move the login dialog to the front of the view stack
        root.getChildList()!!.move(loginAnchorPane,root.getChildList()!!.lastIndex)

        val clipRect = Rectangle(kotsLoginPanel.width, kotsLoginPanel.height)

        // changes according pane's width and height
        clipRect.heightProperty().bind(kotsLoginPanel.heightProperty().plus(50))
        clipRect.widthProperty().bind(kotsLoginPanel.widthProperty().plus(50))


        clipRect.yProperty().set(AnchorPane.getTopAnchor(forNode))

        // set rect as clip rect
        loginAnchorPane.clip = clipRect

        val tr = TranslateTransition()
        tr.setOnFinished {
            eidLabel.disableProperty().set(false)
        }
        tr.node = kotsLoginPanel
        tr.byY = animatorDistance
        tr.duration = Duration.seconds(myDuration)
        tr.cycleCount = myCycleCount
        tr.play()
    }


    fun submitOrder(toDestination: String)
    {
        if(orderIDTextField.text != null || orderIDTextField.text != "")
        {
            //send Order menu you to kitchen
            when(toDestination)
            {
                "kitchen" -> {
                    //we need to design the logic for the kitchen Screen
                }
                "payout" -> {
                    //send the order to the final out screen

                }

            }
        }

    }

    fun createScreenRow(item: RestaurantItem)
    {

        println("found item: $item")

        val row = HBox()

        val label = Label()
        val exitImg = javafx.scene.image.ImageView()
        val keyboardImgView = javafx.scene.image.ImageView()

        row.prefHeight = rowItem.prefHeight
        row.prefWidth = rowItem.prefWidth
        row.maxHeight = rowItem.maxHeight
        row.maxWidth = rowItem.maxWidth
        row.minHeight = rowItem.minHeight
        row.minWidth = rowItem.minWidth
        row.spacing = rowItem.spacing
        row.hgrow = rowItem.hgrow
        row.styleClass.addAll(rowItem.styleClass)



        label.prefHeight = rowLabel.prefHeight
        label.prefWidth = rowLabel.prefWidth
        label.maxWidth = rowLabel.maxWidth
        label.maxHeight = rowLabel.height
        label.minHeight = rowLabel.minHeight
        label.minWidth = rowLabel.minWidth
        label.styleClass.addAll(rowLabel.styleClass)
        label.textAlignment = rowLabel.textAlignment
        label.textOverrun = rowLabel.textOverrun
        label.ellipsisString = rowLabel.ellipsisString
        label.lineSpacing = rowLabel.lineSpacing
        label.text = generateLabelText(item!!.name,item!!.price)
        AnchorPane.setBottomAnchor(label,0.0)
        AnchorPane.setTopAnchor(label, 0.0)
        AnchorPane.setLeftAnchor(label,0.0)
        AnchorPane.setRightAnchor(label,0.0)

        //create mini-stickyNote ImageView, and add to anchoredPane
        val miniStickyIcon =javafx.scene.image.ImageView()
        miniStickyIcon.image = miniStickyImgView.image
        miniStickyIcon.isPreserveRatio = true
        miniStickyIcon.isSmooth = true
        miniStickyIcon.fitHeight = miniStickyImgView.fitHeight
        miniStickyIcon.fitWidth = miniStickyImgView.fitWidth

        //create textLabel with only name to get width -> place sticky note w/Padding
        //not adding to view
        val tempLabel = Text()
        tempLabel.styleClass.addAll(rowLabel.styleClass)
        tempLabel.fontProperty().set(Font("Avenir Book", 36.0))
        tempLabel.text = item!!.name
        val stickyPadding = 10.0
        val separationDistance = tempLabel.boundsInLocal.width+stickyPadding

        miniStickyIcon.layoutX = separationDistance
        miniStickyIcon.layoutY = miniStickyImgView.layoutY

        //hide miniSticky
        miniStickyIcon.isVisible = false


        //create removeIcon
        exitImg.image = rowExitImg.image
        exitImg.fitWidth = rowExitImg.fitWidth
        exitImg.fitHeight = rowExitImg.fitHeight
        exitImg.isPreserveRatio = true
        exitImg.isSmooth = true
        exitImg.setOnMouseClicked {

            if(outputScreen.getChildList() != null)
            {
//                val index = ((it.source as javafx.scene.image.ImageView).parent as HBox).id.toInt()
                val index = ((it.source as javafx.scene.image.ImageView).parent as HBox).indexInParent
                outputScreen.getChildList()!!.remove(outputScreen.getChildList()!![index])
            }
        }

        //create customizeOrder icon
        keyboardImgView.image = rowKeyboardImg.image
        keyboardImgView.fitWidth = rowKeyboardImg.fitWidth
        keyboardImgView.fitHeight = rowKeyboardImg.fitHeight
        keyboardImgView.isPreserveRatio = true
        keyboardImgView.isSmooth = true

        //create stackPane for Label, add label to stackPane
        val labelStackPane = StackPane()
        labelStackPane.maxWidth = Double.MAX_VALUE
        labelStackPane.add(label)

        //create anchorPane for stickyNote, place ontop of Label in stackPane (add stickyNote, then add anchorPane to stackPane)
        val anchoredLabelContainer = AnchorPane()
        anchoredLabelContainer.maxWidth = Double.MAX_VALUE
        anchoredLabelContainer.add(miniStickyIcon)


        labelStackPane.add(anchoredLabelContainer)

        //add all elements to the row (including stackPane) before displaying row
        row.children.addAll(labelStackPane,exitImg,keyboardImgView)


        //display the row
        outputScreen.add(row)

        //create customize orderView and hide it from user until keyboard icon is pressed


        //pass stickyNote
        editView.stickyNote = miniStickyIcon
        viewStackPane.add(editView.root)
        editView.root.hide()


        keyboardImgView.setOnMouseClicked {
            editView.dishNameLabel.text = item!!.name
            editView.root.show()
        }

    }
    fun generateLabelText(itemName: String, itemPrice: Double) : String
    {
        var ellipseString = ""

        for(i in 0..200)
        {
            ellipseString += "."
        }

        return "$itemName $ellipseString $$itemPrice"
    }

    fun layoutMenu()
    {
        val menuMap = menuDB["menu"] as Map<String, Any>

        if(sideBar.getChildList()!!.size > 0)
        {
            sideBar.getChildList()!!.clear()
        }

        menuMap.keys.forEach {

            val sideBarBtn = button {
                text = it
                println("stlye class: "+styleClass.add("sideBarBtn"))
            }

            sideBarBtn.setOnMouseClicked {
                itemsVbox.getChildList()!!.clear()
                val category = (it.source as Button).textProperty().get()
                val itemsList = menuMap[category] as? ArrayList<RestaurantItem>

                if(itemsList != null)
                {
                    var hBox = HBox()
                    hBox.prefWidth = 200.0
                    hBox.prefHeight = 100.0
                    hBox.maxWidth = Double.MAX_VALUE
                    hBox.maxHeight = Double.MAX_VALUE

                    var rows = itemsList.size / 5
                    var itemsLeftOver = itemsList.size % 5

                    if(rows >= 0 && itemsLeftOver > 0)
                    {
                        rows++
                    }


                    itemsList.forEachIndexed { index, item ->


                        //these are the blue menu item buttons
                        val itemBtn = Button()


                        itemBtn.text = item.name
                        itemBtn.id = "myBtn"
                        itemBtn.setOnMousePressed {

                            //get menu from map
                            val menuMap = menuDB["menu"] as Map<String, Any>


                            val itemsList = menuMap[category] as ArrayList<RestaurantItem>
                            val item = itemsList.find { it.name == item.name }


                            //this will fail silently, we need to report that if the list is not created show an alert view to the user
                            if(orderListItems != null)
                            {
                                orderListItems!!.add(item)
                                createScreenRow(item!!)
                                println("OrderList: "+orderListItems.toString())
                            }
                            else
                            {
                                System.out.println(ConsoleColors.yellowText("orderListItems does not exist!"))
                            }



                        }


                        //if this is a multiple of 5
                        if(index!=0 && index % 5 == 0)
                        {
                            itemsVbox.add(hBox)
                            hBox = HBox()
                            hBox.prefWidth = 200.0
                            hBox.prefHeight = 100.0
                            hBox.maxWidth = Double.MAX_VALUE
                            hBox.maxHeight = Double.MAX_VALUE
                        }


                        hBox.add(itemBtn)
                    }
                    if(itemsLeftOver > 0)
                    {
                        itemsVbox.add(hBox)
                    }
                }else{
                    System.out.println(ConsoleColors.yellowText("itemsList is NULL!! from: sideBarBtn Mouse Listener  Class: RestaurantPOSView"))
                }
            }
//            sideBar.replaceChildren(sideBarBtn)
            sideBar.add(sideBarBtn)
        }
    }



}