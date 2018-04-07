package kots

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider
import javafx.application.Application
import javafx.scene.layout.StackPane
import tornadofx.*

class KitchenOrderQueueView : View()
{
    val kitchenOrderQVIew: StackPane by fxml("/fxml/kitchenOrderQueue_UI.fxml")
    override val root = kitchenOrderQVIew

    init {
//        val g = Array<String>(0,{ ""})
//        KOTS_Server.main(g)

//        root.vgrow = Priority.ALWAYS
    }


    //Kitchen Order Queue System Architecture




}

public class KOTS : App(KitchenOrderQueueView::class)

fun main(args: Array<String>) {

    Application.launch(KOTS::class.java, *args)
    SvgImageLoaderFactory.install(PrimitiveDimensionProvider())




}