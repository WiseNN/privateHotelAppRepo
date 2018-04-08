package mainhotelapp

import couchdb.DB
import couchdb.DBNames
import couchdb.RestaurantItem
import devutil.ConsoleColors
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
import javafx.util.Duration


class RestaurantPOSView(parentView : MyButtonBarView) : View()
{
    val restuarantPOS: StackPane by fxml("/fxml/RestuarantPosUI.fxml")
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



    override val root = restuarantPOS
    var menuDB : ObservableMap<String, Any> = (HashMap<String, Object>()).observable()

    init{

        //get animator translation distances for show/ hiding
        val yAnimatorDistance = -kotsLoginPanel.parentToLocal(kotsLoginPanel.boundsInParent.maxX,kotsLoginPanel.boundsInParent.maxY).y-120
        val yAnimatorDistance2 = +kotsLoginPanel.parentToLocal(kotsLoginPanel.boundsInParent.maxX,kotsLoginPanel.boundsInParent.maxY).y+120
        //translate login dialog out of view
        kotsLoginPanel.translateYProperty().set(yAnimatorDistance)
//        //hide loginAnchorPane
        loginAnchorPane.visibleProperty().set(false)




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

            //make newOrderBtn visible
            newOrderBtn.visibleProperty().set(true)

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



            root.vgrow = Priority.ALWAYS
            menuDB.put("menu", RestaurantItem().getDeserializedMenu(DB().readDocInDB(DBNames.restaurantMenu)))

            menuDB.addListener(MapChangeListener {
                println("changed!!")
                layoutMenu()
            })

            outputScreen.getChildList()!!.clear()


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


        clipRect.yProperty().set(51.0)

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
        val editView = EditOrderPOSView(this)

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


                        val itemBtn = Button()


                        itemBtn.text = item.name
                        itemBtn.id = "myBtn"
                        itemBtn.setOnMousePressed {
                            val menuMap = menuDB["menu"] as Map<String, Any>

                            val itemsList = menuMap[category] as ArrayList<RestaurantItem>
                            val item = itemsList.find { it.name == item.name }


                            //this will fail silently, we need to report that if the list is not created show an alert view to the user
                            if(orderListItems !=null)
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