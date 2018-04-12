package kots

import couchdb.DB
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider
import devutil.ConsoleColors
import javafx.application.Application
import tornadofx.*
import javafx.scene.paint.Color
import javafx.animation.*
import javafx.scene.shape.Circle
import javafx.animation.Animation
import javafx.animation.Timeline
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.util.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.schedule
import javafx.animation.KeyFrame
import javafx.beans.binding.DoubleBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.stage.StageStyle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.timerTask


class KitchenOrderQueueView : View()
{
    val kitchenOrderQVIew: StackPane by fxml("/fxml/kitchenOrderQueue_UI.fxml")
    val timeLabel : Label by fxid()
    val connectedStatusText : Text by fxid()
    val statusIndicator : Circle by fxid()
    override val root = kitchenOrderQVIew
    val kitchenObservationVBox  : VBox by fxid()
    val orderOneVBox : VBox by fxid()
    val orderTwoVBox : VBox by fxid()
    val orderThreeVBox : VBox by fxid()
    val orderFourVBox : VBox by fxid()

    val orderOneTextLabel : Label by fxid()
    val orderTwoTextLabel : Label by fxid()
    val orderThreeTextLabel : Label by fxid()
    val orderFourTextLabel : Label by fxid()

    val usernameTextField : TextField by fxid()
    val passwordTextField : TextField by fxid()
    val headerBarHBox : HBox by fxid()
    val loginBtn : Button by fxid()
    val ordersBtn : Label by fxid()
    val signInOutBtn : Label by  fxid()
    val gridPane : GridPane by fxid()
    var t : TimerTask? = null
    val employeeManagerBtn : Label by fxid()
    //overlay root
    val orderDetailsOverlayAnchorPane : AnchorPane by fxid()
    //overlay children
    val sidebarMenuAnchorPane : AnchorPane by fxid()
    val menuItemsListAnchorPane: AnchorPane by fxid()
    val itemDetailAnchorPane : AnchorPane by fxid()
    val kotsLoginPanelAnchorPane : AnchorPane by fxid()
    //-------------------------------------------

    val timer = Timer("dequeue timer", true)

    //statics for overlay layout
    val detailWidth = itemDetailAnchorPane.widthProperty()
    val itemsListWidth = menuItemsListAnchorPane.widthProperty()
    val sideBarWidth = sidebarMenuAnchorPane.widthProperty()
    val loginPanelHeight = kotsLoginPanelAnchorPane.heightProperty()
    val headerBarHeight = headerBarHBox.heightProperty()
    val employeeManagerView = KOTS_EmployeeManagerView()


    //set testing boolean
    val isTesting = false
    var isInitialLoad = false
    var isLoggingOut = false


    init
    {


        //init view layout for initial view
        initializeViewLayout(isTesting)

        //disable kitchen observation box
        kitchenObservationVBox.disableProperty().set(!isTesting)


        val dateTime = LocalDateTime.now()
        initClock()




        //login button from slide down loginPanel
        loginBtn.setOnMouseClicked {

            if((usernameTextField.text != "" || usernameTextField.text != null) &&
                    (passwordTextField.text != "" || passwordTextField.text != null))
            {
                val result = KOTS_EmployeeManager().signIntoKOTS(KOTS_EmployeeManager.kotsUserType.SYSTEM,usernameTextField.text, passwordTextField.text)


                if(result[0])
                {
                    //pass this instance to server class
                    KOTS_Server_Java.kitchView = this

                    //start Server
                    KOTS_Server_Java.startServer()


                    //set isInitialLoad
                    isInitialLoad = true


//                    KOTS_Server_Java.orderQueue = ConcurrentLinkedQueue<KOTS_Order>()

                    //init dequeing timer
                    timer.scheduleAtFixedRate(timerTask {

//                        if(isInitialLoad)
//                        {
//                            KOTS_Server_Java.orderQueue = ConcurrentLinkedQueue<KOTS_Order>()
//                        }
                        println("running...")


                        initDequeueTask()
                    }, 2000,3000)



                    //load logged in event listeners
//                    loadLoggedInListeners()

                    //hide loginPanel
                    hideLoginPanel(loginPanelHeight.add(headerBarHeight).add(20)).play()

                    //enable kitchen observation view
                    kitchenObservationVBox.disableProperty().set(false)

                    println(ConsoleColors.cyanText("From Login: Success!"))


                }
                else if(!result[0] && result[1]) //user exists, failed password
                {
                    println(ConsoleColors.cyanText("From Login: Password does not match our records"))
                }
                else if(!result[0] && !result[1]) //user does not exist
                {
                    println(ConsoleColors.cyanText("From Login: Username does not exist"))
                }

            }

            //clear textFields
            usernameTextField.text = ""
            passwordTextField.text = ""

        }

        root.vgrow = Priority.ALWAYS


        //for incoming orders
//        testOrderChangeAnimation()

    }

    fun initDequeueTask()
    {


//        val task = object : TimerTask() {
//            internal var i = 0
//            override fun run() {

                println("counting")

                if (KOTS_Server_Java.orderQueue != null)
                {

                    if (KOTS_Server_Java.orderQueue.peek() != null) {
                        //if available, send the next order to the kitchen view screen

                        updateOrders(KOTS_Server_Java.orderQueue.poll())
                    }


                }


//            }
//        }
//
//       return task

    }




    fun loadLoggedInListeners()
    {
        kitchenObservationVBox.setOnMouseClicked {

            println("click count: "+it.clickCount)
            if(it.clickCount == 2)
            {
                //send source node to disable on transition
                //send returnToNode node, to know what view to put back on the stack after hide animation
                //here source node is same as returnToView
                showSideBarMenu(sideBarWidth.add(0),false).play()

            }

        }

        ordersBtn.setOnMouseClicked {


            //            (it.source as Label).disableProperty().set(true)
            //source not is different from returnToView

            //source > parent > parent
            showSideBarMenuItems(it.source as Node ,itemsListWidth.add(sideBarWidth),false).play()
        }


        signInOutBtn.setOnMouseClicked {

            //stop server
            KOTS_Server_Java.stopServer()
            //set isLogging out flag
            isLoggingOut = true

            //unload event listeners
            unloadLoggedInListeners()

            //~~hide the sideBarMenu, then show the login panel~~

            //hide sideBarMenu
            val ttSideBar = hideSideBarMenu(sideBarWidth.add(0))

            //slideDown the loginPanel
            val ttLoginPanel = showLoginPanel(loginPanelHeight.add(headerBarHeight).add(20),false)
            sequentialTransition {
                children.addAll(ttSideBar,ttLoginPanel)

                setOnFinished {
                    //disable the kitchen observation view
                    kitchenObservationVBox.disableProperty().set(true)
                    isLoggingOut = false
                }
            }


        }


        employeeManagerBtn.setOnMouseClicked {
            //if docked or visible, do not open
            if(employeeManagerView.isDocked) return@setOnMouseClicked
            employeeManagerView.openWindow(stageStyle = StageStyle.UTILITY)
        }


    }

    fun unloadLoggedInListeners()
    {
        kitchenObservationVBox.setOnMouseClicked { null }
        ordersBtn.setOnMouseClicked { null }
        signInOutBtn.setOnMouseClicked { null }
        employeeManagerBtn.setOnMouseClicked { null }
    }

    fun initClock()
    {

        val timeline = Timeline()

        val keyFrame = KeyFrame(
                Duration.seconds(1.0),
                EventHandler<ActionEvent> {

                    val cal = Calendar.getInstance()


                     val date = Date();

                    val strDateFormat = "HH:mm:ss a";
                    val simpleDateFormatted = SimpleDateFormat(strDateFormat);
                    val strDate = simpleDateFormatted.format(date)

                    val second = strDate.substring(6,8)
                    val minute = strDate.substring(3,5)
                    val hour = strDate.substring(0,2).toInt() % 12
                    val period = strDate.substring(9)


                    timeLabel.text =  "$hour : $minute : $second $period"

                }
        )

        timeline.keyFrames.addAll(keyFrame)
        timeline.cycleCount = Timeline.INDEFINITE
        timeline.play()

    }




    fun showLoginPanel( boundedValue : DoubleBinding, reverse : Boolean) : TranslateTransition
    {
        //unbind yTranslate Value
        kotsLoginPanelAnchorPane.translateYProperty().unbind()

        //move overlay's to the last view in the stack
        root.getChildList()!!.move(orderDetailsOverlayAnchorPane, root.getChildList()!!.lastIndex)

        val tt = TranslateTransition()


        tt.setOnFinished {


            //if not initial load enable this
            if(!isInitialLoad && !isLoggingOut)
            {
                //create focus change listener for orverlay Panel View
                kotsLoginPanelAnchorPane.setOnMouseClicked {

                    //if has click, but this does not, rebind, and hide menu
                    if (it.clickCount == 2 )
                    {

                        hideLoginPanel(boundedValue).play()
                    }
            }

            }




        }
        tt.node = kotsLoginPanelAnchorPane
        tt.toY = 0.0
//        tt.delay = 1.seconds
        tt.duration = 0.3.seconds
        tt.cycleCount = 1

        return tt

    }

    //hide menu that were previously hidden, requires rebinding given from showSideBarMenu()
    fun hideLoginPanel(boundedValue: DoubleBinding) : TranslateTransition
    {

        //disable during animation/processing click
        kotsLoginPanelAnchorPane.disableProperty().set(true)

        val tt = TranslateTransition()


        tt.setOnFinished {



            kotsLoginPanelAnchorPane.translateYProperty().unbind()
            //re-bind yTranslate Value
            kotsLoginPanelAnchorPane.translateYProperty().bind(-boundedValue)

            //detach listeners on node created in showSideBarMenu onFinishFunction
            kotsLoginPanelAnchorPane.setOnMouseClicked { null }

            //re enable node even though its out of view range
            kotsLoginPanelAnchorPane.disableProperty().set(false)

            //move overlay View back to front of stack unless this is initialLoad, then move kitchen Observation to front
            if(isInitialLoad){
                loadLoggedInListeners()
                root.getChildList()!!.move(kitchenObservationVBox, root.getChildList()!!.lastIndex)
                isInitialLoad = false
            }
            else{
                root.getChildList()!!.move(orderDetailsOverlayAnchorPane, root.getChildList()!!.lastIndex)
            }


        }

        tt.node = kotsLoginPanelAnchorPane
        tt.byY = -boundedValue.get()
//        tt.delay = 1.seconds
        tt.duration = 0.5.seconds
        tt.cycleCount = 1




        return tt
    }

    fun showSideBarMenu( boundedValue : DoubleBinding, reverse : Boolean) : TranslateTransition
    {
        //disable node so user cannot click mid animation
//        kitchenObservationVBox.disableProperty().set(true)

        //unbind xTranslate Value
        sidebarMenuAnchorPane.translateXProperty().unbind()
        //move overlay's parent to the last view in the stack
        root.getChildList()!!.move(orderDetailsOverlayAnchorPane, root.getChildList()!!.lastIndex)

        val tt = TranslateTransition()


        tt.setOnFinished {


            //create focus change listener for orverlay Panel View
            sidebarMenuAnchorPane.setOnMouseClicked {

                //if has click, but this does not, rebind, and hide menu
                if (it.clickCount == 2 )
                {

                    hideSideBarMenu(boundedValue).play()
                }
            }



        }
        tt.node = sidebarMenuAnchorPane
        tt.toX = 0.0
//        tt.delay = 1.seconds
        tt.duration = 0.3.seconds
        tt.cycleCount = 1

        return tt

    }

    //hide menu that were previously hidden, requires rebinding given from showSideBarMenu()
    fun hideSideBarMenu(boundedValue: DoubleBinding) : TranslateTransition
    {

        //disable during animation/processing click
        kitchenObservationVBox.disableProperty().set(true)

        val tt = TranslateTransition()


        tt.setOnFinished {
            if(isLoggingOut)
            {
                //move overlay View back to front of stack if logging out, else...
                root.getChildList()!!.move(orderDetailsOverlayAnchorPane, root.getChildList()!!.lastIndex)
            }else{
                //...move kitchen Observation View back to front of stack
                root.getChildList()!!.move(kitchenObservationVBox, root.getChildList()!!.lastIndex)
            }



            sidebarMenuAnchorPane.translateXProperty().unbind()
            //re-bind xTranslate Value
            sidebarMenuAnchorPane.translateXProperty().bind(boundedValue)

            //detach listeners on node created in showSideBarMenu onFinishFunction
            sidebarMenuAnchorPane.setOnMouseClicked { null }

            //re enable node thats about to be seen
            kitchenObservationVBox.disableProperty().set(false)


        }
        tt.node = sidebarMenuAnchorPane
        tt.toX = boundedValue.get()
//        tt.delay = 1.seconds
        tt.duration = 0.5.seconds
        tt.cycleCount = 1




        return tt
    }

    fun showSideBarMenuItems( returnToView : Node,boundedValue : DoubleBinding, reverse : Boolean) : TranslateTransition
    {
        //disable the orders button when showing this elm
        sidebarMenuAnchorPane.disableProperty().set(true)

        //unbind xTranslate Value
        menuItemsListAnchorPane.translateXProperty().unbind()

        //move overlay's parent to the last view in the stack
        root.getChildList()!!.move(orderDetailsOverlayAnchorPane, root.getChildList()!!.lastIndex)

        val tt = TranslateTransition()


        tt.setOnFinished {


            //create focus change listener
            menuItemsListAnchorPane.setOnMouseClicked {


                //if parent has click, but this does not, rebind, and hide menu
                if (it.clickCount == 2 )
                {

                    //hide sideBarAnchorPane AND...Pass mouse event to detach in the future (on hiding element)
//                    val ttForSideBar = hideSideBarMenu(sourceNode,returnToView,boundedValue)
                    //...hide menuItemsListAnchorPane
                    hideSideBarMenuItemsList(returnToView,boundedValue).play()

                }
            }
        }
        tt.node = menuItemsListAnchorPane
        tt.toX = 0.0
//        tt.delay = 1.seconds
        tt.duration = 0.3.seconds
        tt.cycleCount = 1

        return  tt

    }

    //hide menu that were previously hidden, requires rebinding given from showSideBarMenu()
    fun hideSideBarMenuItemsList( returnToView: Node, boundedValue: DoubleBinding) : TranslateTransition
    {

        val tt = TranslateTransition()


        tt.setOnFinished {
            //move kitchen Observation View back to front of stack
            root.getChildList()!!.move(orderDetailsOverlayAnchorPane, root.getChildList()!!.lastIndex)


            menuItemsListAnchorPane.translateXProperty().unbind()
            //re-bind xTranslate Value
            menuItemsListAnchorPane.translateXProperty().bind(boundedValue)

            //detach listeners on node created in showSideBarMenu onFinishFunction
            menuItemsListAnchorPane.setOnMouseClicked { null }

            //re enable node even though its out of view range
            sidebarMenuAnchorPane.disableProperty().set(false)
        }
        tt.node = menuItemsListAnchorPane
        tt.toX = boundedValue.get()
//        tt.delay = 1.seconds
        tt.duration = 0.5.seconds
        tt.cycleCount = 1

        return tt



    }


    fun inHierarchy(node: Node?, potentialHierarchyElement: Node?): Boolean {
        var node = node
        if (potentialHierarchyElement == null) {
            return true
        }
        while (node != null) {
            if ( node === potentialHierarchyElement) {
                return true
            }
            node = node.parent
        }
        return false
    }



    //move all side elements off of the screen to slide in on use
    fun initializeViewLayout(testing: Boolean)
    {

        //get width of all overlay panes


        //move all overlay panels off of the screen
        println("width: "+detailWidth+itemsListWidth+sideBarWidth)
        itemDetailAnchorPane.translateXProperty().bind(detailWidth.add(itemsListWidth).add(sideBarWidth))
        menuItemsListAnchorPane.translateXProperty().bind(itemsListWidth.add(sideBarWidth))
        sidebarMenuAnchorPane.translateXProperty().bind(sideBarWidth)
        if(testing) kotsLoginPanelAnchorPane.translateYProperty().bind(-(loginPanelHeight.add(headerBarHeight).add(20)))


//        //get widths of all login panes
//        val loginPaneWidthHeight = kotsLoginPanelAnchorPane.heightProperty()
        //move all login container anchor pane  off of the screen
//        kotsLoginPanelAnchorPane.translateYProperty().bind(-loginPaneWidthHeight)


        //now make the las view in the stack the kitchenObservationVBox
        if(testing) root.getChildList()!!.move(kitchenObservationVBox,root.getChildList()!!.lastIndex)
        else root.getChildList()!!.move(orderDetailsOverlayAnchorPane,root.getChildList()!!.lastIndex)

    }
    fun testOrderChangeAnimation()
    {


        val timer = Timer("schedule", true);

// schedule a single event
        val clearColor = Color.rgb(0,0,0,0.0)
        val greenColor =  Color.web("#55FF00")
        val yellowColor = Color.web("#FFEC00")
        val redColor = Color.RED
        val borderDuration = 0.1
        val borderCycleCount = 7
        val translationDuration = 1.0
        val transCycleCount = 10


        timer.schedule(1000) {


            val colorChangeTransition = ParallelTransition()
            colorChangeTransition.cycleCount = borderCycleCount

            colorChangeTransition.children.addAll(
                    changeBorderColor(clearColor,orderOneVBox,borderDuration),
                    changeBorderColor(greenColor,orderTwoVBox,borderDuration),
                    changeBorderColor(yellowColor,orderThreeVBox,borderDuration),
                    changeBorderColor(redColor,orderFourVBox,borderDuration)
            )


            val menuTranslation = animateBoxTranslation(orderOneVBox,orderTwoVBox,orderThreeVBox,orderFourVBox,translationDuration,transCycleCount)
//            menuTranslation.play()
            parallelTransition {

                cycleCount = 10
                children.addAll(colorChangeTransition,menuTranslation)

            }



        }


    }


    fun updateOrders(newOrder: KOTS_Order)
    {


        // schedule a single event
        val clearColor = Color.rgb(0,0,0,0.0)
        val greenColor =  Color.web("#55FF00")
        val yellowColor = Color.web("#FFEC00")
        val redColor = Color.RED
        val borderDuration = 0.1
        val borderCycleCount = 7
        val translationDuration = 1.0
        val transCycleCount = 10



            val colorChangeTransition = ParallelTransition()
            colorChangeTransition.cycleCount = borderCycleCount

            colorChangeTransition.children.addAll(
                    changeBorderColor(clearColor,orderOneVBox,borderDuration),
                    changeBorderColor(greenColor,orderTwoVBox,borderDuration),
                    changeBorderColor(yellowColor,orderThreeVBox,borderDuration),
                    changeBorderColor(redColor,orderFourVBox,borderDuration)
            )


            val menuTranslation = animateBoxTranslation(orderOneVBox,orderTwoVBox,orderThreeVBox,orderFourVBox,translationDuration,transCycleCount)
//            menuTranslation.play()
            parallelTransition {
                cycleCount = 2


                children.addAll(colorChangeTransition,menuTranslation)

                var newOrderItemsString = ""
                newOrder.itemsList.forEach {
                    newOrderItemsString +=  it.name+"\n"
                }
                setOnFinished {

                    //update the onscreen labels with the new order information
                    runAsync {
                        ui {
                            //old order one
                            val oldOrderOne = orderOneTextLabel.text
                            //store new order one
                            orderOneTextLabel.text = newOrderItemsString
                            //save old order two, then push old order one to text Label two
                            val oldOrderTwo = orderTwoTextLabel.text
                            orderTwoTextLabel.text = oldOrderOne
                            //save old order three, the push old order two to text label three
                            val oldOrderThree = orderThreeTextLabel.text
                            orderThreeTextLabel.text = oldOrderTwo
                            //drop old order four, and push old order three to text label four
                            orderFourTextLabel.text = oldOrderThree
                        }
                    }


                }

            }
    }

    fun changeBorderColor(toNewColor: Color, onOrderVbox: VBox, durationInSeconds: Double) : SequentialTransition
    {




        //get original border colors
        val origBorderColor = onOrderVbox.border.strokes[0].bottomStroke
        val bS = onOrderVbox.border.strokes[0]

        val fadeOut = FadeTransition().apply {

            setOnFinished {
                println("first cycle count"+cycleCount)

                //change color to new colors
                onOrderVbox.border = Border(BorderStroke(toNewColor, bS.topStyle, bS.radii, bS.widths))
            }

            //fade out animation
            node = onOrderVbox
            duration = Duration.seconds(durationInSeconds)
            cycleCount = 1
            fromValue = 1.0
            toValue = 0.0
        }


        val fadeIn = FadeTransition().apply {

            setOnFinished {
                println("second cycle count"+cycleCount)
            }


            cycleCount = 1
            node = onOrderVbox
            duration = Duration.seconds(durationInSeconds)
            fromValue = 0.0
            toValue = 1.0
        }

        val fadeBackOut = FadeTransition().apply {

            setOnFinished {
                println("third cycle count"+cycleCount)

                //change color to original colors
                onOrderVbox.border = Border(BorderStroke(origBorderColor, bS.topStyle, bS.radii, bS.widths))
            }

            //fade out animation
            node = onOrderVbox
            duration = Duration.seconds(durationInSeconds)
            cycleCount = 1
            fromValue = 1.0
            toValue = 0.0
        }

        val fadeBackIn = FadeTransition().apply {

            setOnFinished {
                println("fourth cycle count"+cycleCount)

            }

            cycleCount = 1
            node = onOrderVbox
            duration = Duration.seconds(durationInSeconds)
            fromValue = 0.0
            toValue = 1.0
        }

        val seq = SequentialTransition()
        seq.children.addAll(fadeOut,fadeIn,fadeBackOut,fadeBackIn)

        return seq

    }

    fun animateBoxTranslation(box1: VBox, box2: VBox, box3:VBox, box4 : VBox, durationInSeconds: Double, myCycleCount : Int) : ParallelTransition
    {
        //DO NOT CHANGE!
        val OFFSET = 68
        val forBx1 = box1.parentToLocal(box2.boundsInParent.minX,box2.boundsInParent.minY)
        val forBx2 = box2.parentToLocal(box3.boundsInParent.minX,box3.boundsInParent.minY)
        val forBx3 = box3.parentToLocal(box4.boundsInParent.minX,box4.boundsInParent.minY)


        val box1Trans = TranslateTransition().apply {
            cycleCount = 1
            node = box1
            duration = Duration.seconds(durationInSeconds)

//        fromX = box1.boundsInLocal.minX

            toX = forBx1.x
            toY =  forBx1.y

        }
        val box2Trans = TranslateTransition().apply {
            cycleCount = 1
            node = box2
            duration = Duration.seconds(durationInSeconds)

//        fromX = box2.boundsInLocal.minX

            toX = forBx2.x
            toY =  forBx2.y
        }


        val box3Trans = TranslateTransition().apply {
            cycleCount = 1
            node = box3
            duration = Duration.seconds(durationInSeconds)

//        fromX = box3.boundsInLocal.minX
            toX = forBx3.x
            toY =  forBx3.y
        }

        val box4Trans = TranslateTransition().apply {
            cycleCount = 1
            node = box4
            duration = Duration.seconds(durationInSeconds)
//        fromX = box4.boundsInLocal.minX
            byX = box4.width+OFFSET

        }

        val parTrans = ParallelTransition()
        parTrans.isAutoReverse = true

        parTrans.cycleCount = myCycleCount
        parTrans.children.addAll(box1Trans,box2Trans,box3Trans,box4Trans)

        return  parTrans
    }





    fun customeInterpolationAnimator(order1: VBox)
    {
        val content = "Lorem ipsum"
        val text = Text(10.0, 20.0, "")

        val transition = object : Transition() {
            init {
                cycleDuration = 2.seconds
                cycleCount = Int.MAX_VALUE
            }

            override fun interpolate(frac: Double)
            {
                val length = content.length
                val n = Math.round(length * frac.toFloat())
                println("n: $n")
                println("frac $frac")
                val ds = BorderStroke(Color.rgb((frac*1).toInt(),((255/2)*frac).toInt(),3),BorderStrokeStyle.SOLID,CornerRadii(frac),BorderWidths(10.0))
                val dse = Border(ds)
                order1.borderProperty().set(dse)
            }

        }

        order1.add(text)

        transition.play();
    }


    fun signOn()
    {
        if(usernameTextField.text != "" || usernameTextField != null &&
                passwordTextField.text != "" || passwordTextField.text != null)
        {
            val db = DB()

        }
    }

}






public class KOTS : App(KitchenOrderQueueView::class)

fun main(args: Array<String>)
{

    Application.launch(KOTS::class.java, *args)
    SvgImageLoaderFactory.install(PrimitiveDimensionProvider())
}

fun blinkAnimation(view : StackPane)
{
    val endPos = view.layoutXProperty().add(200.0)
//    view.translateXProperty().animate(endValue = endPos.value, duration = 5.seconds)
//    view.translateYProperty().animate(endValue = endPos.value, duration = 10.seconds)
    val f = StrokeTransition(5.seconds, view.shape, Color.ALICEBLUE, Color.BISQUE)
    val ball = Circle(100.0, 100.0, 20.0)
    val tl = Timeline()
    var dx = 1
    var dy = 1
    tl.cycleCount = Animation.INDEFINITE





}


