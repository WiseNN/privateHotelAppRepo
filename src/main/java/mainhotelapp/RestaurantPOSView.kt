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
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import tornadofx.*

class RestaurantPOSView(parentView : MyButtonBarView) : View()
{
    val restuarantPOS: VBox by fxml("/fxml/RestuarantPosUI.fxml")
//    val sideBarBtn : Button by fxid()
    val sideBar : VBox by fxid()
//    val menuBtn : Button by fxid()
    val menuRow : HBox by fxid()
    val itemsVbox : VBox by fxid()




    override val root = restuarantPOS
    var menuDB : ObservableMap<String, Any> = (HashMap<String, Object>()).observable()



    init{




        menuDB.put("menu", RestaurantItem().getDeserializedMenu(DB().readDocInDB(DBNames.restaurantMenu)))

        menuDB.addListener(MapChangeListener {
            println("changed!!")
            layoutMenu()
        })



//        layoutMenu()





//        menuDB.forEach { key, value ->
//
//            val myBtn = Button()
//            myBtn.text = key
//
//            val itemsList = value as ArrayList<RestaurantItem>
//            itemsList.forEach {
//
//            }
//
//        }



        //used to layout the items in the menu








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