/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.util.Objects;

import enums.PaymentType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.payment.Payment;
import service.ParkingContext;

public class PaymentViewController {

    private final ParkingContext context;

    @FXML
    private TextField ticketIdField;

    @FXML
    private ChoiceBox<PaymentType> paymentTypeChoiceBox;

    @FXML
    private Label paymentDetailsLabel;

    @FXML
    private Label messageLabel;

    public PaymentViewController(ParkingContext context) {
        this.context = Objects.requireNonNull(context, "Parking context cannot be null");
    }

    @FXML
    private void initialize() {
        paymentTypeChoiceBox.setItems(FXCollections.observableArrayList(PaymentType.values()));
        paymentTypeChoiceBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void processPayment() {
        try {
            Payment payment = context.getExitPaymentService().processExit(
                    ticketIdField.getText(), paymentTypeChoiceBox.getValue());
            paymentDetailsLabel.setText("Paid " + payment.getAmount()
                    + " by " + payment.getType());
            messageLabel.setText("");
        } catch (RuntimeException exception) {
            paymentDetailsLabel.setText("");
            messageLabel.setText(exception.getMessage());
        }
    }
}
