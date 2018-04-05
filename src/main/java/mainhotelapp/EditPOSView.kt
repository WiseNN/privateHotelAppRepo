package mainhotelapp

import couchdb.DB
import couchdb.DBNames
import couchdb.RestaurantItem
import devutil.ConsoleColors
import hotelbackend.HotelBackEndNorris
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*
import java.awt.event.ActionListener
import java.awt.event.KeyEvent

class EditPOSView(parentView : MyButtonBarView) : View()
{
    val editRestaurantPOS: VBox by fxml("/fxml/Edit_POS_Menu_UI.fxml")
    val editItemsToggleGroup : ToggleGroup by fxid()
    val categoryTextField : TextField by fxid()
    val itemTextField : TextField by fxid()
    val priceTextField : TextField by fxid()
    val submitBtn : Button by fxid()
    val screen : AnchorPane by fxid()


    //internal UI Elements
    val categoriesComboBox = ComboBox<String>()
    val itemsComboBox = ComboBox<String>()

    //internal data models
    var editMenuDB = (parentView.restuarantPOSView as RestaurantPOSView).menuDB
    var categoriesList : ObservableList<String>? = mutableListOf("").observable()
    var itemsList = mutableListOf("").observable()

//    var toggleState = (editItemsToggleGroup.selectedToggleProperty().get() as RadioButton).text
    var toggleState : String? = null

    //retain instance to RestaurantPOSView
//    val restaurantView =





    override val root = editRestaurantPOS

    init {
        root.vgrow = Priority.ALWAYS


//        categoryTextField.textProperty().addListener { observable, oldValue, newValue ->
//            UIUtil().validateText(observable,oldValue,newValue,categoryTextField,"Name")
//        }
//        itemTextField.textProperty().addListener { observable, oldValue, newValue ->
//            UIUtil().validateText(observable,oldValue,newValue,itemTextField,"alphaText")
//        }
//        priceTextField.textProperty().addListener { observable, oldValue, newValue ->
//            UIUtil().validateText(observable,oldValue,newValue,priceTextField,"Name")
//        }


        //set categoriesComboBox Invisible Boolean
        categoriesComboBox.visibleProperty().set(false)


        editMenuDB!!.addListener(MapChangeListener {
            println("changed!!")
            if(toggleState!! == "Add Category") updateCategoriesList()

        })


        categoriesComboBox.selectionModel.selectedItemProperty().addListener(ChangeListener { observable, oldValue, newValue ->

            //validate selection
            if(newValue == null || newValue == "")
            {
                System.out.println(ConsoleColors.yellowText("No Category was selected!"))
                return@ChangeListener
            }
            //render a new list per each category selection
            updateItemsList(newValue)
        })



//        toggleState.bind(selectedRadioBtn.textProperty())


        editItemsToggleGroup.selectedToggleProperty().addListener(ChangeListener { observable, oldValue, newValue ->


            toggleState = (editItemsToggleGroup.selectedToggleProperty().get() as RadioButton).text
            toggleActionHandler(toggleState!!)

            println("selected Toggle: "+toggleState)





        })


        submitBtn.setOnMouseClicked {
                submitUpdate()
            clearTextFields()
        }

        itemTextField.setOnKeyPressed {


            if(it.code.toString() == "ENTER" && (itemTextField.text != "" || itemTextField.text != null) && (priceTextField.text  != "" || priceTextField.text == null))
            {
                submitUpdate()
                clearTextFields()
            }
        }
        priceTextField.setOnKeyPressed {

            if(it.code.toString() == "ENTER" && (itemTextField.text != "" || itemTextField.text != null) && (priceTextField.text  != "" || priceTextField.text == null))
            {
                submitUpdate()
                clearTextFields()
            }
        }
//        val l = ActionListener()

        categoryTextField.setOnKeyPressed{

            println("Value: ${it.code.toString()}")
            if(it.code.toString() == "ENTER")
            {
                submitUpdate()
                clearTextFields()
            }
        }


        //call updateItems to hydrate comboBox with the first category key
        val menuMap = editMenuDB["menu"]!! as Map<String, Any>
        if (menuMap.size > 0) updateItemsList(menuMap.keys.first())

        //show comboBox if necessary

        val selectedRadioTxt = (editItemsToggleGroup.selectedToggleProperty().get() as RadioButton).text
        toggleActionHandler(selectedRadioTxt)

    }

    fun showCategoriesComboBox()
    {


        //create combobox for the category
        categoriesComboBox.items = categoriesList

        categoriesComboBox.resizeRelocate(categoryTextField.layoutX,categoryTextField.layoutY,categoryTextField.width,categoryTextField.height)
        screen.add(categoriesComboBox)
    }
    fun showItemsComboBox()
    {


        //create combobox for the category
        itemsComboBox.items = itemsList

        itemsComboBox.resizeRelocate(itemTextField.layoutX,categoryTextField.layoutY,itemTextField.width,itemTextField.height)
        screen.add(itemsComboBox)
    }
    fun updateCategoriesList()
    {

        val editMenuMap = editMenuDB!!["menu"] as Map<String, Any>

        if(categoriesList!!.sizeProperty.get() > 0)
        {
           try{
               categoriesList!!.clear()
           }catch (e: Exception){
               System.out.println(ConsoleColors.yellowText(e.message))
           }
        }

        categoriesList!!.addAll(editMenuMap.keys)
        categoryTextField.text = ""

    }

    fun clearTextFields()
    {
        categoryTextField.text = ""
        itemTextField.text = ""
        priceTextField.text = ""
    }

    fun updateItemsList(inCategory:String)
    {
        val editMenuMap = editMenuDB!!["menu"] as Map<String, Any>
        if(itemsList.size > 0) itemsList.clear()

        val itemsListInMenu = editMenuMap[inCategory] as ArrayList<RestaurantItem>
        var tempList = ArrayList<String>().observable()
        itemsListInMenu.forEach {  tempList.add(it.name)}
        itemsList = tempList
    }

    fun submitUpdate()
    {
        val category = if(categoryTextField.isVisible) categoryTextField.text else categoriesComboBox.selectedItem

        val item = if (!itemTextField.text.equals("")) itemTextField.text else ""
        val price = if (!priceTextField.text.equals("")) priceTextField.text.toDouble() else -1.0
        var updatedName = null
        var updatedPrice = -1
        var sendState = ""

        //silently fail if category is not specified
        if(category == "" ||category == null)
        {
            println(ConsoleColors.yellowText("No Category Has Been Selected!"))
            return
        }

        HotelBackEndNorris().updatePOSMenu(toggleState, category, item, price,updatedName, updatedPrice)





        val s= RestaurantItem().getDeserializedMenu(DB().readDocInDB(DBNames.restaurantMenu)).observable()
        editMenuDB!!["menu"] = s

        println("map: "+editMenuDB!!["menu"].toString())
    }

    fun toggleActionHandler(selectedRadioTxt: String)
    {
        when(selectedRadioTxt)
        {
            "Add Item" -> {

                categoryTextField.disableProperty().set(true)
                itemTextField.disableProperty().set(false)
                priceTextField.disableProperty().set(false)

                //show comboBox
                if(!categoriesComboBox.isVisible)
                {
                    //make categories textField invisible
                    categoryTextField.visibleProperty().set(false)
                    //set comboBox visible property (doesn't update automatically)
                    categoriesComboBox.visibleProperty().set(true)
                    showCategoriesComboBox()
                }


            }
            "Remove Item" -> {
                categoryTextField.disableProperty().set(false)
                priceTextField.disableProperty().set(true)



                //show comboBox
                if(!categoriesComboBox.isVisible)
                {
                    //make categories textField invisible
                    categoryTextField.visibleProperty().set(false)
                    //make items textField invisible
                    itemTextField.disableProperty().set(false)

                    //set comboBox visible property (doesn't update automatically)
                    categoriesComboBox.visibleProperty().set(true)

                    showItemsComboBox()
                }
            }
            "Modify Item" -> {
                categoryTextField.disableProperty().set(false)
                itemTextField.disableProperty().set(false)
                priceTextField.disableProperty().set(false)



                //show comboBox
                if(!categoriesComboBox.isVisible)
                {
                    //make categories textField invisible
                    categoryTextField.visibleProperty().set(false)
                    //set comboBox visible property (doesn't update automatically)
                    categoriesComboBox.visibleProperty().set(true)
                    showCategoriesComboBox()
                }
            }
            "Add Category" -> {
                categoryTextField.disableProperty().set(false)
                itemTextField.disableProperty().set(true)
                priceTextField.disableProperty().set(true)


                //show textBox
                if(!categoryTextField.isVisible)
                {
                    categoriesComboBox.visibleProperty().set(false)

                    categoryTextField.visibleProperty().set(true)
                }

            }
            "Remove Category" -> {
                categoryTextField.disableProperty().set(true)
                itemTextField.disableProperty().set(true)
                priceTextField.disableProperty().set(true)



                //show comboBox
                if(!categoriesComboBox.isVisible)
                {
                    //make categories textField invisible
                    categoryTextField.visibleProperty().set(false)
                    //set comboBox visible property (doesn't update automatically)
                    categoriesComboBox.visibleProperty().set(true)
                    showCategoriesComboBox()
                }

            }

        }
    }
}