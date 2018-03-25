package mainhotelapp.customCells

import couchdb.DB
import couchdb.DBNames
import couchdb.Room
import devutil.MyUtil
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TextArea
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import mainhotelapp.HotelRoomReservationView
import mainhotelapp.MyButtonBarView
import tornadofx.*
import java.util.*
import java.text.SimpleDateFormat



//cell factory for notes in the tableView
class NotesTableCell constructor(employeeID: String, notesColumn: TableColumn<Room, String>): TableCell<Room, String>()
{

    var isEditingActive = false
    var notesTextArea = createTextArea()
    var wrappingText = createText()
    val employeedID = employeeID
    val reservationView = find(MyButtonBarView::class).roomRezView as HotelRoomReservationView


    init {

//        this.isWrapText = true
//        this.wrapTextProperty().set(true)
        this.prefWidthProperty().bind(notesColumn.widthProperty())


        //when mouse is clicked, decipher click
        this.setOnMouseClicked { event ->
            println("Cell: "+event.clickCount)

            if(event.clickCount == 2)
                startEdit()
        }
    }



    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        println("requested update")

        //set item to wrappingText text property because we're using
        //wrappingText Text() graphic
        wrappingText.text = item
        graphic = wrappingText
        this.autosize()


    }

    //change the Cell to a TextField, Starting to Edit
    override fun startEdit() {
        super.startEdit()
        println("requested edit isEditingActive: $isEditingActive currentText: $text")

        //set noteTextArea text to wrappingText to see text when editing
        notesTextArea.text = wrappingText.text

        //load notesTextArea graphics
        graphic = notesTextArea



    }

    override fun cancelEdit() {
        super.cancelEdit()



        println("requested cancel")
        //set graphics to wrappingText, it retains the old text
        graphic = wrappingText


    }

    override fun commitEdit(newValue: String?) {

        println("commit edit $newValue")

        //get the text value from the TextArea notesTextArea, and sustain in
        // wrappingText for viewing

        //if nothing has changed, or if textArea is empty stop commit
        if(notesTextArea.text == wrappingText.text)
        {
            cancelEdit()
        }
        else if(notesTextArea.text == "")
        {
            wrappingText.text = ""
            graphic = wrappingText

        }else{

            //process notes tableCell value

            print("this index from row: "+tableRow.index)


            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date = Date()


            println(formatter.format(date))

            val updatedText = newValue+"\n--${date}--\n~~$employeedID~~"

             saveNote(updatedText).apply {

                wrappingText.text = updatedText

                 reservationView.listOfAvailableRooms[index].notes = wrappingText.text.substring(0,wrappingText.text.indexOf("-")-1)

                graphic = wrappingText


            }











        }



    }

    private fun saveNote(withText: String) : String
    {
        //get room number for corresponding row
        val roomNumberCell = tableRow.getChildList()!![0] as? TableCell<Room, String>

        val roomNumber = roomNumberCell!!.item

        //update the notes property in the database
        val db = DB()
        val roomsMap = db.readDocInDB(DBNames.rooms)
        val serializedRoom = roomsMap[roomNumber] as String

        val util = MyUtil()
        val deSerializedRoom = util.deserializeObject(Room::class.java,serializedRoom)
        deSerializedRoom.notes = wrappingText.text

        //re-serialize room
        val reSerializedRoom = util.serializeObject(deSerializedRoom)

        //put room back into roomsMap
        roomsMap[roomNumber] = reSerializedRoom

        //save updated roomsMap to DB
        db.updateDocInDB(DBNames.rooms, roomsMap)

        return withText

    }

    //create a wrapping text cell
    private fun createText(): Text
    {
        val text = Text()

        text.wrappingWidthProperty().bind(this.widthProperty())
        text.textAlignment = TextAlignment.CENTER


        //bind wrappingText to the cell's textPropert
//        text.textProperty().bind(textProperty())

        text.fill = Color.RED

        return text
    }

    private fun createTextArea(): TextArea
    {

        val textArea = TextArea()

        //Make TextArea wrapText, with flexible height
        textArea.isWrapText = true
        textArea.wrapTextProperty().set(true)
        textArea.prefWidthProperty().bind(widthProperty())



        //set textBox click handler (if double clicked, cancelEdit)
        textArea.setOnMouseClicked { e ->
            println("TextField: "+e.clickCount);
            if(e.clickCount == 2)
                cancelEdit()
        }


        textArea.setOnKeyPressed { e ->
            println("char: "+e.code.toString())

            if (e.isShiftDown && e.code.toString() == "ENTER")
                commitEdit(notesTextArea.text)
            else
                println("not committing...")
        }
        return textArea
    }


}