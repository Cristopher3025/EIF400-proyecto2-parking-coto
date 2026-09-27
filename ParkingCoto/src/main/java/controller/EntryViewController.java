package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

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

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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

    public EntryViewController(ParkingContext context) {

        Objects.requireNonNull(
                context,
                "Parking context cannot be null"
        );

        this.entryService =
                context.getEntryService();

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
                        (observable, oldValue, newValue) ->
                                searchVehicle(newValue)
                );
    }

    private void searchVehicle(String licensePlate) {

        clearEntryResult();

        if (licensePlate == null
                || licensePlate.isBlank()) {

            selectedVehicle = null;

            clearVehicleInformation();

            BTN_REGISTER.setDisable(true);

            return;
        }

        try {

            Vehicle vehicle =
                    queryService.findVehicle(
                            licensePlate.trim()
                    );

            selectedVehicle = vehicle;

            showVehicleInformation(vehicle);

            BTN_REGISTER.setDisable(false);

        } catch (RuntimeException exception) {

            /*
             * Mientras el usuario escribe la placa es normal
             * que todavía no exista una coincidencia exacta.
             *
             * Por eso no mostramos un Alert en cada tecla.
             */
            selectedVehicle = null;

            clearVehicleInformation();

            BTN_REGISTER.setDisable(true);
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
                getVehicleTypeName(vehicle)
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

            ParkingTicket ticket =
                    entryService.registerEntry(
                            selectedVehicle.getLicensePlate()
                    );

            showEntryResult(ticket);

            LBL_CORRECT_ENTRY.setText(
                    "Ingreso registrado correctamente"
            );

            /*
             * Después de registrar el ingreso dejamos
             * deshabilitado el botón.
             *
             * Esto evita que el usuario intente registrar
             * inmediatamente el mismo vehículo otra vez.
             */
            BTN_REGISTER.setDisable(true);

        } catch (RuntimeException exception) {

            clearEntryResult();

            showError(
                    "No se pudo registrar el ingreso",
                    exception.getMessage()
            );
        }
    }

    // =========================================================
    // SHOW ENTRY RESULT
    // =========================================================

    private void showEntryResult(
            ParkingTicket ticket) {

        ParkingSpace parkingSpace =
                ticket.getParkingSpace();

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
                        .format(DATE_FORMAT)
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
    }

    // =========================================================
    // CLEAR ENTRY RESULT
    // =========================================================

    private void clearEntryResult() {

        LBL_CORRECT_ENTRY.setText("");

        TA_TICKET.clear();
        TA_PLATE.clear();
        TA_ALLOCATED_SPACE.clear();
        TA_TYPE_SPACE.clear();
        TA_DATE.clear();
    }

    // =========================================================
    // MESSAGES
    // =========================================================

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