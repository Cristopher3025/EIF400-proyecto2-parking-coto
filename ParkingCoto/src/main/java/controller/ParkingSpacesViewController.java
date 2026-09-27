package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import enums.ParkingSpaceType;

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

import model.parking.ParkingSpace;

import service.ParkingContext;
import service.QueryService;
import service.RegistrationService;

public class ParkingSpacesViewController implements Initializable {

    // =========================================================
    // SERVICES
    // =========================================================

    private final RegistrationService registrationService;
    private final QueryService queryService;

    // =========================================================
    // DISPLAY VALUES
    // =========================================================

    private static final String TYPE_CAR = "Automóvil";
    private static final String TYPE_MOTORCYCLE = "Motocicleta";
    private static final String TYPE_CARGO = "Vehículo de carga";

    // =========================================================
    // FORM
    // =========================================================

    @FXML
    private AnchorPane AP_PARKING_SPACES;

    @FXML
    private MFXTextField TF_NUMBER;

    @FXML
    private MFXComboBox<String> CB_TYPE_VEHICLE;

    @FXML
    private JFXButton BTN_REGISTER;

    // =========================================================
    // SEARCH
    // =========================================================

    @FXML
    private JFXTextField TF_SEARCH_SPACES;

    // =========================================================
    // TABLE
    // =========================================================

    @FXML
    private TableView<ParkingSpace> TV_PARKING_SPACES;

    @FXML
    private TableColumn<ParkingSpace, String> TV_RW_ID;

    @FXML
    private TableColumn<ParkingSpace, String> TV_RW_TYPE_VEHICLE;

    @FXML
    private TableColumn<ParkingSpace, String> TV_RW_VEHICLE;

    @FXML
    private TableColumn<ParkingSpace, String> TV_RW_STATE;

    // =========================================================
    // ACTION BUTTONS
    // =========================================================

    @FXML
    private JFXButton BTN_OUT_OF_SERVICE;

    @FXML
    private JFXButton BTN_RESTORE_SERVICE;

    // =========================================================
    // TABLE DATA
    // =========================================================

    private final ObservableList<ParkingSpace> parkingSpaces =
            FXCollections.observableArrayList();

    private FilteredList<ParkingSpace> filteredParkingSpaces;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ParkingSpacesViewController(ParkingContext context) {

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

        configureSpaceTypes();

        configureTable();

        configureSearch();

        loadParkingSpaces();
    }

    // =========================================================
    // COMBO BOX
    // =========================================================

    private void configureSpaceTypes() {

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

        TV_RW_ID.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                cellData
                                        .getValue()
                                        .getNumber()
                        )
        );

        TV_RW_TYPE_VEHICLE.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                getSpaceTypeName(
                                        cellData.getValue()
                                )
                        )
        );

        TV_RW_VEHICLE.setCellValueFactory(
                cellData -> {

                    ParkingSpace space =
                            cellData.getValue();

                    if (space.getParkedVehicle() == null) {

                        return new ReadOnlyStringWrapper(
                                "—"
                        );
                    }

                    return new ReadOnlyStringWrapper(
                            space
                                    .getParkedVehicle()
                                    .getLicensePlate()
                    );
                }
        );

        TV_RW_STATE.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                getStatusName(
                                        cellData.getValue()
                                )
                        )
        );

        filteredParkingSpaces =
                new FilteredList<>(
                        parkingSpaces,
                        space -> true
                );

        TV_PARKING_SPACES.setItems(
                filteredParkingSpaces
        );
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private void configureSearch() {

        TF_SEARCH_SPACES
                .textProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                filterParkingSpaces(newValue)
                );
    }

    private void filterParkingSpaces(
            String searchText) {

        if (searchText == null
                || searchText.isBlank()) {

            filteredParkingSpaces.setPredicate(
                    space -> true
            );

            return;
        }

        String search =
                searchText
                        .trim()
                        .toLowerCase();

        filteredParkingSpaces.setPredicate(
                space -> {

                    String number =
                            space
                                    .getNumber()
                                    .toLowerCase();

                    String type =
                            getSpaceTypeName(space)
                                    .toLowerCase();

                    String status =
                            getStatusName(space)
                                    .toLowerCase();

                    String vehiclePlate = "";

                    if (space.getParkedVehicle() != null) {

                        vehiclePlate =
                                space
                                        .getParkedVehicle()
                                        .getLicensePlate()
                                        .toLowerCase();
                    }

                    return number.contains(search)
                            || type.contains(search)
                            || status.contains(search)
                            || vehiclePlate.contains(search);
                }
        );
    }

    // =========================================================
    // REGISTER SPACE
    // =========================================================

    @FXML
    private void RegisterSpace(
            ActionEvent event) {

        try {

            ParkingSpace parkingSpace =
                    createParkingSpaceFromForm();

            registrationService
                    .registerParkingSpace(
                            parkingSpace
                    );

            loadParkingSpaces();

            clearForm();

            showInformation(
                    "Espacio registrado",
                    "El espacio "
                    + parkingSpace.getNumber()
                    + " fue registrado correctamente."
            );

        } catch (RuntimeException exception) {

            showError(
                    "No se pudo registrar el espacio",
                    exception.getMessage()
            );
        }
    }

    // =========================================================
    // MARK OUT OF SERVICE
    // =========================================================

    @FXML
    private void MarkOutOfService(
            ActionEvent event) {

        ParkingSpace selectedSpace =
                getSelectedParkingSpace();

        if (selectedSpace == null) {

            showError(
                    "Espacio no seleccionado",
                    "Debe seleccionar un espacio de la tabla."
            );

            return;
        }

        try {

            selectedSpace.markOutOfService();

            /*
             * The object contained by ParkingLot is the same
             * object displayed by the TableView.
             *
             * refresh() forces the table to read its new status.
             */
            TV_PARKING_SPACES.refresh();

            showInformation(
                    "Espacio fuera de servicio",
                    "El espacio "
                    + selectedSpace.getNumber()
                    + " fue puesto fuera de servicio."
            );

        } catch (RuntimeException exception) {

            showError(
                    "No se pudo modificar el espacio",
                    exception.getMessage()
            );
        }
    }

    // =========================================================
    // RESTORE SERVICE
    // =========================================================

    @FXML
    private void RestoreService(
            ActionEvent event) {

        ParkingSpace selectedSpace =
                getSelectedParkingSpace();

        if (selectedSpace == null) {

            showError(
                    "Espacio no seleccionado",
                    "Debe seleccionar un espacio de la tabla."
            );

            return;
        }

        try {

            selectedSpace.restoreService();

            TV_PARKING_SPACES.refresh();

            showInformation(
                    "Espacio habilitado",
                    "El espacio "
                    + selectedSpace.getNumber()
                    + " fue habilitado correctamente."
            );

        } catch (RuntimeException exception) {

            showError(
                    "No se pudo habilitar el espacio",
                    exception.getMessage()
            );
        }
    }

    // =========================================================
    // SELECTED SPACE
    // =========================================================

    private ParkingSpace getSelectedParkingSpace() {

        return TV_PARKING_SPACES
                .getSelectionModel()
                .getSelectedItem();
    }

    // =========================================================
    // SPACE CREATION
    // =========================================================

    private ParkingSpace createParkingSpaceFromForm() {

        String number =
                TF_NUMBER.getText();

        String selectedType =
                CB_TYPE_VEHICLE.getValue();

        if (selectedType == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un tipo de espacio."
            );
        }

        ParkingSpaceType type =
                getParkingSpaceType(
                        selectedType
                );

        return new ParkingSpace(
                number,
                type
        );
    }

    private ParkingSpaceType getParkingSpaceType(
            String selectedType) {

        switch (selectedType) {

            case TYPE_CAR:
                return ParkingSpaceType.CAR;

            case TYPE_MOTORCYCLE:
                return ParkingSpaceType.MOTORCYCLE;

            case TYPE_CARGO:
                return ParkingSpaceType.CARGO;

            default:
                throw new IllegalArgumentException(
                        "Tipo de espacio no válido."
                );
        }
    }

    // =========================================================
    // LOAD DATA
    // =========================================================

    private void loadParkingSpaces() {

        List<ParkingSpace> registeredSpaces =
                queryService.getParkingSpaces();

        parkingSpaces.setAll(
                registeredSpaces
        );
    }

    // =========================================================
    // DISPLAY TEXT
    // =========================================================

    private String getSpaceTypeName(
            ParkingSpace space) {

        switch (space.getType()) {

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

    private String getStatusName(
            ParkingSpace space) {

        switch (space.getStatus()) {

            case AVAILABLE:
                return "Disponible";

            case OCCUPIED:
                return "Ocupado";

            case OUT_OF_SERVICE:
                return "Fuera de servicio";

            default:
                return "Desconocido";
        }
    }

    // =========================================================
    // FORM
    // =========================================================

    private void clearForm() {

        TF_NUMBER.clear();

        CB_TYPE_VEHICLE
                .getSelectionModel()
                .clearSelection();

        TF_NUMBER.requestFocus();
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