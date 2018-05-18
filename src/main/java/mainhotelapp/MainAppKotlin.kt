package mainhotelapp

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import javafx.application.Application
import tornadofx.*
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider
import kots.KOTS_Server_Java


class MainAppKotlin : App(MyButtonBarView::class)
    {

        init {

            importStylesheet("/styles/RectBtnStyle.css")
            reloadStylesheetsOnFocus()
            SvgImageLoaderFactory.install()

        }

        override fun stop() {
            val name = Thread.currentThread().name
            println("stop() method: " + name)
            name
            KOTS_Server_Java.stopServer()

            super.stop()

        }
    }


//fun main(args: Array<String>) {
//
//        Application.launch(MainAppKotlin::class.java, *args)
//        SvgImageLoaderFactory.install(PrimitiveDimensionProvider())
//
//
//
//    }

