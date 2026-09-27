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

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

            /*
             * Mostramos el ticket aunque posteriormente
             * PaymentService sea quien determine si realmente
             * puede pagarse.
             */
            selectedTicket = ticket;

            showTicketInformation(ticket);

            BTN_PROCESS_PAYMENT.setDisable(
                    ticket.getStatus()
                            != enums.TicketStatus.CLOSED
            );

        } catch (RuntimeException exception) {

            /*
             * No mostramos Alert mientras el usuario escribe.
             */
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
                getSpaceTypeName(ticket)
        );

        TA_ENTRY_DATE.setText(
                ticket
                        .getEntryTime()
                        .format(DATE_FORMAT)
        );

        if (ticket.getExitTime() != null) {

            TA_EXIT_DATE.setText(
                    ticket
                            .getExitTime()
                            .format(DATE_FORMAT)
            );

        } else {

            TA_EXIT_DATE.clear();
        }

        if (ticket.getAmount() != null) {

            TA_AMOUNT_PAID.setText(
                    "₡"
                    + ticket
                            .getAmount()
                            .toPlainString()
            );

        } else {

            TA_AMOUNT_PAID.clear();
        }

        TA_STATE.setText(
                getTicketStatusName(ticket)
        );
    }

    // =========================================================
    // RECORD PAYMENT
    // =========================================================

    @FXML
    private void RecordPayment(
            ActionEvent event) {

        if (selectedTicket == null) {

            showError(
                    "Ticket no seleccionado",
                    "Debe buscar un ticket cerrado antes de procesar el pago."
            );

            return;
        }

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

            Payment payment =
                    paymentService.registerPayment(
                            selectedTicket.getId(),
                            paymentType
                    );

            /*
             * PaymentService cambia el estado del mismo
             * ParkingTicket de CLOSED a PAID.
             */
            showTicketInformation(
                    selectedTicket
            );

            BTN_PROCESS_PAYMENT.setDisable(true);

            showInformation(
                    "Pago registrado",
                    "El pago de ₡"
                    + payment.getAmount().toPlainString()
                    + " fue procesado correctamente mediante "
                    + getPaymentTypeName(payment.getType())
                    + "."
            );

        } catch (RuntimeException exception) {

            showError(
                    "No se pudo procesar el pago",
                    exception.getMessage()
            );
        }
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

        CB_TYPE_PAYMENT
                .getSelectionModel()
                .clearSelection();
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