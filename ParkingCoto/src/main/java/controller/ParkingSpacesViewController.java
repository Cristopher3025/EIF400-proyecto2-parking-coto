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

    // =========================================================
    // SERVICES
    // =========================================================

    private final RegistrationService registrationService;
    private final QueryService queryService;


    // =========================================================
    // DISPLAY VALUES
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
    // ANIMATION CONTROL
    // =========================================================

    /*
     * Stores the number of the space that was just registered.
     *
     * This allows us to animate ONLY the new row instead of
     * animating the complete table.
     */
    private String recentlyRegisteredSpaceNumber;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

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

        // =====================================================
        // NUMBER
        // =====================================================

        TV_RW_ID.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                cellData
                                        .getValue()
                                        .getNumber()
                        )
        );


        // =====================================================
        // TYPE
        // =====================================================

        TV_RW_TYPE_VEHICLE.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                getSpaceTypeName(
                                        cellData.getValue()
                                )
                        )
        );


        // =====================================================
        // VEHICLE
        // =====================================================

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


        // =====================================================
        // STATUS
        // =====================================================

        TV_RW_STATE.setCellValueFactory(
                cellData ->
                        new ReadOnlyStringWrapper(
                                getStatusName(
                                        cellData.getValue()
                                )
                        )
        );


        // =====================================================
        // FILTERED LIST
        // =====================================================

        filteredParkingSpaces =
                new FilteredList<>(
                        parkingSpaces,
                        space -> true
                );


        TV_PARKING_SPACES.setItems(
                filteredParkingSpaces
        );


        // =====================================================
        // CINEMATIC ROW ANIMATION
        // =====================================================

        configureRowAnimations();
    }


    // =========================================================
    // ROW ANIMATIONS
    // =========================================================

    private void configureRowAnimations() {

        TV_PARKING_SPACES.setRowFactory(
                tableView -> {

                    TableRow<ParkingSpace> row =
                            new TableRow<>();


                    /*
                     * JavaFX reuses TableRow objects internally.
                     *
                     * For that reason, we listen to the item
                     * represented by the row.
                     */
                    row.itemProperty().addListener(
                            (
                                    observable,
                                    oldSpace,
                                    newSpace
                            ) -> {

                                if (newSpace == null) {
                                    return;
                                }


                                /*
                                 * Only animate the space that
                                 * was JUST registered.
                                 */
                                if (recentlyRegisteredSpaceNumber != null
                                        && recentlyRegisteredSpaceNumber
                                                .equalsIgnoreCase(
                                                        newSpace.getNumber()
                                                )) {

                                    animateNewSpaceRow(
                                            row
                                    );


                                    /*
                                     * Clear the marker so that
                                     * the animation happens once.
                                     */
                                    recentlyRegisteredSpaceNumber = null;
                                }
                            }
                    );

                    return row;
                }
        );
    }


    // =========================================================
    // NEW SPACE ANIMATION
    // =========================================================

    private void animateNewSpaceRow(
            TableRow<ParkingSpace> row) {

        // =====================================================
        // INITIAL STATE
        // =====================================================

        row.setOpacity(
                0.0
        );

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
    // STATUS CHANGE ANIMATION
    // =========================================================

    private void animateStatusChange(
            ParkingSpace space) {

        /*
         * Find the visible row corresponding to the
         * selected ParkingSpace.
         */
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


            /*
             * We only modify opacity.
             *
             * There is NO scaling and NO font-size change.
             */
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


    // =========================================================
    // SEARCH
    // =========================================================

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


    // =========================================================
    // REGISTER SPACE
    // =========================================================

    @FXML
    private void RegisterSpace(
            ActionEvent event) {

        try {

            // =================================================
            // CREATE SPACE
            // =================================================

            ParkingSpace parkingSpace =
                    createParkingSpaceFromForm();


            // =================================================
            // REGISTER
            // =================================================

            registrationService
                    .registerParkingSpace(
                            parkingSpace
                    );


            /*
             * Remember the newly registered space BEFORE
             * refreshing the table.
             */
            recentlyRegisteredSpaceNumber =
                    parkingSpace.getNumber();


            // =================================================
            // RELOAD
            // =================================================

            loadParkingSpaces();


            // =================================================
            // SCROLL TO NEW SPACE
            // =================================================

            scrollToParkingSpace(
                    parkingSpace
            );


            // =================================================
            // CLEAR FORM
            // =================================================

            clearForm();


            // =================================================
            // MESSAGE
            // =================================================

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
    // SCROLL TO NEW SPACE
    // =========================================================

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

            // =================================================
            // CHANGE DOMAIN STATE
            // =================================================

            selectedSpace.markOutOfService();


            // =================================================
            // REFRESH
            // =================================================

            TV_PARKING_SPACES.refresh();


            // =================================================
            // VISUAL ANIMATION
            // =================================================

            animateStatusChange(
                    selectedSpace
            );


            // =================================================
            // MESSAGE
            // =================================================

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

            // =================================================
            // RESTORE DOMAIN STATE
            // =================================================

            selectedSpace.restoreService();


            // =================================================
            // REFRESH
            // =================================================

            TV_PARKING_SPACES.refresh();


            // =================================================
            // VISUAL ANIMATION
            // =================================================

            animateStatusChange(
                    selectedSpace
            );


            // =================================================
            // MESSAGE
            // =================================================

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


    // =========================================================
    // CONVERT DISPLAY TYPE TO ENUM
    // =========================================================

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


        TV_PARKING_SPACES.refresh();
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