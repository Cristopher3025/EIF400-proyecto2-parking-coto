package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import model.parking.ParkingSpace;
import model.ticket.ParkingTicket;
import model.vehicle.Vehicle;

import service.EntryService;
import service.ParkingContext;
import service.QueryService;

public class EntryViewController implements Initializable {

    // =========================================================
    // SERVICES
    // =========================================================
    private final EntryService entryService;
    private final QueryService queryService;

    // =========================================================
    // DATE FORMAT
    // =========================================================
    private static final DateTimeFormatter DATE_FORMAT
            = DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    // =========================================================
    // MAIN CONTAINER
    // =========================================================
    @FXML
    private AnchorPane AP_ENTRY;

    // =========================================================
    // VEHICLE SEARCH
    // =========================================================
    @FXML
    private JFXTextField TF_SEARCH_PLATE;

    @FXML
    private JFXTextArea TA_BRAND;

    @FXML
    private JFXTextArea TA_MODEL;

    @FXML
    private JFXTextArea TA_COLOR;

    @FXML
    private JFXTextArea TA_TYPE;

    @FXML
    private JFXButton BTN_REGISTER;

    // =========================================================
    // ENTRY RESULT
    // =========================================================
    @FXML
    private AnchorPane AP_ENTRY_CORRECTED;

    @FXML
    private Label LBL_CORRECT_ENTRY;

    @FXML
    private JFXTextArea TA_TICKET;

    @FXML
    private JFXTextArea TA_PLATE;

    @FXML
    private JFXTextArea TA_ALLOCATED_SPACE;

    @FXML
    private JFXTextArea TA_TYPE_SPACE;

    @FXML
    private JFXTextArea TA_DATE;

    // =========================================================
    // CURRENT VEHICLE
    // =========================================================
    private Vehicle selectedVehicle;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public EntryViewController(
            ParkingContext context) {

        Objects.requireNonNull(
                context,
                "Parking context cannot be null"
        );

        this.entryService
                = context.getEntryService();

        this.queryService
                = context.getQueryService();
    }

    // =========================================================
    // INITIALIZATION
    // =========================================================
    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle) {

        configureVehicleSearch();

        clearVehicleInformation();

        clearEntryResult();

        BTN_REGISTER.setDisable(true);
    }

    // =========================================================
    // VEHICLE SEARCH
    // =========================================================
    private void configureVehicleSearch() {

        TF_SEARCH_PLATE
                .textProperty()
                .addListener(
                        (observable, oldValue, newValue)
                        -> searchVehicle(newValue)
                );
    }

    private void searchVehicle(
            String licensePlate) {

        /*
         * If another vehicle is searched,
         * the previous entry result disappears.
         */
        clearEntryResult();

        if (licensePlate == null
                || licensePlate.isBlank()) {

            selectedVehicle = null;

            clearVehicleInformation();

            BTN_REGISTER.setDisable(true);

            return;
        }

        try {

            Vehicle vehicle
                    = queryService.findVehicle(
                            licensePlate.trim()
                    );

            selectedVehicle
                    = vehicle;

            showVehicleInformation(
                    vehicle
            );

            BTN_REGISTER.setDisable(
                    false
            );

        } catch (RuntimeException exception) {

            /*
             * While the user is typing the license plate,
             * it is normal that there is not yet an exact match.
             */
            selectedVehicle
                    = null;

            clearVehicleInformation();

            BTN_REGISTER.setDisable(
                    true
            );
        }
    }

    // =========================================================
    // SHOW VEHICLE INFORMATION
    // =========================================================
    private void showVehicleInformation(
            Vehicle vehicle) {

        TA_BRAND.setText(
                vehicle.getBrand()
        );

        TA_MODEL.setText(
                vehicle.getModel()
        );

        TA_COLOR.setText(
                vehicle.getColor()
        );

        TA_TYPE.setText(
                getVehicleTypeName(
                        vehicle
                )
        );


        /*
         * Soft animation when the vehicle is found.
         *
         * No scaling is used.
         */
        animateVehicleInformation();
    }

    // =========================================================
    // VEHICLE INFORMATION ANIMATION
    // =========================================================
    private void animateVehicleInformation() {

        animateFadeOnly(
                TA_BRAND,
                300
        );

        animateFadeOnly(
                TA_MODEL,
                350
        );

        animateFadeOnly(
                TA_COLOR,
                400
        );

        animateFadeOnly(
                TA_TYPE,
                450
        );
    }

    // =========================================================
    // REGISTER ENTRY
    // =========================================================
    @FXML
    private void RegisterEntry(
            ActionEvent event) {

        if (selectedVehicle == null) {

            showError(
                    "Vehículo no seleccionado",
                    "Debe buscar una placa registrada antes de realizar el ingreso."
            );

            return;
        }

        try {

            // =================================================
            // REGISTER ENTRY
            // =================================================
            ParkingTicket ticket
                    = entryService.registerEntry(
                            selectedVehicle
                                    .getLicensePlate()
                    );

            // =================================================
            // PUT INFORMATION IN CONTROLS
            // =================================================
            showEntryResult(
                    ticket
            );

            // =================================================
            // SUCCESS MESSAGE
            // =================================================
            LBL_CORRECT_ENTRY.setText(
                    "Ingreso registrado correctamente"
            );

            // =================================================
            // MAKE CARD AVAILABLE
            // =================================================
            AP_ENTRY_CORRECTED.setManaged(
                    true
            );

            AP_ENTRY_CORRECTED.setVisible(
                    true
            );

            // =================================================
            // CINEMATIC RESULT ANIMATION
            // =================================================
            animateEntryResult();

            // =================================================
            // PREVENT DUPLICATE ENTRY
            // =================================================
            BTN_REGISTER.setDisable(
                    true
            );

        } catch (RuntimeException exception) {

            clearEntryResult();

            showError(
                    "No se pudo registrar el ingreso",
                    exception.getMessage()
            );
        }
    }

    // =========================================================
    // CINEMATIC ENTRY RESULT
    // =========================================================
    private void animateEntryResult() {

        /*
         * First hide the controls that will participate
         * in the sequence.
         */
        AP_ENTRY_CORRECTED.setOpacity(
                0.0
        );

        TA_TICKET.setOpacity(
                0.0
        );

        TA_PLATE.setOpacity(
                0.0
        );

        TA_ALLOCATED_SPACE.setOpacity(
                0.0
        );

        TA_TYPE_SPACE.setOpacity(
                0.0
        );

        TA_DATE.setOpacity(
                0.0
        );

        // =====================================================
        // 1. GREEN SUCCESS CARD
        // =====================================================
        ParallelTransition successCard
                = createFadeAndSlide(
                        AP_ENTRY_CORRECTED,
                        450
                );

        // =====================================================
        // SMALL CINEMATIC PAUSE
        // =====================================================
        PauseTransition pauseAfterSuccess
                = new PauseTransition(
                        Duration.millis(100)
                );

        // =====================================================
        // 2. TICKET
        // =====================================================
        ParallelTransition ticketAnimation
                = createFadeAndSlide(
                        TA_TICKET,
                        300
                );

        // =====================================================
        // 3. PLATE
        // =====================================================
        ParallelTransition plateAnimation
                = createFadeAndSlide(
                        TA_PLATE,
                        300
                );

        // =====================================================
        // 4. PARKING SPACE
        // =====================================================
        ParallelTransition spaceAnimation
                = createFadeAndSlide(
                        TA_ALLOCATED_SPACE,
                        300
                );

        // =====================================================
        // 5. SPACE TYPE
        // =====================================================
        ParallelTransition typeAnimation
                = createFadeAndSlide(
                        TA_TYPE_SPACE,
                        300
                );

        // =====================================================
        // 6. DATE
        // =====================================================
        ParallelTransition dateAnimation
                = createFadeAndSlide(
                        TA_DATE,
                        300
                );

        // =====================================================
        // COMPLETE SEQUENCE
        // =====================================================
        SequentialTransition sequence
                = new SequentialTransition(
                        successCard,
                        pauseAfterSuccess,
                        ticketAnimation,
                        plateAnimation,
                        spaceAnimation,
                        typeAnimation,
                        dateAnimation
                );

        sequence.play();
    }

    // =========================================================
    // FADE + SMALL SLIDE
    // =========================================================
    private ParallelTransition createFadeAndSlide(
            Node node,
            double duration) {

        /*
         * Starting position.
         *
         * Only 6 pixels are used to keep the animation
         * elegant and subtle.
         */
        node.setOpacity(
                0.0
        );

        node.setTranslateY(
                6.0
        );

        // =====================================================
        // FADE
        // =====================================================
        FadeTransition fade
                = new FadeTransition(
                        Duration.millis(duration),
                        node
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
        TranslateTransition movement
                = new TranslateTransition(
                        Duration.millis(duration),
                        node
                );

        movement.setFromY(
                6.0
        );

        movement.setToY(
                0.0
        );

        return new ParallelTransition(
                fade,
                movement
        );
    }

    // =========================================================
    // SIMPLE FADE
    // =========================================================
    private void animateFadeOnly(
            Node node,
            double duration) {

        node.setOpacity(
                0.0
        );

        FadeTransition fade
                = new FadeTransition(
                        Duration.millis(duration),
                        node
                );

        fade.setFromValue(
                0.0
        );

        fade.setToValue(
                1.0
        );

        fade.play();
    }

    // =========================================================
    // SHOW ENTRY RESULT
    // =========================================================
    private void showEntryResult(
            ParkingTicket ticket) {

        ParkingSpace parkingSpace
                = ticket.getParkingSpace();

        // =====================================================
        // TICKET ID
        // =====================================================
        TA_TICKET.setText(
                ticket.getId()
        );

        // =====================================================
        // LICENSE PLATE
        // =====================================================
        TA_PLATE.setText(
                ticket
                        .getVehicle()
                        .getLicensePlate()
        );

        // =====================================================
        // ALLOCATED PARKING SPACE
        // =====================================================
        TA_ALLOCATED_SPACE.setText(
                parkingSpace.getNumber()
        );

        // =====================================================
        // PARKING SPACE TYPE
        // =====================================================
        TA_TYPE_SPACE.setText(
                getParkingSpaceTypeName(
                        parkingSpace
                )
        );

        // =====================================================
        // ENTRY DATE AND TIME
        // =====================================================
        TA_DATE.setText(
                ticket
                        .getEntryTime()
                        .format(
                                DATE_FORMAT
                        )
        );
    }

    // =========================================================
    // VEHICLE TYPE
    // =========================================================
    private String getVehicleTypeName(
            Vehicle vehicle) {

        switch (vehicle.getRequiredSpaceType()) {

            case CAR:

                return "Automóvil";

            case MOTORCYCLE:

                return "Motocicleta";

            case CARGO:

                return "Vehículo de carga";

            default:

                return "Desconocido";
        }
    }

    // =========================================================
    // PARKING SPACE TYPE
    // =========================================================
    private String getParkingSpaceTypeName(
            ParkingSpace parkingSpace) {

        switch (parkingSpace.getType()) {

            case CAR:

                return "Automóvil";

            case MOTORCYCLE:

                return "Motocicleta";

            case CARGO:

                return "Vehículo de carga";

            default:

                return "Desconocido";
        }
    }

    // =========================================================
    // CLEAR VEHICLE INFORMATION
    // =========================================================
    private void clearVehicleInformation() {

        TA_BRAND.clear();

        TA_MODEL.clear();

        TA_COLOR.clear();

        TA_TYPE.clear();


        /*
         * Important:
         * restore opacity in case a previous animation
         * was interrupted.
         */
        TA_BRAND.setOpacity(
                1.0
        );

        TA_MODEL.setOpacity(
                1.0
        );

        TA_COLOR.setOpacity(
                1.0
        );

        TA_TYPE.setOpacity(
                1.0
        );
    }

    // =========================================================
    // CLEAR ENTRY RESULT
    // =========================================================
    private void clearEntryResult() {

        // =====================================================
        // HIDE SUCCESS CARD
        // =====================================================
        AP_ENTRY_CORRECTED.setVisible(
                false
        );

        AP_ENTRY_CORRECTED.setManaged(
                false
        );


        /*
         * Restore transformations.
         *
         * This prevents a previous animation from leaving
         * controls in an intermediate state.
         */
        AP_ENTRY_CORRECTED.setOpacity(
                1.0
        );

        AP_ENTRY_CORRECTED.setTranslateY(
                0.0
        );

        // =====================================================
        // CLEAR MESSAGE
        // =====================================================
        LBL_CORRECT_ENTRY.setText(
                ""
        );

        // =====================================================
        // CLEAR RESULT
        // =====================================================
        TA_TICKET.clear();

        TA_PLATE.clear();

        TA_ALLOCATED_SPACE.clear();

        TA_TYPE_SPACE.clear();

        TA_DATE.clear();

        // =====================================================
        // RESTORE VISUAL STATE
        // =====================================================
        resetNode(
                TA_TICKET
        );

        resetNode(
                TA_PLATE
        );

        resetNode(
                TA_ALLOCATED_SPACE
        );

        resetNode(
                TA_TYPE_SPACE
        );

        resetNode(
                TA_DATE
        );
    }

    // =========================================================
    // RESET NODE
    // =========================================================
    private void resetNode(
            Node node) {

        node.setOpacity(
                1.0
        );

        node.setTranslateX(
                0.0
        );

        node.setTranslateY(
                0.0
        );
    }

    // =========================================================
    // MESSAGES
    // =========================================================
    private void showError(
            String title,
            String message) {

        Alert alert
                = new Alert(
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
