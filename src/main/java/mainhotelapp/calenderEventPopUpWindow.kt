package mainhotelapp

import hotelbackend.HotelBackEndNorris
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import tornadofx.*

class CalenderEventPopUpWindow(calenderDay: String) : Fragment()
{
    val calenderEventPopUpWindow: VBox by fxml("/fxml/CalenderEventPopUpWindow.fxml")
    val eventUrlTextField: TextField by fxid()
    val calenderDayText: Text by fxid()
    val addCalenderEventBtn: Button by fxid()
    val addEventRadioBtn: RadioButton by fxid("addEventRadioBtn")
    val removeEventRadioBtn: RadioButton by fxid("removeEventRadioBtn")


    override val root = calenderEventPopUpWindow

    init {

//        val stage = Stage()
//        stage.initOwner(parentStage)
//        stage.initModality(Modality.APPLICATION_MODAL)
//        val popUpScene = Scene(root,root.width,root.height)
//        stage.scene = popUpScene
//        this.modalStage = stage



        //group radio buttons together
        val eventToggleGroup = ToggleGroup()
        addEventRadioBtn.toggleGroup = eventToggleGroup
        removeEventRadioBtn.toggleGroup = eventToggleGroup
        addEventRadioBtn.isSelected = true

        calenderDayText.text = calenderDay

        addCalenderEventBtn.setOnMouseClicked {

                    val backEnd = HotelBackEndNorris()


            if( addEventRadioBtn.isSelected)
            {
                backEnd.modifyCalenderEvent(calenderDay.toInt(),eventUrlTextField.text,"add").apply {
                    this@CalenderEventPopUpWindow.modalStage!!.hide()
                }

            }
            else if(removeEventRadioBtn.isSelected)
            {
                backEnd.modifyCalenderEvent(calenderDay.toInt(),null,"remove").apply {

                    this@CalenderEventPopUpWindow.modalStage!!.hide()
                }
            }


        }


    }


}