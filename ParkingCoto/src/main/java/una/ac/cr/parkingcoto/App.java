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
     * Contexto único de la aplicación.
     *
     * Todos los controladores que necesiten ParkingContext
     * reciben exactamente esta misma instancia.
     */
    private static final ParkingContext parkingContext =
            new ParkingContext(
                    new ParkingLot()
            );

    @Override
    public void start(Stage stage) throws IOException {

        // =====================================================
        // CARGAR VISTA PRINCIPAL
        // =====================================================

        Parent root =
                loadFXML(
                        "MainView"
                );

        // =====================================================
        // CREAR ESCENA
        // =====================================================

        scene =
                new Scene(
                        root,
                        1200,
                        700
                );

        // =====================================================
        // CONFIGURAR VENTANA
        // =====================================================

        stage.setTitle(
                "Parking Coto"
        );

        stage.setScene(
                scene
        );

        /*
         * Impide que el usuario pueda estirar
         * o redimensionar la ventana.
         */
        stage.setResizable(
                false
        );

        /*
         * Coloca la aplicación en el centro
         * de la pantalla.
         */
        stage.centerOnScreen();

        /*
         * Finalmente mostramos la ventana.
         */
        stage.show();
    }

    // =========================================================
    // CAMBIO COMPLETO DE ESCENA
    // =========================================================

    /**
     * Reemplaza la raíz de la escena actual.
     *
     * En Parking Coto normalmente navegamos dentro de MainView,
     * pero este método queda disponible si en algún momento se
     * necesita reemplazar completamente la interfaz.
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

    // =========================================================
    // CARGA DE FXML
    // =========================================================

    /**
     * Carga un archivo FXML ubicado en:
     *
     * src/main/resources/view
     *
     * @param fxml nombre del archivo sin ".fxml"
     * @return jerarquía JavaFX cargada
     * @throws IOException si el FXML no puede cargarse
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

        /*
         * Utilizamos nuestra propia fábrica para poder
         * inyectar ParkingContext en los controladores.
         */
        loader.setControllerFactory(
                App::createController
        );

        return loader.load();
    }

    // =========================================================
    // CONTROLLER FACTORY
    // =========================================================

    /**
     * Crea automáticamente los controladores.
     *
     * Primero intenta encontrar:
     *
     * Controller(ParkingContext context)
     *
     * Si no existe, utiliza:
     *
     * Controller()
     */
    private static Object createController(
            Class<?> controllerType
    ) {

        try {

            // =================================================
            // CONTROLADOR CON PARKING CONTEXT
            // =================================================

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

            // =================================================
            // CONTROLADOR SIN PARKING CONTEXT
            // =================================================

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

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(
            String[] args
    ) {

        launch(
                args
        );
    }
}