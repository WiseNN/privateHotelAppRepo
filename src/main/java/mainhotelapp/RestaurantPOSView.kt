package mainhotelapp

import couchdb.DB
import couchdb.DBNames
import couchdb.RestaurantItem
import devutil.ConsoleColors
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
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






    override val root = restuarantPOS
    var menuDB : ObservableMap<String, Any> = (HashMap<String, Object>()).observable()



    init{




            menuDB.put("menu", RestaurantItem().getDeserializedMenu(DB().readDocInDB(DBNames.restaurantMenu)))

            menuDB.addListener(MapChangeListener {
                println("changed!!")
                layoutMenu()
            })

            outputScreen.getChildList()!!.clear()


        }

    fun createScreenRow(inCategory: String,forBtn: String)
    {
        val menuMap = menuDB["menu"] as Map<String, Any>

        val itemsList = menuMap[inCategory] as ArrayList<RestaurantItem>
        val item = itemsList.find { it.name == forBtn }
        println("found item: ${item.toString()}")

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


        exitImg.image = rowExitImg.image
        exitImg.fitWidth = rowExitImg.fitWidth
        exitImg.fitHeight = rowExitImg.fitHeight
        exitImg.setOnMouseClicked {

            if(outputScreen.getChildList() != null)
            {
//                val index = ((it.source as javafx.scene.image.ImageView).parent as HBox).id.toInt()
                val index = ((it.source as javafx.scene.image.ImageView).parent as HBox).indexInParent
                outputScreen.getChildList()!!.remove(outputScreen.getChildList()!![index])
            }
        }

        keyboardImgView.image = rowKeyboardImg.image
        keyboardImgView.fitWidth = rowKeyboardImg.fitWidth
        keyboardImgView.fitHeight = rowKeyboardImg.fitHeight

        keyboardImgView.setOnMouseClicked {
            val editView = EditOrderPOSView(this)
            viewStackPane.add(editView)

        }


        row.children.addAll(label,exitImg,keyboardImgView)

        outputScreen.add(row)

    }
    fun generateLabelText(itemName: String, itemPrice: Double) : String
    {
        var ellipseString = ""

        for(i in 0..100)
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
                            createScreenRow(category,item.name)
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