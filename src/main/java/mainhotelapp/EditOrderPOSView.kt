package mainhotelapp

import couchdb.RestaurantItem
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import tornadofx.*

class EditOrderPOSView(itemName: String, parentView: View ) : View()
{
    val editOrderView : StackPane by fxml("/fxml/EditOrder_POS_Item_UI.fxml")
    override val root = editOrderView
    val trashCanImgView : ImageView by fxid()
    val checkboxImgView : ImageView by fxid()
    val notesTextArea : TextArea by fxid()
    val dishNameLabel : Label by fxid()
    var stickyNote: ImageView = ImageView()

    init {

            root.vgrow = Priority.ALWAYS
            //set dish label
            dishNameLabel.label(itemName)

            //make trashcan disregard textArea text, and dismiss view
            trashCanImgView.setOnMouseClicked {
                notesTextArea.text = ""
                stickyNote.isVisible = false
                root.hide()
            }



            //make checkbox save textArea text to menuItem's notes property
            checkboxImgView.setOnMouseClicked {
                stickyNote.isVisible = true
                root.hide()
            }
            //create/update an ObservableBoolean on menu page to update if this dish has a note
            //if so, right on change observer to add a sticky not icon to the order if note
            //has been added




    }

}