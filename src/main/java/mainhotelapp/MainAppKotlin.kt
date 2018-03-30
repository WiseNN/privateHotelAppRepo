package mainhotelapp

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import javafx.application.Application
import tornadofx.*
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider




class MainAppKotlin : App(MyButtonBarView::class)
    {

        init {
            importStylesheet("/styles/RectBtnStyle.css")
            reloadStylesheetsOnFocus()
            SvgImageLoaderFactory.install()

        }
    }


fun main(args: Array<String>) {

        Application.launch(MainAppKotlin::class.java, *args)
        SvgImageLoaderFactory.install(PrimitiveDimensionProvider())


    }

