package mainhotelapp.customCells

import couchdb.Room
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import mainhotelapp.AvailableRooms
import tornadofx.*

class SubmitBtnTableViewCell (submitBtnColumn: TableColumn<Room, String>): TableCell<Room, String>()
{


    init {
        this.prefWidthProperty().bind(submitBtnColumn.widthProperty())

    }
    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)

        graphic = button {

            text = item

//            width = this@SubmitBtnTableViewCell.widthProperty().get()
//            height = this@SubmitBtnTableViewCell.heightProperty().get()
            styleClass.add("submitTableBtnCell")
        }
    }
}