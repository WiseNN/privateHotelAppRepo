package mainhotelapp

import javafx.application.Application
import tornadofx.*


class MainAppKotlin : App(MyButtonBarView::class)
    {

        init {
            importStylesheet("/styles/RectBtnStyle.css")
            reloadStylesheetsOnFocus()
        }
    }


fun main(args: Array<String>)
{
    Application.launch(MainAppKotlin::class.java, *args)
}
