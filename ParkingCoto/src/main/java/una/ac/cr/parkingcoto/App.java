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
    private static final ParkingContext parkingContext = new ParkingContext(new ParkingLot());

    @Override
    public void start(Stage stage) throws IOException {

        scene = new Scene(loadFXML("EntryView"), 1200, 750);

        stage.setTitle("Parking Coto");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/view/" + fxml + ".fxml")
        );
        loader.setControllerFactory(App::createController);

        return loader.load();
    }

    private static Object createController(Class<?> controllerType) {
        try {
            Constructor<?> contextConstructor = controllerType.getDeclaredConstructor(ParkingContext.class);
            return contextConstructor.newInstance(parkingContext);
        } catch (NoSuchMethodException exception) {
            try {
                return controllerType.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException creationException) {
                throw new IllegalStateException("Unable to create controller: " + controllerType.getName(),
                        creationException);
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to create controller: " + controllerType.getName(), exception);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}