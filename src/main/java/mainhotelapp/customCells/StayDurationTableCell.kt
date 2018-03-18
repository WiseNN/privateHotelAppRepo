package mainhotelapp.customCells

import couchdb.Room
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.layout.*
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import javafx.scene.text.TextAlignment
import mainhotelapp.AvailableRooms
import tornadofx.*

class StayDurationTableCell (durationColumn : TableColumn<Room, String>): TableCell<Room, String>()
{
    val fromDateProperty = SimpleStringProperty()
    val toDateProperty = SimpleStringProperty()
    val cellGraphics = createGraphics()



    init {

        isEditable = false
        this.prefWidthProperty().bind(durationColumn.widthProperty())
        this.prefHeightProperty().set(100.0)
    }
    override fun updateItem(item: String?, empty: Boolean)
    {
        super.updateItem(item, empty)

        fromDateProperty.set(item)
        toDateProperty.set(item)

//        println("item from duration cell: ${cellGraphics.text()}")


        graphic = cellGraphics



    }
    init {
        this.paddingAll = 0

    }



    fun createGraphics() : StackPane
    {
        val stack = StackPane()
        stack.prefWidthProperty().bind(this.widthProperty())
        stack.prefHeightProperty().bind(this.heightProperty())
        stack.vgrow = Priority.NEVER

        val l1 = Label()
//        l1.addClass("rect")
        l1.styleClass.add("durationLabel")
        l1.styleClass.add("topDurationLabel")
        l1.text = "topText"
        l1.textAlignment = TextAlignment.CENTER
        l1.alignment = Pos.CENTER
        l1.textProperty().bind(fromDateProperty)

//        l1.paddingProperty().set(Insets(0.0,0.0,10.0,0.0))

        val l2 = Label()
//        l2.addClass("rect")
        l2.styleClass.add("durationLabel")
        l2.styleClass.add("topDurationLabel")
        l2.text = "bottomText"
        l2.textAlignment = TextAlignment.CENTER
        l2.alignment = Pos.CENTER
        l2.textProperty().bind(toDateProperty)
        l2.textAlignment = TextAlignment.CENTER
        l2.alignment = Pos.CENTER
        l2.textProperty().bind(toDateProperty)
        l2.paddingProperty().set(Insets(10.0,0.0,0.0,0.0))

        var shapesVBox = VBox()
        shapesVBox.vgrow = Priority.NEVER
        shapesVBox.prefWidthProperty().bind(stack.widthProperty())
        shapesVBox.prefHeightProperty().bind(stack.heightProperty())


        l1.prefWidthProperty().bind(shapesVBox.widthProperty())
        l2.prefWidthProperty().bind(shapesVBox.widthProperty())
        //make the height of both labels
        //taller than its container to split the container height equally
        l1.prefHeightProperty().set(200.0)
        l2.prefHeightProperty().set(200.0)
        shapesVBox.children.addAll(l1,l2)

        stack.add(shapesVBox)


        //create rect
        val rect = Rectangle(shapesVBox.layoutBounds.maxX/2,shapesVBox.layoutBounds.maxY/2,shapesVBox.width,shapesVBox.width)

        rect.styleClass.add("designRect")
        rect.widthProperty().set(15.0)
        rect.heightProperty().set(15.0)


        rect.strokeType = StrokeType.OUTSIDE
        rect.strokeWidth = 2.0
        rect.stroke = c("#E7E7E7")
        rect.fill = c("#D0D0D0")

        rect.arcWidthProperty().bind(shapesVBox.widthProperty().divide(20.0))
        rect.arcHeightProperty().bind(shapesVBox.heightProperty().divide(20.0))


        println("arcW: "+rect.arcWidth+"arcH: "+rect.arcHeight)






        stack.add(rect)



//        var reg = Region()
//        reg.layoutXProperty().set(rect.x)
//        reg.layoutYProperty().set(rect.y)
//        reg.prefWidthProperty().set(rect.width)
//        reg.prefHeightProperty().set(rect.height)
//
//        //border style > e <
//        val style = BorderStrokeStyle.SOLID
//        var corner = CornerRadii(20.0)
//        val width = BorderWidths(1.0)
//        val colorP = Color.GRAY
//        val strokeStyle = BorderStroke(colorP, style,corner,width)
//        val e = Border(strokeStyle)
//
//        reg.borderProperty().set(e)
//
//        var bgFill = BackgroundFill(Color.BLUE, CornerRadii(10.0), null)
//        val bg2 = Background(bgFill)
//        reg.backgroundProperty().set(bg2)
//
//        stack.add(reg)


        return stack
    }


    fun makeRect() : Group
    {
        //Drawing a Rectangle
        val rectangle = Rectangle()

        //Setting the properties of the rectangle
        rectangle.x = 150.0
        rectangle.y = 75.0
        rectangle.width = 300.0
        rectangle.height = 150.0

        //Setting the height and width of the arc
        rectangle.arcWidth = 30.0
        rectangle.arcHeight = 20.0

        //Creating a Group object
        return Group(rectangle)
    }


}