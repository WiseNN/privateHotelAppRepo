package mainhotelapp

import javafx.scene.control.Button
import javafx.scene.layout.VBox
import tornadofx.*

class MyButtonBarView constructor(parentView : VBox) : View()
{

    //kotlin does not have static members, because of this
    //we have to create something called a companion object. Whatever
    //is inside, is considered a static member
    companion object {
        var oneButton : Button by singleAssign()
    }

    override val root = hbox{

        oneButton =  button("Home"){

            this.prefWidthProperty().bind(parentView.widthProperty())
            this.prefHeightProperty().bind(parentView.heightProperty().divide(barBtnSizeDiviser))

            action{
                println("Presenting 1 ")
            }
        }

        button("Reservation System"){
            this.prefWidthProperty().bind(parentView.widthProperty())
            this.prefHeightProperty().bind(parentView.heightProperty().divide(barBtnSizeDiviser))
            action{

                println("Presenting 2")
            }
        }

        button("Rewards System"){

            this.prefWidthProperty().bind(parentView.widthProperty())
            this.prefHeightProperty().bind(parentView.heightProperty().divide(barBtnSizeDiviser))
            action{

                println("Presenting 3")
            }
        }

        button("Home"){
            this.prefWidthProperty().bind(parentView.widthProperty())
            this.prefHeightProperty().bind(parentView.heightProperty().divide(barBtnSizeDiviser))
            action{

                println("Presenting 1 ")
            }


        }
        button("Reservation System"){
            this.prefWidthProperty().bind(parentView.widthProperty())
            this.prefHeightProperty().bind(parentView.heightProperty().divide(barBtnSizeDiviser))
            action{

                println("Presenting 2")
            }


        }
        button("Rewards System"){
            this.prefWidthProperty().bind(parentView.widthProperty())
            this.prefHeightProperty().bind(parentView.heightProperty().divide(barBtnSizeDiviser))
            action{

                println("Presenting 3")
            }
        }

    }
}