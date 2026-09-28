package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import model.vehicle.Car;
import model.vehicle.CargoVehicle;
import model.vehicle.Motorcycle;
import model.vehicle.Vehicle;

import service.ParkingContext;
import service.QueryService;
import service.RegistrationService;

public class VehiclesViewController implements Initializable {

    // =========================================================
    // SERVICES
    // =========================================================

    private final RegistrationService registrationService;
    private final QueryService queryService;


    // =========================================================
    // VEHICLE TYPES
    // =========================================================

    private static final String TYPE_CAR =
            "Automóvil";

    private static final String TYPE_MOTORCYCLE =
            "Motocicleta";

    private static final String TYPE_CARGO =
            "Vehículo de carga";


    // =========================================================
    // FORM
    // =========================================================

    @FXML
    private AnchorPane AP_VEHICLES;

    @FXML
    private MFXTextField TF_PLATE;

    @FXML
    private MFXTextField TF_BRAND;

    @FXML
    private MFXTextField TF_MODEL;

    @FXML
    private MFXTextField TF_COLOR;

    @FXML
    private MFXComboBox<String> CB_TYPE_VEHICLE;

    @FXML
    private JFXButton BTN_REGISTER;


    // =========================================================
    // SEARCH
    // =========================================================

    @FXML
    private JFXTextField TF_SEARCH_VEHICLE;


    // =========================================================
    // TABLE
    // =========================================================

    @FXML
    private TableView<Vehicle> TV_REGISTERED_VEHICLES;

    @FXML
    private TableColumn<Vehicle, String> TV_RW_PLATE;

    @FXML
    private TableColumn<Vehicle, String> TV_RW_BRAND;

    @FXML
    private TableColumn<Vehicle, String> TV_RW_MODEL;

    @FXML
    private TableColumn<Vehicle, String> TV_RW_COLOR;

    @FXML
    private TableColumn<Vehicle, String> TV_RW_TYPE_VEHICLE;


    // =========================================================
    // TABLE DATA
    // =========================================================

    private final ObservableList<Vehicle> vehicles =
            FXCollections.observableArrayList();

    private FilteredList<Vehicle> filteredVehicles;


    /*
     * Stores the license plate of the vehicle that was
     * just registered.
     *
     * When JavaFX creates the row corresponding to this
     * vehicle, that row will receive the cinematic animation.
     */
    private String recentlyRegisteredPlate;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VehiclesViewController(
            ParkingContext context) {

        Objects.requireNonNull(
                context,
                "Parking context cannot be null"
        );

        this.registrationService =
                context.getRegistrationService();

        this.queryService =
                context.getQueryService();
    }


    // =========================================================
    // INITIALIZATION
    // =========================================================

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle) {

        configureVehicleTypes();

        configureTable();

        configureSearch();

        loadVehicles();
    }


    // =========================================================
    // COMBO BOX
    // =========================================================

    private void configureVehicleTypes() {

        CB_TYPE_VEHICLE.setItems(
                FXCollections.observableArrayList(
                        TYPE_CAR,
                        TYPE_MOTORCYCLE,
                        TYPE_CARGO
                )
        );
    }


    // =========================================================
    // TABLE
    // =========================================================

    private void configureTable() {

        // =====================================================
        // PLATE
        // =====================================================

        TV_RW_PLATE.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                cellData
                                        .getValue()
                                        .getLicensePlate()
                        )
        );


        // =====================================================
        // BRAND
        // =====================================================

        TV_RW_BRAND.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                cellData
                                        .getValue()
                                        .getBrand()
                        )
        );


        // =====================================================
        // MODEL
        // =====================================================

        TV_RW_MODEL.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                cellData
                                        .getValue()
                                        .getModel()
                        )
        );


        // =====================================================
        // COLOR
        // =====================================================

        TV_RW_COLOR.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                cellData
                                        .getValue()
                                        .getColor()
                        )
        );


        // =====================================================
        // VEHICLE TYPE
        // =====================================================

        TV_RW_TYPE_VEHICLE.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                getVehicleTypeName(
                                        cellData.getValue()
                                )
                        )
        );


        // =====================================================
        // FILTERED LIST
        // =====================================================

        filteredVehicles =
                new FilteredList<>(
                        vehicles,
                        vehicle -> true
                );


        TV_REGISTERED_VEHICLES.setItems(
                filteredVehicles
        );


        // =====================================================
        // CINEMATIC ROW FACTORY
        // =====================================================

        configureRowAnimations();
    }


    // =========================================================
    // ROW ANIMATIONS
    // =========================================================

    private void configureRowAnimations() {

        TV_REGISTERED_VEHICLES.setRowFactory(
                tableView -> {

                    TableRow<Vehicle> row =
                            new TableRow<>();


                    /*
                     * A TableRow is reused internally by JavaFX.
                     *
                     * Therefore we listen for changes in the item
                     * instead of assuming that a row always
                     * represents the same vehicle.
                     */
                    row.itemProperty().addListener(
                            (
                                    observable,
                                    oldVehicle,
                                    newVehicle
                            ) -> {

                                if (newVehicle == null) {
                                    return;
                                }


                                /*
                                 * Only animate the vehicle that
                                 * has JUST been registered.
                                 */
                                if (recentlyRegisteredPlate != null
                                        && recentlyRegisteredPlate
                                                .equalsIgnoreCase(
                                                        newVehicle
                                                                .getLicensePlate()
                                                )) {

                                    animateNewVehicleRow(
                                            row
                                    );


                                    /*
                                     * Remove the marker so the row
                                     * does not animate repeatedly.
                                     */
                                    recentlyRegisteredPlate = null;
                                }
                            }
                    );

                    return row;
                }
        );
    }


    // =========================================================
    // NEW VEHICLE ROW ANIMATION
    // =========================================================

    private void animateNewVehicleRow(
            TableRow<Vehicle> row) {

        /*
         * Start almost invisible.
         */
        row.setOpacity(
                0.0
        );


        /*
         * Start slightly below its final position.
         *
         * This is deliberately small because we do NOT
         * want elements jumping around the interface.
         */
        row.setTranslateY(
                8.0
        );


        // =====================================================
        // FADE
        // =====================================================

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(500),
                        row
                );

        fade.setFromValue(
                0.0
        );

        fade.setToValue(
                1.0
        );


        // =====================================================
        // MOVEMENT
        // =====================================================

        TranslateTransition movement =
                new TranslateTransition(
                        Duration.millis(500),
                        row
                );

        movement.setFromY(
                8.0
        );

        movement.setToY(
                0.0
        );


        // =====================================================
        // PLAY
        // =====================================================

        fade.play();

        movement.play();
    }


    // =========================================================
    // SEARCH
    // =========================================================

    private void configureSearch() {

        TF_SEARCH_VEHICLE
                .textProperty()
                .addListener(
                        (
                                observable,
                                oldValue,
                                newValue
                        ) ->
                                filterVehicles(
                                        newValue
                                )
                );
    }


    private void filterVehicles(
            String searchText) {

        if (searchText == null
                || searchText.isBlank()) {

            filteredVehicles.setPredicate(
                    vehicle -> true
            );

            return;
        }


        String search =
                searchText
                        .trim()
                        .toLowerCase();


        filteredVehicles.setPredicate(
                vehicle ->

                        vehicle
                                .getLicensePlate()
                                .toLowerCase()
                                .contains(search)

                        ||

                        vehicle
                                .getBrand()
                                .toLowerCase()
                                .contains(search)

                        ||

                        vehicle
                                .getModel()
                                .toLowerCase()
                                .contains(search)
        );
    }


    // =========================================================
    // REGISTER VEHICLE
    // =========================================================

    @FXML
    private void RegisterVehicle(
            ActionEvent event) {

        try {

            // =================================================
            // CREATE VEHICLE
            // =================================================

            Vehicle vehicle =
                    createVehicleFromForm();


            // =================================================
            // REGISTER
            // =================================================

            registrationService.registerVehicle(
                    vehicle
            );


            /*
             * IMPORTANT:
             *
             * Save the plate BEFORE reloading the TableView.
             *
             * When JavaFX detects the new vehicle and creates
             * its row, configureRowAnimations() will recognize
             * this plate and animate only that row.
             */
            recentlyRegisteredPlate =
                    vehicle.getLicensePlate();


            // =================================================
            // RELOAD TABLE
            // =================================================

            loadVehicles();


            /*
             * Scroll to the registered vehicle.
             *
             * This is useful once the table contains many
             * vehicles.
             */
            scrollToVehicle(
                    vehicle
            );


            // =================================================
            // CLEAR FORM
            // =================================================

            clearForm();


            // =================================================
            // INFORMATION
            // =================================================

            showInformation(
                    "Vehículo registrado",
                    "El vehículo "
                    + vehicle.getLicensePlate()
                    + " fue registrado correctamente."
            );


        } catch (RuntimeException exception) {

            showError(
                    "No se pudo registrar el vehículo",
                    exception.getMessage()
            );
        }
    }


    // =========================================================
    // SCROLL TO NEW VEHICLE
    // =========================================================

    private void scrollToVehicle(
            Vehicle vehicle) {

        int index =
                filteredVehicles.indexOf(
                        vehicle
                );


        if (index >= 0) {

            TV_REGISTERED_VEHICLES.scrollTo(
                    index
            );
        }
    }


    // =========================================================
    // VEHICLE CREATION
    // =========================================================

    private Vehicle createVehicleFromForm() {

        String plate =
                TF_PLATE.getText();

        String brand =
                TF_BRAND.getText();

        String model =
                TF_MODEL.getText();

        String color =
                TF_COLOR.getText();

        String selectedType =
                CB_TYPE_VEHICLE.getValue();


        if (selectedType == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un tipo de vehículo."
            );
        }


        switch (selectedType) {

            // =================================================
            // CAR
            // =================================================

            case TYPE_CAR:

                return new Car(
                        plate,
                        brand,
                        model,
                        color
                );


            // =================================================
            // MOTORCYCLE
            // =================================================

            case TYPE_MOTORCYCLE:

                return new Motorcycle(
                        plate,
                        brand,
                        model,
                        color
                );


            // =================================================
            // CARGO VEHICLE
            // =================================================

            case TYPE_CARGO:

                return new CargoVehicle(
                        plate,
                        brand,
                        model,
                        color
                );


            // =================================================
            // INVALID TYPE
            // =================================================

            default:

                throw new IllegalArgumentException(
                        "Tipo de vehículo no válido."
                );
        }
    }


    // =========================================================
    // LOAD DATA
    // =========================================================

    private void loadVehicles() {

        List<Vehicle> registeredVehicles =
                queryService.getVehicles();


        vehicles.setAll(
                registeredVehicles
        );


        /*
         * Forces JavaFX to visually refresh the table after
         * replacing its backing data.
         */
        TV_REGISTERED_VEHICLES.refresh();
    }


    // =========================================================
    // VEHICLE TYPE DESCRIPTION
    // =========================================================

    private String getVehicleTypeName(
            Vehicle vehicle) {

        switch (vehicle.getRequiredSpaceType()) {

            case CAR:
                return TYPE_CAR;

            case MOTORCYCLE:
                return TYPE_MOTORCYCLE;

            case CARGO:
                return TYPE_CARGO;

            default:
                return "Desconocido";
        }
    }


    // =========================================================
    // FORM
    // =========================================================

    private void clearForm() {

        TF_PLATE.clear();

        TF_BRAND.clear();

        TF_MODEL.clear();

        TF_COLOR.clear();


        CB_TYPE_VEHICLE
                .getSelectionModel()
                .clearSelection();


        TF_PLATE.requestFocus();
    }


    // =========================================================
    // MESSAGES
    // =========================================================

    private void showInformation(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );


        alert.setTitle(
                "Parking Coto"
        );


        alert.setHeaderText(
                title
        );


        alert.setContentText(
                message
        );


        alert.showAndWait();
    }


    private void showError(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );


        alert.setTitle(
                "Parking Coto"
        );


        alert.setHeaderText(
                title
        );


        alert.setContentText(
                message != null
                        ? message
                        : "Ha ocurrido un error inesperado."
        );


        alert.showAndWait();
    }
}