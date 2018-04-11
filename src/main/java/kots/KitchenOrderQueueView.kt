package kots

import couchdb.DB
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider
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
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Label
import java.text.SimpleDateFormat
import java.util.Calendar





class KitchenOrderQueueView : View()
{
    val kitchenOrderQVIew: StackPane by fxml("/fxml/kitchenOrderQueue_UI.fxml")
    val timeLabel : Label by fxid()
    override val root = kitchenOrderQVIew
    val kitchenObservationVBox  : VBox by fxid()
    val orderOneVBox : VBox by fxid()
    val orderTwoVBox : VBox by fxid()
    val orderThreeVBox : VBox by fxid()
    val orderFourVBox : VBox by fxid()
    val usernameTextField : TextField by fxid()
    val passwordTextField : TextField by fxid()
    val headerBarHBox : HBox by fxid()
    val loginBtn : TextField by fxid()
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


    //statics for overlay layout
    val detailWidth = itemDetailAnchorPane.widthProperty()
    val itemsListWidth = menuItemsListAnchorPane.widthProperty()
    val sideBarWidth = sidebarMenuAnchorPane.widthProperty()
    val loginPanelHeight = kotsLoginPanelAnchorPane.heightProperty()
    val headerBarHeight = headerBarHBox.heightProperty()

    init
    {
        //init view layout for initial view
        initializeViewLayout()

        val dateTime = LocalDateTime.now()
        initClock()

        kitchenObservationVBox.setOnMouseClicked {

            println("click count: "+it.clickCount)
            if(it.clickCount == 2)
            {
                //send source node to disable on transition
                //send returnToNode node, to know what view to put back on the stack after hide animation
                //here source node is same as returnToView
                showSideBarMenu(it.source as Node,sideBarWidth.add(0),false).play()

            }

        }

        orderDetailsOverlayAnchorPane.setOnMouseClicked {

        }

        ordersBtn.setOnMouseClicked {


//            (it.source as Label).disableProperty().set(true)
            //source not is different from returnToView

            //source > parent > parent
            showSideBarMenuItems(it.source as Node ,itemsListWidth.add(sideBarWidth),false).play()
        }

        signInOutBtn.setOnMouseClicked {
            showLoginPanel(kitchenObservationVBox, loginPanelHeight.add(headerBarHeight).add(20), false).play()
        }

        root.vgrow = Priority.ALWAYS


        //for incoming orders
//        testOrderChangeAnimation()

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




    fun showLoginPanel( returnToView : Node,boundedValue : DoubleBinding, reverse : Boolean) : TranslateTransition
    {
        //disable node so user cannot click mid animation
        signInOutBtn.disableProperty().set(true)

        //unbind yTranslate Value
        kotsLoginPanelAnchorPane.translateYProperty().unbind()

        //move overlay's to the last view in the stack
        root.getChildList()!!.move(orderDetailsOverlayAnchorPane, root.getChildList()!!.lastIndex)

        val tt = TranslateTransition()


        tt.setOnFinished {


            //create focus change listener for orverlay Panel View
            kotsLoginPanelAnchorPane.setOnMouseClicked {

                //if has click, but this does not, rebind, and hide menu
                if (it.clickCount == 2 )
                {

                    hideLoginPanel(returnToView,boundedValue).play()
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
    fun hideLoginPanel(returnToView: Node,boundedValue: DoubleBinding) : TranslateTransition
    {

        //disable during animation/processing click
        kotsLoginPanelAnchorPane.disableProperty().set(true)

        val tt = TranslateTransition()


        tt.setOnFinished {



            kotsLoginPanelAnchorPane.translateYProperty().unbind()
            //re-bind xTranslate Value
            kotsLoginPanelAnchorPane.translateYProperty().bind(-boundedValue)

            //detach listeners on node created in showSideBarMenu onFinishFunction
            kotsLoginPanelAnchorPane.setOnMouseClicked { null }

            //re enable node even though its out of view range
            kotsLoginPanelAnchorPane.disableProperty().set(false)

            //move kitchen Observation View back to front of stack
            root.getChildList()!!.move(orderDetailsOverlayAnchorPane, root.getChildList()!!.lastIndex)

            //re-enable th signOutBtn
            signInOutBtn.disableProperty().set(false)
        }

        tt.node = kotsLoginPanelAnchorPane
        tt.byY = -boundedValue.get()
//        tt.delay = 1.seconds
        tt.duration = 0.5.seconds
        tt.cycleCount = 1




        return tt
    }

    fun showSideBarMenu( returnToView : Node,boundedValue : DoubleBinding, reverse : Boolean) : TranslateTransition
    {
        //disable node so user cannot click mid animation
        kitchenObservationVBox.disableProperty().set(true)

        //unbind xTranslate Value
        sidebarMenuAnchorPane.translateXProperty().unbind()
        //move overlay's parent to the last view in the stack
        root.getChildList()!!.move(sidebarMenuAnchorPane.parent, root.getChildList()!!.lastIndex)

        val tt = TranslateTransition()


        tt.setOnFinished {


            //create focus change listener for orverlay Panel View
            sidebarMenuAnchorPane.setOnMouseClicked {

                //if has click, but this does not, rebind, and hide menu
                if (it.clickCount == 2 )
                {

                    hideSideBarMenu(returnToView,boundedValue).play()
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
    fun hideSideBarMenu(returnToView: Node,boundedValue: DoubleBinding) : TranslateTransition
    {

        //disable during animation/processing click
        kitchenObservationVBox.disableProperty().set(true)

        val tt = TranslateTransition()


        tt.setOnFinished {
            //move kitchen Observation View back to front of stack
            root.getChildList()!!.move(kitchenObservationVBox, root.getChildList()!!.lastIndex)


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
    fun initializeViewLayout()
    {

        //get width of all overlay panes


        //move all overlay panels off of the screen
        println("width: "+detailWidth+itemsListWidth+sideBarWidth)
        itemDetailAnchorPane.translateXProperty().bind(detailWidth.add(itemsListWidth).add(sideBarWidth))
        menuItemsListAnchorPane.translateXProperty().bind(itemsListWidth.add(sideBarWidth))
        sidebarMenuAnchorPane.translateXProperty().bind(sideBarWidth)
        kotsLoginPanelAnchorPane.translateYProperty().bind(-(loginPanelHeight.add(headerBarHeight).add(20)))


//        //get widths of all login panes
//        val loginPaneWidthHeight = kotsLoginPanelAnchorPane.heightProperty()
        //move all login container anchor pane  off of the screen
//        kotsLoginPanelAnchorPane.translateYProperty().bind(-loginPaneWidthHeight)


        //now make the las view in the stack the kitchenObservationVBox
        root.getChildList()!!.move(kitchenObservationVBox,root.getChildList()!!.lastIndex)

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


