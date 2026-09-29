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
import javafx.scene.layout.AnchorPane;

import model.parking.ParkingSpace;
import model.ticket.ParkingTicket;

import service.ExitService;
import service.ParkingContext;
import service.QueryService;

public class ExitViewController implements Initializable {

    private final ExitService exitService;
    private final QueryService queryService;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private AnchorPane AP_EXIT;

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

    private ParkingTicket selectedTicket;

    public ExitViewController(ParkingContext context) {

        Objects.requireNonNull(
                context,
                "Parking context cannot be null"
        );

        this.exitService =
                context.getExitService();

        this.queryService =
                context.getQueryService();
    }

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle) {

        configureTicketSearch();

        clearTicketInformation();

        clearExitResult();

        BTN_REGISTER_EXIT.setDisable(true);
    }

    private void configureTicketSearch() {

        TF_SEARCH_TICKET
                .textProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                searchTicket(newValue)
                );
    }

    private void searchTicket(String ticketId) {

        clearExitResult();

        if (ticketId == null
                || ticketId.isBlank()) {

            selectedTicket = null;

            clearTicketInformation();

            BTN_REGISTER_EXIT.setDisable(true);

            return;
        }

        try {

            ParkingTicket ticket =
                    queryService.findActiveTicket(
                            ticketId.trim()
                    );

            selectedTicket = ticket;

            showTicketInformation(ticket);

            animateTicketInformation();

            BTN_REGISTER_EXIT.setDisable(false);

        } catch (RuntimeException exception) {

            selectedTicket = null;

            clearTicketInformation();

            BTN_REGISTER_EXIT.setDisable(true);
        }
    }

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

    private void animateTicketInformation() {

        prepareNode(TA_PLATE);
        prepareNode(TA_SPACE);
        prepareNode(TA_TYPE);
        prepareNode(TA_ENTRY_DATE);
        prepareNode(TA_STATE);

        SequentialTransition sequence =
                new SequentialTransition(

                        createFadeAndSlide(
                                TA_PLATE,
                                220
                        ),

                        createFadeAndSlide(
                                TA_SPACE,
                                220
                        ),

                        createFadeAndSlide(
                                TA_TYPE,
                                220
                        ),

                        createFadeAndSlide(
                                TA_ENTRY_DATE,
                                220
                        ),

                        createFadeAndSlide(
                                TA_STATE,
                                220
                        )
                );

        sequence.play();
    }

    @FXML
    private void Checkout(ActionEvent event) {

        if (selectedTicket == null) {

            showError(
                    "Ticket no seleccionado",
                    "Debe buscar un ticket activo antes "
                    + "de registrar la salida."
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

            TA_STATE.setText(
                    getTicketStatusName(
                            closedTicket
                    )
            );

            animateExitResult();

            BTN_REGISTER_EXIT.setDisable(true);

            selectedTicket = null;

        } catch (RuntimeException exception) {

            showError(
                    "No se pudo registrar la salida",
                    exception.getMessage()
            );
        }
    }

    private void showExitResult(
            ParkingTicket ticket) {

        LocalDateTime entryTime =
                ticket.getEntryTime();

        LocalDateTime exitTime =
                ticket.getExitTime();

        Duration parkingDuration =
                Duration.between(
                        entryTime,
                        exitTime
                );

        long billedHours =
                calculateBilledHours(
                        parkingDuration
                );

        TA_EXIT_DATE.setText(
                exitTime.format(
                        DATE_FORMAT
                )
        );

        TA_PARKING_TIME.setText(
                formatDuration(
                        parkingDuration
                )
        );

        TA_BILLED_HOURS.setText(
                String.valueOf(
                        billedHours
                )
        );

        TA_AMOUNT_COLLECTED.setText(
                " "
                + ticket
                        .getAmount()
                        .toPlainString()
        );
    }

    private void animateExitResult() {

        prepareNode(TA_EXIT_DATE);

        prepareNode(TA_PARKING_TIME);

        prepareNode(TA_BILLED_HOURS);

        prepareNode(TA_AMOUNT_COLLECTED);

        FadeTransition stateFade =
                new FadeTransition(
                        javafx.util.Duration.millis(400),
                        TA_STATE
                );

        stateFade.setFromValue(0.20);

        stateFade.setToValue(1.0);

        ParallelTransition exitDateAnimation =
                createFadeAndSlide(
                        TA_EXIT_DATE,
                        320
                );

        ParallelTransition parkingTimeAnimation =
                createFadeAndSlide(
                        TA_PARKING_TIME,
                        320
                );

        ParallelTransition billedHoursAnimation =
                createFadeAndSlide(
                        TA_BILLED_HOURS,
                        320
                );

        PauseTransition pauseBeforeAmount =
                new PauseTransition(
                        javafx.util.Duration.millis(150)
                );

        ParallelTransition amountAnimation =
                createFadeAndSlide(
                        TA_AMOUNT_COLLECTED,
                        450
                );

        SequentialTransition resultSequence =
                new SequentialTransition(

                        exitDateAnimation,

                        parkingTimeAnimation,

                        billedHoursAnimation,

                        pauseBeforeAmount,

                        amountAnimation
                );

        ParallelTransition completeAnimation =
                new ParallelTransition(
                        stateFade,
                        resultSequence
                );

        completeAnimation.setOnFinished(
                event -> {

                    showInformation(
                            "Salida registrada",
                            "La salida fue registrada "
                            + "correctamente. El espacio "
                            + "de parqueo ha sido liberado."
                    );
                }
        );

        completeAnimation.play();
    }

    private long calculateBilledHours(
            Duration duration) {

        long minutes =
                duration.toMinutes();

        if (minutes <= 0) {

            return 1;
        }

        return (minutes + 59) / 60;
    }

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

    private void prepareNode(
            Node node) {

        node.setOpacity(0.0);

        node.setTranslateY(7.0);
    }

    private ParallelTransition createFadeAndSlide(
            Node node,
            double durationMillis) {

        FadeTransition fade =
                new FadeTransition(
                        javafx.util.Duration.millis(
                                durationMillis
                        ),
                        node
                );

        fade.setFromValue(0.0);

        fade.setToValue(1.0);

        TranslateTransition movement =
                new TranslateTransition(
                        javafx.util.Duration.millis(
                                durationMillis
                        ),
                        node
                );

        movement.setFromY(7.0);

        movement.setToY(0.0);

        return new ParallelTransition(
                fade,
                movement
        );
    }

    private void clearTicketInformation() {

        TA_PLATE.clear();

        TA_SPACE.clear();

        TA_TYPE.clear();

        TA_ENTRY_DATE.clear();

        TA_STATE.clear();

        resetNode(TA_PLATE);

        resetNode(TA_SPACE);

        resetNode(TA_TYPE);

        resetNode(TA_ENTRY_DATE);

        resetNode(TA_STATE);
    }

    private void clearExitResult() {

        TA_EXIT_DATE.clear();

        TA_PARKING_TIME.clear();

        TA_BILLED_HOURS.clear();

        TA_AMOUNT_COLLECTED.clear();

        resetNode(TA_EXIT_DATE);

        resetNode(TA_PARKING_TIME);

        resetNode(TA_BILLED_HOURS);

        resetNode(TA_AMOUNT_COLLECTED);
    }

    private void resetNode(
            Node node) {

        node.setOpacity(1.0);

        node.setTranslateX(0.0);

        node.setTranslateY(0.0);
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

        alert.show();
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
