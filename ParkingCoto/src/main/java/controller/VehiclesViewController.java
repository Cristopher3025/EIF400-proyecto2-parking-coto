package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

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

    private static final String TYPE_CAR = "Automóvil";
    private static final String TYPE_MOTORCYCLE = "Motocicleta";
    private static final String TYPE_CARGO = "Vehículo de carga";

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

        TV_RW_PLATE.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                cellData
                                        .getValue()
                                        .getLicensePlate()
                        )
        );

        TV_RW_BRAND.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                cellData
                                        .getValue()
                                        .getBrand()
                        )
        );

        TV_RW_MODEL.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                cellData
                                        .getValue()
                                        .getModel()
                        )
        );

        TV_RW_COLOR.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                cellData
                                        .getValue()
                                        .getColor()
                        )
        );

        TV_RW_TYPE_VEHICLE.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                getVehicleTypeName(
                                        cellData.getValue()
                                )
                        )
        );

        filteredVehicles =
                new FilteredList<>(
                        vehicles,
                        vehicle -> true
                );

        TV_REGISTERED_VEHICLES.setItems(
                filteredVehicles
        );
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private void configureSearch() {

        TF_SEARCH_VEHICLE
                .textProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                filterVehicles(newValue)
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

                        || vehicle
                                .getBrand()
                                .toLowerCase()
                                .contains(search)

                        || vehicle
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

            Vehicle vehicle =
                    createVehicleFromForm();

            registrationService.registerVehicle(
                    vehicle
            );

            loadVehicles();

            clearForm();

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

            case TYPE_CAR:

                return new Car(
                        plate,
                        brand,
                        model,
                        color
                );

            case TYPE_MOTORCYCLE:

                return new Motorcycle(
                        plate,
                        brand,
                        model,
                        color
                );

            case TYPE_CARGO:

                return new CargoVehicle(
                        plate,
                        brand,
                        model,
                        color
                );

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

        CB_TYPE_VEHICLE.getSelectionModel().clearSelection();

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