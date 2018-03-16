package mainhotelapp

import javafx.scene.control.SplitPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import tornadofx.*

class CalenderView constructor(listOfDays : List<Int>, parentView : SplitPane) : View()
{


    //holds days of month and month button (with string "April")
    override  val root = BorderPane()



    init {


        //give space between the calender and the calender frame
        root.paddingAll = calenderFramePadding

        //makes calender resizable with constraints (to a small degree)
        root.minWidth = calenderMinWidth
        root.maxWidth = calenderMaxWidth


        //displays the calender month
        val calenderMonthBtn = button {
            text = "April"
            minHeightProperty().set(27.0)
            maxHeightProperty().set(35.0)

            prefWidthProperty().bind(root.widthProperty())
            prefHeightProperty().bind(prefWidthProperty().divide(7.285))




        }

        //pin calenderMonthBtn to the top of the borderPane
        root.top = calenderMonthBtn


        val calenderDays = datagrid(listOfDays) {

            //set 1px margin between all calenderDay buttons
            this.horizontalCellSpacing = 1.0
            this.verticalCellSpacing = 1.0

            //doesnt allow the calender frame to grow vertically
            this.vgrow = Priority.NEVER

            //sets how many calender days can sit in one row
            maxCellsInRow = numOfCalenderDaysInOneRow.toInt()


            //width of datagrid is equal to width of the container of the data grid
            this.prefWidthProperty().bind(root.widthProperty())

            //split the width of the calenderMonthBtn across the number of calender day buttons (5 in this case)
            cellWidthProperty.bind(calenderMonthBtn.widthProperty().divide(numOfCalenderDaysInOneRow).subtract(calenderFramePadding-3))

            cellHeightProperty.bind(cellWidthProperty)

            cellCache {

                button {

                    minWidthProperty().set(35.0)
                    minHeightProperty().set(35.0)

                    this.prefWidthProperty().bind(cellWidthProperty)
                    this.prefHeightProperty().bind(cellHeightProperty)

                    text = "$it"

                }



            }
        }

        root.center = calenderDays

    }

}