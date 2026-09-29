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

    private static final ParkingContext parkingContext =
            new ParkingContext(
                    new ParkingLot()
            );

    @Override
    public void start(Stage stage) throws IOException {

        Parent root =
                loadFXML(
                        "MainView"
                );

        scene =
                new Scene(
                        root,
                        1200,
                        700
                );

        stage.setTitle(
                "Parking Coto"
        );

        stage.setScene(
                scene
        );

        stage.setResizable(
                false
        );

        stage.centerOnScreen();

        stage.show();
    }

    /**
     * Replaces the root node of the current scene.
     *
     * @param fxml name of the FXML resource without the {@code .fxml} extension
     * @throws IOException if the FXML resource cannot be loaded
     */
    public static void setRoot(
            String fxml
    ) throws IOException {
        scene.setRoot(
                loadFXML(
                        fxml
                )
        );
    }

    /**
     * Loads an FXML resource from the application's view directory.
     *
     * @param fxml resource name without the {@code .fxml} extension
     * @return the loaded JavaFX root node
     * @throws IOException if the FXML resource cannot be loaded
     */
    public static Parent loadFXML(
            String fxml
    ) throws IOException {
        FXMLLoader loader =
                new FXMLLoader(
                        App.class.getResource(
                                "/view/"
                                + fxml
                                + ".fxml"
                        )
                );

        loader.setControllerFactory(
                App::createController
        );

        return loader.load();
    }

    /**
     * Creates a controller using the shared parking context when supported.
     *
     * @param controllerType controller class to instantiate
     * @return the instantiated controller
     * @throws IllegalStateException if the controller cannot be instantiated
     */
    private static Object createController(
            Class<?> controllerType
    ) {

        try {

            Constructor<?> contextConstructor =
                    controllerType
                            .getDeclaredConstructor(
                                    ParkingContext.class
                            );

            return contextConstructor
                    .newInstance(
                            parkingContext
                    );

        } catch (NoSuchMethodException exception) {

            try {

                return controllerType
                        .getDeclaredConstructor()
                        .newInstance();

            } catch (
                    ReflectiveOperationException
                    creationException
            ) {

                throw new IllegalStateException(
                        "Unable to create controller: "
                        + controllerType.getName(),
                        creationException
                );
            }

        } catch (
                ReflectiveOperationException exception
        ) {

            throw new IllegalStateException(
                    "Unable to create controller: "
                    + controllerType.getName(),
                    exception
            );
        }
    }

    public static void main(
            String[] args
    ) {

        launch(
                args
        );
    }
}
