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

    // =========================================================
    // SERVICES
    // =========================================================

    private final ExitService exitService;
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
    // CONFIGURE SEARCH
    // =========================================================

    private void configureTicketSearch() {

        TF_SEARCH_TICKET
                .textProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                searchTicket(newValue)
                );
    }


    // =========================================================
    // SEARCH TICKET
    // =========================================================

    private void searchTicket(String ticketId) {

        /*
         * Every time another ticket is searched,
         * remove the previous exit result.
         */
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
             * Search only for an active ticket.
             */
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


    // =========================================================
    // SHOW TICKET INFORMATION
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
    // ANIMATE FOUND TICKET INFORMATION
    // =========================================================

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


    // =========================================================
    // REGISTER EXIT
    // =========================================================

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

            // =================================================
            // REGISTER EXIT
            // =================================================

            ParkingTicket closedTicket =
                    exitService.registerExit(
                            selectedTicket.getId()
                    );


            // =================================================
            // LOAD RESULT INTO CONTROLS
            // =================================================

            showExitResult(
                    closedTicket
            );


            // =================================================
            // UPDATE TICKET STATUS
            // =================================================

            TA_STATE.setText(
                    getTicketStatusName(
                            closedTicket
                    )
            );


            // =================================================
            // RUN CINEMATIC ANIMATION
            // =================================================

            animateExitResult();


            // =================================================
            // PREVENT DUPLICATE EXIT
            // =================================================

            BTN_REGISTER_EXIT.setDisable(true);

            selectedTicket = null;

        } catch (RuntimeException exception) {

            showError(
                    "No se pudo registrar la salida",
                    exception.getMessage()
            );
        }
    }


    // =========================================================
    // SHOW EXIT RESULT
    // =========================================================

    private void showExitResult(
            ParkingTicket ticket) {

        LocalDateTime entryTime =
                ticket.getEntryTime();

        LocalDateTime exitTime =
                ticket.getExitTime();


        /*
         * IMPORTANT:
         *
         * This Duration belongs to java.time.Duration.
         * It is NOT JavaFX Duration.
         */
        Duration parkingDuration =
                Duration.between(
                        entryTime,
                        exitTime
                );


        long billedHours =
                calculateBilledHours(
                        parkingDuration
                );


        // =====================================================
        // EXIT DATE
        // =====================================================

        TA_EXIT_DATE.setText(
                exitTime.format(
                        DATE_FORMAT
                )
        );


        // =====================================================
        // TOTAL PARKING TIME
        // =====================================================

        TA_PARKING_TIME.setText(
                formatDuration(
                        parkingDuration
                )
        );


        // =====================================================
        // BILLED HOURS
        // =====================================================

        TA_BILLED_HOURS.setText(
                String.valueOf(
                        billedHours
                )
        );


        // =====================================================
        // AMOUNT TO COLLECT
        // =====================================================

        TA_AMOUNT_COLLECTED.setText(
                "₡ "
                + ticket
                        .getAmount()
                        .toPlainString()
        );
    }


    // =========================================================
    // CINEMATIC EXIT ANIMATION
    // =========================================================

    private void animateExitResult() {

        // =====================================================
        // PREPARE RESULT CONTROLS
        // =====================================================

        prepareNode(TA_EXIT_DATE);

        prepareNode(TA_PARKING_TIME);

        prepareNode(TA_BILLED_HOURS);

        prepareNode(TA_AMOUNT_COLLECTED);


        // =====================================================
        // STATUS ANIMATION
        // =====================================================

        FadeTransition stateFade =
                new FadeTransition(
                        javafx.util.Duration.millis(400),
                        TA_STATE
                );

        stateFade.setFromValue(0.20);

        stateFade.setToValue(1.0);


        // =====================================================
        // EXIT DATE
        // =====================================================

        ParallelTransition exitDateAnimation =
                createFadeAndSlide(
                        TA_EXIT_DATE,
                        320
                );


        // =====================================================
        // PARKING TIME
        // =====================================================

        ParallelTransition parkingTimeAnimation =
                createFadeAndSlide(
                        TA_PARKING_TIME,
                        320
                );


        // =====================================================
        // BILLED HOURS
        // =====================================================

        ParallelTransition billedHoursAnimation =
                createFadeAndSlide(
                        TA_BILLED_HOURS,
                        320
                );


        // =====================================================
        // PAUSE BEFORE AMOUNT
        // =====================================================

        PauseTransition pauseBeforeAmount =
                new PauseTransition(
                        javafx.util.Duration.millis(150)
                );


        // =====================================================
        // AMOUNT
        // =====================================================

        ParallelTransition amountAnimation =
                createFadeAndSlide(
                        TA_AMOUNT_COLLECTED,
                        450
                );


        // =====================================================
        // RESULT SEQUENCE
        // =====================================================

        SequentialTransition resultSequence =
                new SequentialTransition(

                        exitDateAnimation,

                        parkingTimeAnimation,

                        billedHoursAnimation,

                        pauseBeforeAmount,

                        amountAnimation
                );


        // =====================================================
        // STATUS + RESULT AT SAME TIME
        // =====================================================

        ParallelTransition completeAnimation =
                new ParallelTransition(
                        stateFade,
                        resultSequence
                );


        // =====================================================
        // SHOW MESSAGE AFTER ANIMATION
        // =====================================================

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


    // =========================================================
    // CALCULATE BILLED HOURS
    // =========================================================

    private long calculateBilledHours(
            Duration duration) {

        long minutes =
                duration.toMinutes();


        /*
         * Minimum charge = 1 hour.
         */
        if (minutes <= 0) {

            return 1;
        }


        /*
         * Round up.
         *
         * 10 min  -> 1 hour
         * 60 min  -> 1 hour
         * 61 min  -> 2 hours
         * 119 min -> 2 hours
         * 121 min -> 3 hours
         */
        return (minutes + 59) / 60;
    }


    // =========================================================
    // FORMAT PARKING DURATION
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


        // =====================================================
        // DAYS
        // =====================================================

        if (days > 0) {

            return days
                    + " d "
                    + hours
                    + " h "
                    + minutes
                    + " min";
        }


        // =====================================================
        // HOURS
        // =====================================================

        if (hours > 0) {

            return hours
                    + " h "
                    + minutes
                    + " min";
        }


        // =====================================================
        // MINUTES
        // =====================================================

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
    // PREPARE NODE FOR ANIMATION
    // =========================================================

    private void prepareNode(
            Node node) {

        node.setOpacity(0.0);

        node.setTranslateY(7.0);
    }


    // =========================================================
    // CREATE FADE + SLIDE
    // =========================================================

    private ParallelTransition createFadeAndSlide(
            Node node,
            double durationMillis) {

        /*
         * We write javafx.util.Duration directly because
         * java.time.Duration is already being used for
         * calculating the parking duration.
         */

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


    // =========================================================
    // CLEAR TICKET INFORMATION
    // =========================================================

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


    // =========================================================
    // CLEAR EXIT RESULT
    // =========================================================

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


    // =========================================================
    // RESET NODE
    // =========================================================

    private void resetNode(
            Node node) {

        node.setOpacity(1.0);

        node.setTranslateX(0.0);

        node.setTranslateY(0.0);
    }


    // =========================================================
    // INFORMATION MESSAGE
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

        /*
         * IMPORTANT:
         *
         * We use show() instead of showAndWait().
         *
         * This message is executed when the animation
         * finishes, so it must not block JavaFX's
         * animation/layout processing.
         */
        alert.show();
    }


    // =========================================================
    // ERROR MESSAGE
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

        /*
         * This one CAN remain showAndWait().
         *
         * Errors are produced from the normal application
         * flow and not from the animation's onFinished event.
         */
        alert.showAndWait();
    }
}