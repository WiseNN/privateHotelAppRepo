package mainhotelapp.customCells

import couchdb.Room
import javafx.geometry.Insets
import javafx.geometry.NodeOrientation
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.text.Text
import tornadofx.*
import javafx.scene.control.ListCell
import javafx.scene.layout.*
import mainhotelapp.AvailableRooms


//create listView singleton
var addPackagesList by singleAssign<ListView<String>>()

class PackagesTableViewCell (packagesColumn: TableColumn<Room, String>): TableCell<Room, String>()
{
    val tableCellHeight = 50.0
    val myCol = packagesColumn


    init{
        isEditable = false

//        listView.vgrow = Priority.NEVER
//        listView.setCellFactory { PackageListViewCell() }


        this.prefWidthProperty().bind(packagesColumn.widthProperty())


        this.prefHeightProperty().set(tableCellHeight)
        this.maxHeightProperty().set(tableCellHeight)
        println("colWidth: ${packagesColumn.widthProperty().get()}")

    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        println("tableItem: $item")


        //if not null split into array, if null make empty array
        val pkgListAry = if(item != null) item!!.split(";".toRegex()) else listOf()

//        val myListView = ListView<String>()
//        myListView.vgrow = Priority.NEVER
//
//        myListView.prefHeightProperty().bind(this.heightProperty())
//        myListView.vgrow = Priority.NEVER
//        myListView.setCellFactory {  }
//        graphic = vbox { add(myListView) }


        val scrollPane = ScrollPane()
        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        scrollPane.vmax = this.prefHeightProperty().get()
        scrollPane.hmax = this.prefWidthProperty().get()



        val vbox = VBox()
        vbox.prefWidthProperty().bind(scrollPane.widthProperty().minus(15.0))



        pkgListAry.forEachIndexed { index, s ->

            val pkgKVList = s!!.split("=".toRegex())

            val checkbox = CheckBox()
            checkbox.nodeOrientation = NodeOrientation.RIGHT_TO_LEFT
            checkbox.alignment = Pos.TOP_RIGHT
            checkbox.prefWidthProperty().bind(vbox.widthProperty())

            checkbox.styleClass.add("tableViewCheckBox")
            checkbox.isSelected = pkgKVList[1].toBoolean()

            checkbox.text = pkgKVList[0]

            checkbox.padding = Insets(5.0,5.0,5.0,8.0)
//            checkbox.paddingProperty().set(Insets(0.0,0.0,0.0,myCol.width.minus(checkbox.width)))





            checkbox.setOnMouseClicked {
                println("clicked!")
                println("colWidth: "+myCol.widthProperty().get())
                println("who is this: ${it.target}")
            }

            vbox.add(checkbox)

            scrollPane.content = vbox











        }

        graphic = scrollPane







//        graphic = listView
        //graphics need to equal a list view
//        graphic = //a ListView

        //we are not using text propery for this cell
//        text = null

    }



//    override fun startEdit() {
//        super.startEdit()
//    }



}


 class PackageListViewCell : ListCell<String>()
 {
//


    public override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)

        println("isEmpty Bool: $empty")

            val hbox = HBox()
            val textObj = Text()
            val checkBox = CheckBox()

            textObj.text = item

            checkBox.isSelected = false
            checkBox.isDisable = true

            hbox.add(textObj)

            hbox.add(checkBox)

//            graphic = hbox

    }
}
