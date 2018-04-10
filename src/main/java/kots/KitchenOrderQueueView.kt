package kots

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider
import javafx.application.Application
import tornadofx.*
import javafx.scene.paint.Color
import javafx.animation.*
import javafx.scene.shape.Circle
import javafx.animation.Animation
import javafx.animation.Timeline
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.util.Duration
import java.util.*
import kotlin.concurrent.schedule



class KitchenOrderQueueView : View()
{
    val kitchenOrderQVIew: StackPane by fxml("/fxml/kitchenOrderQueue_UI.fxml")
    override val root = kitchenOrderQVIew
    val orderOneVBox : VBox by fxid()
    val orderTwoVBox : VBox by fxid()
    val orderThreeVBox : VBox by fxid()
    val orderFourVBox : VBox by fxid()

    val gridPane : GridPane by fxid()
    var t : TimerTask? = null

    init
    {



        root.vgrow = Priority.ALWAYS

//            testAnimation()

    }

    fun testAnimation()
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


