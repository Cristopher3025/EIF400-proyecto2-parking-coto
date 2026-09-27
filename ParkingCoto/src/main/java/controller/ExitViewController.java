package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

import model.parking.ParkingSpace;
import model.ticket.ParkingTicket;

import service.ExitService;
import service.ParkingContext;
import service.QueryService;

public class ExitViewController implements Initializable {

    // =========================================================
    // SERVICES
    // =========================================================

    private final ExitService exitService;
    private final QueryService queryService;

    // =========================================================
    // FORMAT
    // =========================================================

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    // =========================================================
    // MAIN CONTAINER
    // =========================================================

    @FXML
    private AnchorPane AP_EXIT;

    // =========================================================
    // TICKET SEARCH
    // =========================================================

    @FXML
    private JFXTextField TF_SEARCH_TICKET;

    @FXML
    private JFXTextArea TA_PLATE;

    @FXML
    private JFXTextArea TA_SPACE;

    @FXML
    private JFXTextArea TA_TYPE;

    @FXML
    private JFXTextArea TA_ENTRY_DATE;

    @FXML
    private JFXTextArea TA_STATE;

    // =========================================================
    // EXIT RESULT
    // =========================================================

    @FXML
    private JFXTextArea TA_EXIT_DATE;

    @FXML
    private JFXTextArea TA_PARKING_TIME;

    @FXML
    private JFXTextArea TA_BILLED_HOURS;

    @FXML
    private JFXTextArea TA_AMOUNT_COLLECTED;

    @FXML
    private JFXButton BTN_REGISTER_EXIT;

    // =========================================================
    // CURRENT TICKET
    // =========================================================

    private ParkingTicket selectedTicket;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ExitViewController(
            ParkingContext context) {

        Objects.requireNonNull(
                context,
                "Parking context cannot be null"
        );

        this.exitService =
                context.getExitService();

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

        configureTicketSearch();

        clearTicketInformation();

        clearExitResult();

        BTN_REGISTER_EXIT.setDisable(true);
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private void configureTicketSearch() {

        TF_SEARCH_TICKET
                .textProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                searchTicket(newValue)
                );
    }

    private void searchTicket(
            String ticketId) {

        clearExitResult();

        if (ticketId == null
                || ticketId.isBlank()) {

            selectedTicket = null;

            clearTicketInformation();

            BTN_REGISTER_EXIT.setDisable(true);

            return;
        }

        try {

            /*
             * We specifically search for an ACTIVE ticket.
             *
             * A CLOSED or PAID ticket cannot register
             * another exit.
             */
            ParkingTicket ticket =
                    queryService.findActiveTicket(
                            ticketId.trim()
                    );

            selectedTicket = ticket;

            showTicketInformation(ticket);

            BTN_REGISTER_EXIT.setDisable(false);

        } catch (RuntimeException exception) {

            /*
             * No Alert here because this method runs while
             * the user is typing the ticket ID.
             */
            selectedTicket = null;

            clearTicketInformation();

            BTN_REGISTER_EXIT.setDisable(true);
        }
    }

    // =========================================================
    // SHOW TICKET
    // =========================================================

    private void showTicketInformation(
            ParkingTicket ticket) {

        TA_PLATE.setText(
                ticket
                        .getVehicle()
                        .getLicensePlate()
        );

        TA_SPACE.setText(
                ticket
                        .getParkingSpace()
                        .getNumber()
        );

        TA_TYPE.setText(
                getParkingSpaceTypeName(
                        ticket.getParkingSpace()
                )
        );

        TA_ENTRY_DATE.setText(
                ticket
                        .getEntryTime()
                        .format(DATE_FORMAT)
        );

        TA_STATE.setText(
                getTicketStatusName(ticket)
        );
    }

    // =========================================================
    // REGISTER EXIT
    // =========================================================

    @FXML
    private void Checkout(
            ActionEvent event) {

        if (selectedTicket == null) {

            showError(
                    "Ticket no seleccionado",
                    "Debe buscar un ticket activo antes de registrar la salida."
            );

            return;
        }

        try {

            ParkingTicket closedTicket =
                    exitService.registerExit(
                            selectedTicket.getId()
                    );

            showExitResult(
                    closedTicket
            );

            /*
             * The ticket is now CLOSED, not PAID.
             */
            TA_STATE.setText(
                    getTicketStatusName(
                            closedTicket
                    )
            );

            BTN_REGISTER_EXIT.setDisable(true);

            selectedTicket = null;

            showInformation(
                    "Salida registrada",
                    "La salida fue registrada correctamente. "
                    + "El espacio de parqueo ha sido liberado."
            );

        } catch (RuntimeException exception) {

            showError(
                    "No se pudo registrar la salida",
                    exception.getMessage()
            );
        }
    }

    // =========================================================
    // EXIT RESULT
    // =========================================================

    private void showExitResult(
            ParkingTicket ticket) {

        LocalDateTime entryTime =
                ticket.getEntryTime();

        LocalDateTime exitTime =
                ticket.getExitTime();

        Duration duration =
                Duration.between(
                        entryTime,
                        exitTime
                );

        long billedHours =
                calculateBilledHours(
                        duration
                );

        TA_EXIT_DATE.setText(
                exitTime.format(
                        DATE_FORMAT
                )
        );

        TA_PARKING_TIME.setText(
                formatDuration(
                        duration
                )
        );

        TA_BILLED_HOURS.setText(
                String.valueOf(
                        billedHours
                )
        );

        TA_AMOUNT_COLLECTED.setText(
                "₡"
                + ticket
                        .getAmount()
                        .toPlainString()
        );
    }

    // =========================================================
    // BILLED HOURS
    // =========================================================

    private long calculateBilledHours(
            Duration duration) {

        long minutes =
                duration.toMinutes();

        if (minutes <= 0) {
            return 1;
        }

        return (minutes + 59) / 60;
    }

    // =========================================================
    // DURATION
    // =========================================================

    private String formatDuration(
            Duration duration) {

        long totalMinutes =
                duration.toMinutes();

        long days =
                totalMinutes / (24 * 60);

        long remainingMinutes =
                totalMinutes % (24 * 60);

        long hours =
                remainingMinutes / 60;

        long minutes =
                remainingMinutes % 60;

        if (days > 0) {

            return days
                    + " d "
                    + hours
                    + " h "
                    + minutes
                    + " min";
        }

        if (hours > 0) {

            return hours
                    + " h "
                    + minutes
                    + " min";
        }

        return minutes
                + " min";
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
    // TICKET STATUS
    // =========================================================

    private String getTicketStatusName(
            ParkingTicket ticket) {

        switch (ticket.getStatus()) {

            case ACTIVE:
                return "Activo";

            case CLOSED:
                return "Cerrado";

            case PAID:
                return "Pagado";

            default:
                return "Desconocido";
        }
    }

    // =========================================================
    // CLEAR TICKET INFORMATION
    // =========================================================

    private void clearTicketInformation() {

        TA_PLATE.clear();
        TA_SPACE.clear();
        TA_TYPE.clear();
        TA_ENTRY_DATE.clear();
        TA_STATE.clear();
    }

    // =========================================================
    // CLEAR EXIT RESULT
    // =========================================================

    private void clearExitResult() {

        TA_EXIT_DATE.clear();
        TA_PARKING_TIME.clear();
        TA_BILLED_HOURS.clear();
        TA_AMOUNT_COLLECTED.clear();
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