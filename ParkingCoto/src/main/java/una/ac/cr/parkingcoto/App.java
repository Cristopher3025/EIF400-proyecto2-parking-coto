package una.ac.cr.parkingcoto;

import java.io.IOException;
import java.lang.reflect.Constructor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import model.parking.ParkingLot;
import service.ParkingContext;

public class App extends Application {

    private static Scene scene;

    /*
     * Single application context.
     *
     * All controllers that require ParkingContext receive this
     * exact same instance.
     */
    private static final ParkingContext parkingContext =
            new ParkingContext(new ParkingLot());

    @Override
    public void start(Stage stage) throws IOException {

        scene = new Scene(
                loadFXML("MainView"),
                1200,
                750
        );

        stage.setTitle("Parking Coto");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Replaces the root of the current Scene.
     *
     * This method is available if a complete scene change is ever
     * required. Normal application navigation will occur inside
     * MainView.
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads an FXML located in src/main/resources/view.
     *
     * Every controller is created using the application's
     * ControllerFactory.
     *
     * @param fxml file name without the .fxml extension
     * @return loaded JavaFX hierarchy
     * @throws IOException if the FXML cannot be loaded
     */
    public static Parent loadFXML(String fxml) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                App.class.getResource(
                        "/view/" + fxml + ".fxml"
                )
        );

        loader.setControllerFactory(
                App::createController
        );

        return loader.load();
    }

    /**
     * Creates controllers used by FXMLLoader.
     *
     * If the controller declares a constructor that receives
     * ParkingContext, the shared application context is injected.
     *
     * Otherwise, the default constructor is used.
     */
    private static Object createController(Class<?> controllerType) {

        try {

            Constructor<?> contextConstructor =
                    controllerType.getDeclaredConstructor(
                            ParkingContext.class
                    );

            return contextConstructor.newInstance(
                    parkingContext
            );

        } catch (NoSuchMethodException exception) {

            try {

                return controllerType
                        .getDeclaredConstructor()
                        .newInstance();

            } catch (ReflectiveOperationException creationException) {

                throw new IllegalStateException(
                        "Unable to create controller: "
                        + controllerType.getName(),
                        creationException
                );
            }

        } catch (ReflectiveOperationException exception) {

            throw new IllegalStateException(
                    "Unable to create controller: "
                    + controllerType.getName(),
                    exception
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}