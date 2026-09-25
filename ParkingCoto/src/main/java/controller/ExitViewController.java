/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.ticket.ParkingTicket;
import service.ParkingContext;

public class ExitViewController {

    private final ParkingContext context;

    @FXML
    private TextField ticketIdField;

    @FXML
    private Label ticketDetailsLabel;

    @FXML
    private Label messageLabel;

    public ExitViewController(ParkingContext context) {
        this.context = Objects.requireNonNull(context, "Parking context cannot be null");
    }

    @FXML
    private void findTicket() {
        try {
            ParkingTicket ticket = context.getQueryService().findActiveTicket(ticketIdField.getText());
            ticketDetailsLabel.setText("Vehicle: " + ticket.getVehicle().getLicensePlate()
                    + " | Space: " + ticket.getParkingSpace().getNumber()
                    + " | Entry: " + ticket.getEntryTime());
            messageLabel.setText("");
        } catch (RuntimeException exception) {
            ticketDetailsLabel.setText("");
            messageLabel.setText(exception.getMessage());
        }
    }
}
