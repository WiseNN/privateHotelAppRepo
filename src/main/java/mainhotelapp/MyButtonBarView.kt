package mainhotelapp

import com.sun.javafx.scene.control.skin.LabeledText
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import tornadofx.*

class MyButtonBarView constructor() : View()
{



    //imported views
    var rezView : View? = null
    val spaRezFXML: AnchorPane by fxml("/fxml/SpaReservationUI.fxml")


    var homeBtn : Button? = null
    var rewardsSysBtn : Button? = null
    var roomResBtn : Button? = null
    var spaRezBtn : Button? = null


    override val root = VBox()

    init{
            root.prefWidthProperty().set(699.0)

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

                    //pass in the parentView to retain a reference
                    root.getChildList()!!.remove(root.getChildList()!!.last())
                    root.getChildList()!!.add(root.getChildList()!!.size,Label("Home Screen"))
                }
            }

             roomResBtn = button("Room Reservation"){
                this.prefWidthProperty().bind(root.prefWidthProperty())
                this.prefHeightProperty().bind(root.heightProperty().divide(barBtnSizeDiviser))
                action{



                    this.disableProperty().set(true)

                    homeBtn!!.disableProperty().set(false)
                    rewardsSysBtn!!.disableProperty().set(false)
                    spaRezBtn!!.disableProperty().set(false)

                    root.getChildList()!!.remove(root.getChildList()!!.last())
                    //pass in the parentView to retain a reference
                    root.getChildList()!!.add(root.getChildList()!!.size,rezView!!.root)

                }
            }

            rewardsSysBtn = button("Rewards System"){

                this.prefWidthProperty().bind(root.prefWidthProperty())
                this.prefHeightProperty().bind(root.heightProperty().divide(barBtnSizeDiviser))
                action{

                    this.disableProperty().set(true)

                    homeBtn!!.disableProperty().set(false)
                    roomResBtn!!.disableProperty().set(false)
                    spaRezBtn!!.disableProperty().set(false)

                    root.getChildList()!!.remove(root.getChildList()!!.last())
                    root.getChildList()!!.add(root.getChildList()!!.size,Label("Rewards System"))
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

                    root.getChildList()!!.remove(root.getChildList()!!.last())
                    root.getChildList()!!.add(root.getChildList()!!.size,spaRezFXML)
                }


            }
            button("Reservation System"){
                this.prefWidthProperty().bind(root.prefWidthProperty())
                this.prefHeightProperty().bind(root.heightProperty().divide(barBtnSizeDiviser))
                action{

                    println("Presenting 2")
                }


            }
            button("Rewards System"){
                this.prefWidthProperty().bind(root.prefWidthProperty())
                this.prefHeightProperty().bind(root.heightProperty().divide(barBtnSizeDiviser))
                action{

                    println("Presenting 3")
                }
            }





        }

        rezView = HotelRoomReservationView(this)

        this.add(rezView!!.root)
        roomResBtn!!.disableProperty().set(true)



    }


}