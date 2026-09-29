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

    private final RegistrationService registrationService;
    private final QueryService queryService;

    private static final String TYPE_CAR =
            "Autom├│vil";

    private static final String TYPE_MOTORCYCLE =
            "Motocicleta";

    private static final String TYPE_CARGO =
            "Veh├¡culo de carga";

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

    @FXML
    private JFXTextField TF_SEARCH_VEHICLE;

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

    private final ObservableList<Vehicle> vehicles =
            FXCollections.observableArrayList();

    private FilteredList<Vehicle> filteredVehicles;

    private String recentlyRegisteredPlate;

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

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle) {

        configureVehicleTypes();

        configureTable();

        configureSearch();

        loadVehicles();
    }

    private void configureVehicleTypes() {

        CB_TYPE_VEHICLE.setItems(
                FXCollections.observableArrayList(
                        TYPE_CAR,
                        TYPE_MOTORCYCLE,
                        TYPE_CARGO
                )
        );
    }

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

        configureRowAnimations();
    }

    private void configureRowAnimations() {

        TV_REGISTERED_VEHICLES.setRowFactory(
                tableView -> {

                    TableRow<Vehicle> row =
                            new TableRow<>();

                    row.itemProperty().addListener(
                            (
                                    observable,
                                    oldVehicle,
                                    newVehicle
                            ) -> {

                                if (newVehicle == null) {
                                    return;
                                }

                                if (recentlyRegisteredPlate != null
                                        && recentlyRegisteredPlate
                                                .equalsIgnoreCase(
                                                        newVehicle
                                                                .getLicensePlate()
                                                )) {

                                    animateNewVehicleRow(
                                            row
                                    );

                                    recentlyRegisteredPlate = null;
                                }
                            }
                    );

                    return row;
                }
        );
    }

    private void animateNewVehicleRow(
            TableRow<Vehicle> row) {

        row.setOpacity(
                0.0
        );

        row.setTranslateY(
                8.0
        );

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

        fade.play();

        movement.play();
    }

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

    @FXML
    private void RegisterVehicle(
            ActionEvent event) {

        try {

            Vehicle vehicle =
                    createVehicleFromForm();

            registrationService.registerVehicle(
                    vehicle
            );

            recentlyRegisteredPlate =
                    vehicle.getLicensePlate();

            loadVehicles();

            scrollToVehicle(
                    vehicle
            );

            clearForm();

            showInformation(
                    "Veh├¡culo registrado",
                    "El veh├¡culo "
                    + vehicle.getLicensePlate()
                    + " fue registrado correctamente."
            );

        } catch (RuntimeException exception) {

            showError(
                    "No se pudo registrar el veh├¡culo",
                    exception.getMessage()
            );
        }
    }

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
                    "Debe seleccionar un tipo de veh├¡culo."
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
                        "Tipo de veh├¡culo no v├ílido."
                );
        }
    }

    private void loadVehicles() {

        List<Vehicle> registeredVehicles =
                queryService.getVehicles();

        vehicles.setAll(
                registeredVehicles
        );

        TV_REGISTERED_VEHICLES.refresh();
    }

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
