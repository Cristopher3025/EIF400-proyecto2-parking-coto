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

    private final EntryService entryService;
    private final QueryService queryService;

    private static final DateTimeFormatter DATE_FORMAT
            = DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    @FXML
    private AnchorPane AP_ENTRY;

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

    private Vehicle selectedVehicle;

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

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle) {

        configureVehicleSearch();

        clearVehicleInformation();

        clearEntryResult();

        BTN_REGISTER.setDisable(true);
    }

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

            selectedVehicle
                    = null;

            clearVehicleInformation();

            BTN_REGISTER.setDisable(
                    true
            );
        }
    }

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

        animateVehicleInformation();
    }

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

    @FXML
    private void RegisterEntry(
            ActionEvent event) {

        if (selectedVehicle == null) {

            showError(
                    "Veh├¡culo no seleccionado",
                    "Debe buscar una placa registrada antes de realizar el ingreso."
            );

            return;
        }

        try {

            ParkingTicket ticket
                    = entryService.registerEntry(
                            selectedVehicle
                                    .getLicensePlate()
                    );

            showEntryResult(
                    ticket
            );

            LBL_CORRECT_ENTRY.setText(
                    "Ingreso registrado correctamente"
            );

            AP_ENTRY_CORRECTED.setManaged(
                    true
            );

            AP_ENTRY_CORRECTED.setVisible(
                    true
            );

            animateEntryResult();

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

    private void animateEntryResult() {

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

        ParallelTransition successCard
                = createFadeAndSlide(
                        AP_ENTRY_CORRECTED,
                        450
                );

        PauseTransition pauseAfterSuccess
                = new PauseTransition(
                        Duration.millis(100)
                );

        ParallelTransition ticketAnimation
                = createFadeAndSlide(
                        TA_TICKET,
                        300
                );

        ParallelTransition plateAnimation
                = createFadeAndSlide(
                        TA_PLATE,
                        300
                );

        ParallelTransition spaceAnimation
                = createFadeAndSlide(
                        TA_ALLOCATED_SPACE,
                        300
                );

        ParallelTransition typeAnimation
                = createFadeAndSlide(
                        TA_TYPE_SPACE,
                        300
                );

        ParallelTransition dateAnimation
                = createFadeAndSlide(
                        TA_DATE,
                        300
                );

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

    private ParallelTransition createFadeAndSlide(
            Node node,
            double duration) {

        node.setOpacity(
                0.0
        );

        node.setTranslateY(
                6.0
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

    private void showEntryResult(
            ParkingTicket ticket) {

        ParkingSpace parkingSpace
                = ticket.getParkingSpace();

        TA_TICKET.setText(
                ticket.getId()
        );

        TA_PLATE.setText(
                ticket
                        .getVehicle()
                        .getLicensePlate()
        );

        TA_ALLOCATED_SPACE.setText(
                parkingSpace.getNumber()
        );

        TA_TYPE_SPACE.setText(
                getParkingSpaceTypeName(
                        parkingSpace
                )
        );

        TA_DATE.setText(
                ticket
                        .getEntryTime()
                        .format(
                                DATE_FORMAT
                        )
        );
    }

    private String getVehicleTypeName(
            Vehicle vehicle) {

        switch (vehicle.getRequiredSpaceType()) {

            case CAR:

                return "Autom├│vil";

            case MOTORCYCLE:

                return "Motocicleta";

            case CARGO:

                return "Veh├¡culo de carga";

            default:

                return "Desconocido";
        }
    }

    private String getParkingSpaceTypeName(
            ParkingSpace parkingSpace) {

        switch (parkingSpace.getType()) {

            case CAR:

                return "Autom├│vil";

            case MOTORCYCLE:

                return "Motocicleta";

            case CARGO:

                return "Veh├¡culo de carga";

            default:

                return "Desconocido";
        }
    }

    private void clearVehicleInformation() {

        TA_BRAND.clear();

        TA_MODEL.clear();

        TA_COLOR.clear();

        TA_TYPE.clear();

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

    private void clearEntryResult() {

        AP_ENTRY_CORRECTED.setVisible(
                false
        );

        AP_ENTRY_CORRECTED.setManaged(
                false
        );

        AP_ENTRY_CORRECTED.setOpacity(
                1.0
        );

        AP_ENTRY_CORRECTED.setTranslateY(
                0.0
        );

        LBL_CORRECT_ENTRY.setText(
                ""
        );

        TA_TICKET.clear();

        TA_PLATE.clear();

        TA_ALLOCATED_SPACE.clear();

        TA_TYPE_SPACE.clear();

        TA_DATE.clear();

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
