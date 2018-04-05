package mainhotelapp

import couchdb.DB
import couchdb.DBNames
import couchdb.RestaurantItem
import devutil.ConsoleColors
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.Text
import sun.plugin.javascript.navig.Anchor
import tornadofx.*
import java.awt.Image
import java.util.*
import javax.swing.text.html.ImageView

class RestaurantPOSView(parentView : MyButtonBarView) : View()
{
    val restuarantPOS: VBox by fxml("/fxml/RestuarantPosUI.fxml")
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
    var orderList : ObservableList<Any>? = null
    val orderIDTextField : TextField by fxid()



    override val root = restuarantPOS
    var menuDB : ObservableMap<String, Any> = (HashMap<String, Object>()).observable()

    init{


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

            //when killOrder is clicked, hide btn and disable orderId textField, and viewStackPane, and dump orderList
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

                orderList = null
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
                    if(orderList == null)
                    {
                        orderList = mutableListOf<RestaurantItem>().observable()
                        orderList!!.add(0,orderIDTextField.text)

                    }else{
                        //else if list exists, replace the orderID
                        if(orderIDTextField.text != "" || orderIDTextField.text != null)
                        {
                            orderList!!.remove(0,0)
                            orderList!!.add(0, orderIDTextField.text)
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
        val editView = EditOrderPOSView(item!!.name,this)

        //pass stickyNote
        editView.stickyNote = miniStickyIcon
        viewStackPane.add(editView.root)
        editView.root.hide()


        keyboardImgView.setOnMouseClicked {
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
                            createScreenRow(item!!)
                            if(orderList !=null) orderList!!.add(item) else System.out.println(ConsoleColors.yellowText("orderList does not exist!"))

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