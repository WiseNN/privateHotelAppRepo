package mainhotelapp

import couchdb.*
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import tornadofx.*
import javafx.scene.control.ComboBox
import javafx.scene.layout.Priority
import kots.KitchenOrderQueueView


class MyButtonBarView constructor() : View()
{




    //imported views
    var roomRezView: View? = null
    var tableRezView : View? = null
    var restuarantPOSView : View? = null
    var kotsView : View? = null
    var editPOSView : View? = null

    val spaRezFXML: AnchorPane by fxml("/fxml/SpaReservationUI.fxml")



    var homeBtn : Button? = null
    var rewardsSysBtn : Button? = null
    var roomResBtn : Button? = null
    var spaRezBtn : Button? = null
    var posBtn : Button? = null
    var editPosBtn : Button? = null
    var kotsBtn: Button? = null


    override val root = VBox()

    init{

        //create sample data
        val roomsClass = Room()
        val tablesClass = RestaurantTable()
        val menuItemsClass = RestaurantItem()
        val db = DB()
        db.permenantlyRemoveDoc(DBNames.rooms)
        db.permenantlyRemoveDoc(DBNames.reservations)
        db.permenantlyRemoveDoc(DBNames.hotelCalenderEvents)
        db.permenantlyRemoveDoc(DBNames.restaurantTables)
        db.permenantlyRemoveDoc(DBNames.restaurantMenu)
        roomsClass.createRooms()
        tablesClass.createTables()
        db.createDoc(DBNames.reservations, HashMap<String, Any>())
        db.createDoc(DBNames.hotelCalenderEvents, HashMap<String, Any>())
        menuItemsClass.createMenu()

//        ------------------//------------------//------------------//------------------




        //this was sample code to turn the button into a combobox
        val cBox = ComboBox<String>()
        cBox.items = listOf("1","2").observable()
        root.prefWidthProperty().set(699.0)
        root.vgrow = Priority.ALWAYS

        hbox{

                this.prefHeightProperty().set(30.0)


            homeBtn =  button("Home"){

                this.prefWidthProperty().bind(root.prefWidthProperty())
                this.prefHeightProperty().bind(root.heightProperty().divide(barBtnSizeDiviser))


                action{
                    println("Presenting 1 ")

                    this.disableProperty().set(true)

                    roomResBtn!!.disableProperty().set(false)
                    rewardsSysBtn!!.disableProperty().set(false)
                    spaRezBtn!!.disableProperty().set(false)
                    posBtn!!.disableProperty().set(false)
                    editPosBtn!!.disableProperty().set(false)
                    kotsBtn!!.disableProperty().set(false)


                    //pass in the parentView to retain a reference
                    root.getChildList()!!.remove(root.getChildList()!!.last())
                    root.getChildList()!!.add(root.getChildList()!!.size,Label("Home Screen"))
                }
            }

            //replace this button with a combo box, and put the views inside of the box.
             roomResBtn = button("Room Reservation"){
                this.prefWidthProperty().bind(root.prefWidthProperty())
                this.prefHeightProperty().bind(root.heightProperty().divide(barBtnSizeDiviser))
                action{



                    this.disableProperty().set(true)

                    homeBtn!!.disableProperty().set(false)
                    rewardsSysBtn!!.disableProperty().set(false)
                    spaRezBtn!!.disableProperty().set(false)
                    posBtn!!.disableProperty().set(false)
                    editPosBtn!!.disableProperty().set(false)
                    kotsBtn!!.disableProperty().set(false)

                    root.getChildList()!!.remove(root.getChildList()!!.last())
                    //pass in the parentView to retain a reference
                    root.getChildList()!!.add(root.getChildList()!!.size, roomRezView!!.root)

                }
            }

            rewardsSysBtn = button("Table Reservation"){

                this.prefWidthProperty().bind(root.prefWidthProperty())
                this.prefHeightProperty().bind(root.heightProperty().divide(barBtnSizeDiviser))
                action{

                    this.disableProperty().set(true)

                    homeBtn!!.disableProperty().set(false)
                    roomResBtn!!.disableProperty().set(false)
                    spaRezBtn!!.disableProperty().set(false)
                    posBtn!!.disableProperty().set(false)
                    editPosBtn!!.disableProperty().set(false)
                    kotsBtn!!.disableProperty().set(false)

                    root.getChildList()!!.remove(root.getChildList()!!.last())
                    root.getChildList()!!.add(root.getChildList()!!.size,tableRezView!!.root)
                }
            }

              spaRezBtn = button("Spa Reservation"){
                this.prefWidthProperty().bind(root.prefWidthProperty())
                this.prefHeightProperty().bind(root.heightProperty().divide(barBtnSizeDiviser))
                action{

                    this.disableProperty().set(true)

                    homeBtn!!.disableProperty().set(false)
                    roomResBtn!!.disableProperty().set(false)
                    rewardsSysBtn!!.disableProperty().set(false)
                    posBtn!!.disableProperty().set(false)
                    editPosBtn!!.disableProperty().set(false)
                    kotsBtn!!.disableProperty().set(false)

                    root.getChildList()!!.remove(root.getChildList()!!.last())
                    root.getChildList()!!.add(root.getChildList()!!.size,spaRezFXML)
                }


            }
           posBtn = button("POS System"){
                this.prefWidthProperty().bind(root.prefWidthProperty())
                this.prefHeightProperty().bind(root.heightProperty().divide(barBtnSizeDiviser))
                action{

                    this.disableProperty().set(true)

                    homeBtn!!.disableProperty().set(false)
                    roomResBtn!!.disableProperty().set(false)
                    rewardsSysBtn!!.disableProperty().set(false)
                    editPosBtn!!.disableProperty().set(false)
                    spaRezBtn!!.disableProperty().set(false)
                    kotsBtn!!.disableProperty().set(false)

                    root.getChildList()!!.remove(root.getChildList()!!.last())
                    root.getChildList()!!.add(root.getChildList()!!.size,restuarantPOSView!!.root)
                }


            }
           editPosBtn = button("Edit POS Menu"){
                this.prefWidthProperty().bind(root.prefWidthProperty())
                this.prefHeightProperty().bind(root.heightProperty().divide(barBtnSizeDiviser))
                action{

                    this.disableProperty().set(true)

                    homeBtn!!.disableProperty().set(false)
                    roomResBtn!!.disableProperty().set(false)
                    rewardsSysBtn!!.disableProperty().set(false)
                    posBtn!!.disableProperty().set(false)
                    spaRezBtn!!.disableProperty().set(false)
                    kotsBtn!!.disableProperty().set(false)


                    root.getChildList()!!.remove(root.getChildList()!!.last())
                    root.getChildList()!!.add(root.getChildList()!!.size,editPOSView!!.root)
                }
            }

            kotsBtn = button("KOTS"){
                this.prefWidthProperty().bind(root.prefWidthProperty())
                this.prefHeightProperty().bind(root.heightProperty().divide(barBtnSizeDiviser))
                action{

                    this.disableProperty().set(true)

                    homeBtn!!.disableProperty().set(false)
                    roomResBtn!!.disableProperty().set(false)
                    rewardsSysBtn!!.disableProperty().set(false)
                    posBtn!!.disableProperty().set(false)
                    spaRezBtn!!.disableProperty().set(false)
                    editPosBtn!!.disableProperty().set(false)



                    root.getChildList()!!.remove(root.getChildList()!!.last())
                    root.getChildList()!!.add(root.getChildList()!!.size,kotsView!!.root)
                }
            }





        }

        roomRezView = HotelRoomReservationView(this)
        tableRezView = RestaurantReservationView(this)
        restuarantPOSView = RestaurantPOSView(this)
        kotsView = KitchenOrderQueueView()

        //pass the menu database to the editPOS View considering we are the parent of
        //both instances
        editPOSView = EditPOSView(this)
         (editPOSView as EditPOSView).editMenuDB = (restuarantPOSView as RestaurantPOSView).menuDB



        //set initial view as the room reservation view
        this.add(roomRezView!!.root)
        roomResBtn!!.disableProperty().set(true)



    }


}