package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import enums.PaymentType;

import io.github.palexdev.materialfx.controls.MFXComboBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;

import javafx.collections.FXCollections;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

import model.payment.Payment;
import model.ticket.ParkingTicket;

import service.ParkingContext;
import service.PaymentService;
import service.QueryService;

public class PaymentViewController implements Initializable {

    // =========================================================
    // SERVICES
    // =========================================================

    private final PaymentService paymentService;

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
    private AnchorPane AP_PAYMENT;


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
    private JFXTextArea TA_EXIT_DATE;

    @FXML
    private JFXTextArea TA_AMOUNT_PAID;

    @FXML
    private JFXTextArea TA_STATE;


    // =========================================================
    // PAYMENT
    // =========================================================

    @FXML
    private MFXComboBox<PaymentType> CB_TYPE_PAYMENT;

    @FXML
    private JFXButton BTN_PROCESS_PAYMENT;


    // =========================================================
    // CURRENT TICKET
    // =========================================================

    private ParkingTicket selectedTicket;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PaymentViewController(
            ParkingContext context) {

        Objects.requireNonNull(
                context,
                "Parking context cannot be null"
        );

        this.paymentService =
                context.getPaymentService();

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

        configurePaymentTypes();

        configureTicketSearch();

        clearTicketInformation();

        BTN_PROCESS_PAYMENT.setDisable(true);
    }


    // =========================================================
    // PAYMENT TYPES
    // =========================================================

    private void configurePaymentTypes() {

        CB_TYPE_PAYMENT.setItems(
                FXCollections.observableArrayList(
                        PaymentType.values()
                )
        );

        CB_TYPE_PAYMENT
                .getSelectionModel()
                .clearSelection();
    }


    // =========================================================
    // TICKET SEARCH
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

    private void searchTicket(
            String ticketId) {

        if (ticketId == null
                || ticketId.isBlank()) {

            selectedTicket = null;

            clearTicketInformation();

            BTN_PROCESS_PAYMENT.setDisable(true);

            return;
        }

        try {

            ParkingTicket ticket =
                    queryService.findTicket(
                            ticketId.trim()
                    );

            selectedTicket =
                    ticket;


            // =================================================
            // SHOW INFORMATION
            // =================================================

            showTicketInformation(
                    ticket
            );


            // =================================================
            // ANIMATION
            // =================================================

            animateTicketInformation();


            // =================================================
            // ONLY CLOSED TICKETS CAN BE PAID
            // =================================================

            BTN_PROCESS_PAYMENT.setDisable(
                    ticket.getStatus()
                            != enums.TicketStatus.CLOSED
            );

        } catch (RuntimeException exception) {

            selectedTicket = null;

            clearTicketInformation();

            BTN_PROCESS_PAYMENT.setDisable(true);
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
                getSpaceTypeName(
                        ticket
                )
        );


        TA_ENTRY_DATE.setText(
                ticket
                        .getEntryTime()
                        .format(
                                DATE_FORMAT
                        )
        );


        // =====================================================
        // EXIT DATE
        // =====================================================

        if (ticket.getExitTime() != null) {

            TA_EXIT_DATE.setText(
                    ticket
                            .getExitTime()
                            .format(
                                    DATE_FORMAT
                            )
            );

        } else {

            TA_EXIT_DATE.clear();
        }


        // =====================================================
        // AMOUNT
        // =====================================================

        if (ticket.getAmount() != null) {

            TA_AMOUNT_PAID.setText(
                    "₡ "
                    + ticket
                            .getAmount()
                            .toPlainString()
            );

        } else {

            TA_AMOUNT_PAID.clear();
        }


        // =====================================================
        // STATE
        // =====================================================

        TA_STATE.setText(
                getTicketStatusName(
                        ticket
                )
        );
    }


    // =========================================================
    // ANIMATE TICKET INFORMATION
    // =========================================================

    private void animateTicketInformation() {

        prepareNode(TA_PLATE);

        prepareNode(TA_SPACE);

        prepareNode(TA_TYPE);

        prepareNode(TA_ENTRY_DATE);

        prepareNode(TA_EXIT_DATE);

        prepareNode(TA_AMOUNT_PAID);

        prepareNode(TA_STATE);


        SequentialTransition sequence =
                new SequentialTransition(

                        createFadeAndSlide(
                                TA_PLATE,
                                180
                        ),

                        createFadeAndSlide(
                                TA_SPACE,
                                180
                        ),

                        createFadeAndSlide(
                                TA_TYPE,
                                180
                        ),

                        createFadeAndSlide(
                                TA_ENTRY_DATE,
                                180
                        ),

                        createFadeAndSlide(
                                TA_EXIT_DATE,
                                180
                        ),

                        createFadeAndSlide(
                                TA_AMOUNT_PAID,
                                230
                        ),

                        createFadeAndSlide(
                                TA_STATE,
                                230
                        )
                );


        sequence.play();
    }


    // =========================================================
    // RECORD PAYMENT
    // =========================================================

    @FXML
    private void RecordPayment(
            ActionEvent event) {

        // =====================================================
        // VALIDATE TICKET
        // =====================================================

        if (selectedTicket == null) {

            showError(
                    "Ticket no seleccionado",
                    "Debe buscar un ticket cerrado "
                    + "antes de procesar el pago."
            );

            return;
        }


        // =====================================================
        // PAYMENT TYPE
        // =====================================================

        PaymentType paymentType =
                CB_TYPE_PAYMENT.getValue();


        if (paymentType == null) {

            showError(
                    "Tipo de pago no seleccionado",
                    "Debe seleccionar un método de pago."
            );

            return;
        }


        try {

            // =================================================
            // REGISTER PAYMENT
            // =================================================

            Payment payment =
                    paymentService.registerPayment(
                            selectedTicket.getId(),
                            paymentType
                    );


            /*
             * PaymentService modifies the same ParkingTicket
             * from CLOSED to PAID.
             */

            showTicketInformation(
                    selectedTicket
            );


            // =================================================
            // DISABLE BUTTON
            // =================================================

            BTN_PROCESS_PAYMENT.setDisable(
                    true
            );


            // =================================================
            // PAYMENT ANIMATION
            // =================================================

            animateSuccessfulPayment(
                    payment
            );


        } catch (RuntimeException exception) {

            showError(
                    "No se pudo procesar el pago",
                    exception.getMessage()
            );
        }
    }


    // =========================================================
    // SUCCESSFUL PAYMENT ANIMATION
    // =========================================================

    private void animateSuccessfulPayment(
            Payment payment) {

        // =====================================================
        // STATE
        // =====================================================

        TA_STATE.setOpacity(
                0.0
        );

        TA_STATE.setTranslateY(
                8.0
        );


        // =====================================================
        // AMOUNT
        // =====================================================

        TA_AMOUNT_PAID.setOpacity(
                0.0
        );

        TA_AMOUNT_PAID.setTranslateY(
                8.0
        );


        // =====================================================
        // PAYMENT TYPE
        // =====================================================

        CB_TYPE_PAYMENT.setOpacity(
                0.0
        );


        // =====================================================
        // STATE ANIMATION
        // =====================================================

        ParallelTransition stateAnimation =
                createFadeAndSlide(
                        TA_STATE,
                        400
                );


        // =====================================================
        // AMOUNT ANIMATION
        // =====================================================

        ParallelTransition amountAnimation =
                createFadeAndSlide(
                        TA_AMOUNT_PAID,
                        450
                );


        // =====================================================
        // PAYMENT TYPE FADE
        // =====================================================

        FadeTransition paymentTypeFade =
                new FadeTransition(
                        javafx.util.Duration.millis(
                                400
                        ),
                        CB_TYPE_PAYMENT
                );

        paymentTypeFade.setFromValue(
                0.0
        );

        paymentTypeFade.setToValue(
                1.0
        );


        // =====================================================
        // BUTTON EFFECT
        // =====================================================

        ScaleTransition buttonDown =
                new ScaleTransition(
                        javafx.util.Duration.millis(
                                100
                        ),
                        BTN_PROCESS_PAYMENT
                );

        buttonDown.setToX(
                0.96
        );

        buttonDown.setToY(
                0.96
        );


        ScaleTransition buttonUp =
                new ScaleTransition(
                        javafx.util.Duration.millis(
                                150
                        ),
                        BTN_PROCESS_PAYMENT
                );

        buttonUp.setToX(
                1.0
        );

        buttonUp.setToY(
                1.0
        );


        SequentialTransition buttonAnimation =
                new SequentialTransition(
                        buttonDown,
                        buttonUp
                );


        // =====================================================
        // RESULT
        // =====================================================

        SequentialTransition resultAnimation =
                new SequentialTransition(

                        buttonAnimation,

                        new PauseTransition(
                                javafx.util.Duration.millis(
                                        100
                                )
                        ),

                        amountAnimation,

                        new PauseTransition(
                                javafx.util.Duration.millis(
                                        80
                                )
                        ),

                        stateAnimation,

                        paymentTypeFade
                );


        // =====================================================
        // FINAL MESSAGE
        // =====================================================

        resultAnimation.setOnFinished(
                event -> {

                    /*
                     * IMPORTANT:
                     *
                     * showInformation() uses show(),
                     * not showAndWait().
                     *
                     * Therefore the dialog does not block
                     * JavaFX while the animation is completing.
                     */

                    showInformation(
                            "Pago registrado",
                            "El pago de ₡"
                            + payment
                                    .getAmount()
                                    .toPlainString()
                            + " fue procesado correctamente mediante "
                            + getPaymentTypeName(
                                    payment.getType()
                            )
                            + "."
                    );
                }
        );


        resultAnimation.play();
    }


    // =========================================================
    // SPACE TYPE
    // =========================================================

    private String getSpaceTypeName(
            ParkingTicket ticket) {

        switch (ticket
                .getParkingSpace()
                .getType()) {

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
    // PAYMENT TYPE
    // =========================================================

    private String getPaymentTypeName(
            PaymentType paymentType) {

        switch (paymentType) {

            case CASH:

                return "Efectivo";


            case CARD:

                return "Tarjeta";


            default:

                return paymentType.toString();
        }
    }


    // =========================================================
    // PREPARE NODE FOR ANIMATION
    // =========================================================

    private void prepareNode(
            Node node) {

        node.setOpacity(
                0.0
        );

        node.setTranslateY(
                7.0
        );
    }


    // =========================================================
    // CREATE FADE + SLIDE
    // =========================================================

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


        fade.setFromValue(
                0.0
        );

        fade.setToValue(
                1.0
        );


        TranslateTransition movement =
                new TranslateTransition(
                        javafx.util.Duration.millis(
                                durationMillis
                        ),
                        node
                );


        movement.setFromY(
                7.0
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

        node.setScaleX(
                1.0
        );

        node.setScaleY(
                1.0
        );
    }


    // =========================================================
    // CLEAR INFORMATION
    // =========================================================

    private void clearTicketInformation() {

        TA_PLATE.clear();

        TA_SPACE.clear();

        TA_TYPE.clear();

        TA_ENTRY_DATE.clear();

        TA_EXIT_DATE.clear();

        TA_AMOUNT_PAID.clear();

        TA_STATE.clear();


        // =====================================================
        // RESET ANIMATION STATES
        // =====================================================

        resetNode(
                TA_PLATE
        );

        resetNode(
                TA_SPACE
        );

        resetNode(
                TA_TYPE
        );

        resetNode(
                TA_ENTRY_DATE
        );

        resetNode(
                TA_EXIT_DATE
        );

        resetNode(
                TA_AMOUNT_PAID
        );

        resetNode(
                TA_STATE
        );

        resetNode(
                CB_TYPE_PAYMENT
        );

        resetNode(
                BTN_PROCESS_PAYMENT
        );


        // =====================================================
        // CLEAR PAYMENT SELECTION
        // =====================================================

        CB_TYPE_PAYMENT
                .getSelectionModel()
                .clearSelection();
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
         * DO NOT use showAndWait() here.
         *
         * This method is called from the onFinished event
         * of the payment animation.
         *
         * show() opens the dialog without blocking the
         * JavaFX animation/layout processing.
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
         * This can remain showAndWait().
         *
         * These errors occur during the normal action flow,
         * not from the animation's onFinished event.
         */
        alert.showAndWait();
    }
}