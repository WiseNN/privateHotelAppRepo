package mainhotelapp

import javafx.application.Application
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.collections.FXCollections
import tornadofx.*

fun speakKotlin(statement: String) : String
{
    return "The Statement is: " + statement
}
//
//
//
//------------------- Building UI
//
//
//

class MyView1 : View()
{
//    val controller: MyController()
    override val root = vbox{
        button("Go to MyView2"){
            action{
                replaceWith(MyView2::class)
            }

        }




    }
}

class MyView2 : View()
{
//    val controller: MyController()
    override val root = vbox{
        button("Go to MyView1"){
            action{
                replaceWith(MyView1::class)
            }

        }




    }
}


class MyController: Controller()
{
    val values = FXCollections.observableArrayList<String>("Alpha", "Beta", "Gamma", "Delta")
}


//class MyApp : App(MyView1::class)
//fun main(args: Array<String>)
//{
//    Application.launch(MyApp::class.java, *args)
//}









