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

import model.parking.ParkingSpace;

import service.ParkingContext;
import service.QueryService;
import service.RegistrationService;

public class ParkingSpacesViewController implements Initializable {

    private final RegistrationService registrationService;
    private final QueryService queryService;

    private static final String TYPE_CAR =
            "Autom├│vil";

    private static final String TYPE_MOTORCYCLE =
            "Motocicleta";

    private static final String TYPE_CARGO =
            "Veh├¡culo de carga";

    @FXML
    private AnchorPane AP_PARKING_SPACES;

    @FXML
    private MFXTextField TF_NUMBER;

    @FXML
    private MFXComboBox<String> CB_TYPE_VEHICLE;

    @FXML
    private JFXButton BTN_REGISTER;

    @FXML
    private JFXTextField TF_SEARCH_SPACES;

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

    @FXML
    private JFXButton BTN_OUT_OF_SERVICE;

    @FXML
    private JFXButton BTN_RESTORE_SERVICE;

    private final ObservableList<ParkingSpace> parkingSpaces =
            FXCollections.observableArrayList();

    private FilteredList<ParkingSpace> filteredParkingSpaces;

    private String recentlyRegisteredSpaceNumber;

    public ParkingSpacesViewController(
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

        configureSpaceTypes();

        configureTable();

        configureSearch();

        loadParkingSpaces();
    }

    private void configureSpaceTypes() {

        CB_TYPE_VEHICLE.setItems(
                FXCollections.observableArrayList(
                        TYPE_CAR,
                        TYPE_MOTORCYCLE,
                        TYPE_CARGO
                )
        );
    }

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
                                "ÔÇö"
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

        configureRowAnimations();
    }

    private void configureRowAnimations() {

        TV_PARKING_SPACES.setRowFactory(
                tableView -> {

                    TableRow<ParkingSpace> row =
                            new TableRow<>();

                    row.itemProperty().addListener(
                            (
                                    observable,
                                    oldSpace,
                                    newSpace
                            ) -> {

                                if (newSpace == null) {
                                    return;
                                }

                                if (recentlyRegisteredSpaceNumber != null
                                        && recentlyRegisteredSpaceNumber
                                                .equalsIgnoreCase(
                                                        newSpace.getNumber()
                                                )) {

                                    animateNewSpaceRow(
                                            row
                                    );

                                    recentlyRegisteredSpaceNumber = null;
                                }
                            }
                    );

                    return row;
                }
        );
    }

    private void animateNewSpaceRow(
            TableRow<ParkingSpace> row) {

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

    private void animateStatusChange(
            ParkingSpace space) {

        for (javafx.scene.Node node :
                TV_PARKING_SPACES.lookupAll(".table-row-cell")) {

            if (!(node instanceof TableRow<?>)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            TableRow<ParkingSpace> row =
                    (TableRow<ParkingSpace>) node;

            if (row.getItem() != space) {
                continue;
            }

            FadeTransition fadeOut =
                    new FadeTransition(
                            Duration.millis(150),
                            row
                    );

            fadeOut.setFromValue(
                    1.0
            );

            fadeOut.setToValue(
                    0.35
            );

            FadeTransition fadeIn =
                    new FadeTransition(
                            Duration.millis(350),
                            row
                    );

            fadeIn.setFromValue(
                    0.35
            );

            fadeIn.setToValue(
                    1.0
            );

            fadeOut.setOnFinished(
                    event -> {

                        TV_PARKING_SPACES.refresh();

                        fadeIn.play();
                    }
            );

            fadeOut.play();

            break;
        }
    }

    private void configureSearch() {

        TF_SEARCH_SPACES
                .textProperty()
                .addListener(
                        (
                                observable,
                                oldValue,
                                newValue
                        ) ->
                                filterParkingSpaces(
                                        newValue
                                )
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
                            getSpaceTypeName(
                                    space
                            )
                                    .toLowerCase();

                    String status =
                            getStatusName(
                                    space
                            )
                                    .toLowerCase();

                    String vehiclePlate =
                            "";

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

            recentlyRegisteredSpaceNumber =
                    parkingSpace.getNumber();

            loadParkingSpaces();

            scrollToParkingSpace(
                    parkingSpace
            );

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

    private void scrollToParkingSpace(
            ParkingSpace parkingSpace) {

        int index =
                filteredParkingSpaces.indexOf(
                        parkingSpace
                );

        if (index >= 0) {

            TV_PARKING_SPACES.scrollTo(
                    index
            );
        }
    }

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

            TV_PARKING_SPACES.refresh();

            animateStatusChange(
                    selectedSpace
            );

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

            animateStatusChange(
                    selectedSpace
            );

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

    private ParkingSpace getSelectedParkingSpace() {

        return TV_PARKING_SPACES
                .getSelectionModel()
                .getSelectedItem();
    }

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
                        "Tipo de espacio no v├ílido."
                );
        }
    }

    private void loadParkingSpaces() {

        List<ParkingSpace> registeredSpaces =
                queryService.getParkingSpaces();

        parkingSpaces.setAll(
                registeredSpaces
        );

        TV_PARKING_SPACES.refresh();
    }

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

    private void clearForm() {

        TF_NUMBER.clear();

        CB_TYPE_VEHICLE
                .getSelectionModel()
                .clearSelection();

        TF_NUMBER.requestFocus();
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
