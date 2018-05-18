package mainhotelapp;

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kots.KOTS_Server_Java;
import kots.KitchenOrderQueueView;

import java.util.Timer;
import java.util.TimerTask;


public class MainApp extends Application
{

    @Override
    public void start(Stage stage) throws Exception
    {

//        Parent root = FXMLLoader.load(getClass().getResource("/fxml/ReservationSystemUI.fxml"));
//
//
//         Scene scene = new Scene(root,463,544);
//
////         scene.getStylesheets().add("/styles/RectBtnStyle.css");
////         FirstKotlinKt.speakKotlin("ss");
//         stage.setTitle(FirstKotlinKt.speakKotlin("JavaFX & Kotlin!"));
//         stage.setScene(scene);
//         stage.show();


    }





    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
//        launch(args);
        launch(MainAppKotlin.class, args);
        SvgImageLoaderFactory.install(new PrimitiveDimensionProvider());
    }






}
