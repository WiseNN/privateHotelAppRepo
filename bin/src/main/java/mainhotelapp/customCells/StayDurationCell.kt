package mainhotelapp.customCells

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import javafx.scene.text.TextAlignment
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue

class StayDurationView (fromDate : String, toDate: String): View()
{
    val fromDateProperty = SimpleStringProperty(fromDate)
    var fromDate by fromDateProperty


    val toDateProperty = SimpleStringProperty(toDate)
    var toDate by toDateProperty






    override val root = createGraphics()


    fun createGraphics() : StackPane
    {


        var shapesVBox = VBox()
        val stack = StackPane()

        val l1 = Label()
            l1.addClass("rect")
            l1.styleClass.add("rect")
            l1.text = "topText"
            l1.prefWidthProperty().bind(shapesVBox.widthProperty())
            l1.prefHeightProperty().bind(shapesVBox.widthProperty())
            l1.textAlignment = TextAlignment.CENTER
            l1.alignment = Pos.CENTER
            l1.textProperty().bind(fromDateProperty)

        val l2 = Label()
            l2.addClass("rect")
            l2.styleClass.add("rect")
            l2.text = "bottomText"
            l2.prefWidthProperty().bind(shapesVBox.widthProperty())
            l2.prefHeightProperty().bind(shapesVBox.widthProperty())
            l2.textAlignment = TextAlignment.CENTER
            l2.alignment = Pos.CENTER
            l2.textProperty().bind(toDateProperty)



            shapesVBox.children.addAll(l1,l2)

            stack.add(shapesVBox)


        //create rect
        val rect = Rectangle(shapesVBox.layoutBounds.maxX/2,shapesVBox.layoutBounds.maxY/2,shapesVBox.width/2,shapesVBox.width/2)
            rect.strokeType = StrokeType.OUTSIDE
            rect.strokeWidth = 10.0
            rect.stroke = Color.BLACK


            stack.add(rect)


        return stack
    }


}
class TestApp : App(StayDurationView::class)
{

    init {
        importStylesheet("/styles/RectBtnStyle.css")
        reloadStylesheetsOnFocus()

    }
}

