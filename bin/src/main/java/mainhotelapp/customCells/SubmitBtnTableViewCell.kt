package mainhotelapp.customCells

import couchdb.Room
import couchdb.Reservation
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.stage.StageStyle
import mainhotelapp.HotelRoomReservationView
import mainhotelapp.MyButtonBarView
import mainhotelapp.ReservationSummaryFragment
import tornadofx.*

class SubmitBtnTableViewCell (submitBtnColumn: TableColumn<Room, String>): TableCell<Room, String>()
{



    val reservationView = find(MyButtonBarView::class).roomRezView as HotelRoomReservationView



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

                //this needs to be changed!!!
                /*
                * ISSUE:
                * ------
                * The reservation process returns a list of available rooms. However, the rooms are returned as if they
                * are already reserved. We need to modify the code in the backend-model to return rooms that are currently
                * available for that rooms's roomType, and then do the reservation process after we have selected which
                * rooms we would like to reserve
                *
                * I am actually fairly confused about this (although I wrote it), this will take more investigating
                * */
                val reservedRoomsList = ArrayList<Any>()
                reservedRoomsList.add(reservationView.listOfAvailableRooms[this@SubmitBtnTableViewCell.index])

                val billingView = ReservationSummaryFragment(reservedRoomsList, Reservation.serviceTypes.hotelRoom)

                billingView.openWindow(stageStyle = StageStyle.DECORATED)




            }
        }
    }
}