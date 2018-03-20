package mainhotelapp.customCells

import couchdb.Room
import javafx.collections.ObservableList
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.stage.StageStyle
import mainhotelapp.AvailableRooms
import mainhotelapp.ReservationView
import mainhotelapp.RoomBillingView
import tornadofx.*

class SubmitBtnTableViewCell (submitBtnColumn: TableColumn<Room, String>): TableCell<Room, String>()
{



    val reservationView = find(ReservationView::class)

    init {
        this.prefWidthProperty().bind(submitBtnColumn.widthProperty())



        //store room from observable list



    }
    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)

        graphic = button {

            text = "Submit"

//            width = this@SubmitBtnTableViewCell.widthProperty().get()
//            height = this@SubmitBtnTableViewCell.heightProperty().get()
            styleClass.add("submitTableBtnCell")

            //return the current room on the roomList



            //when this button is clicked, take us to the reseravtion summary page
            setOnMouseClicked {
                //get room number for corresponding row


                val billingView = RoomBillingView(reservationView.listOfAvailableRooms[this@SubmitBtnTableViewCell.index])
                billingView.openWindow(stageStyle = StageStyle.DECORATED)




            }
        }
    }
}