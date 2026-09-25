/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import service.ParkingContext;

public class ReportsViewController {

    private final ParkingContext context;

    @FXML
    private Label totalSpacesLabel;

    @FXML
    private Label availableSpacesLabel;

    @FXML
    private Label occupiedSpacesLabel;

    @FXML
    private Label revenueLabel;

    public ReportsViewController(ParkingContext context) {
        this.context = Objects.requireNonNull(context, "Parking context cannot be null");
    }

    @FXML
    private void initialize() {
        refresh();
    }

    @FXML
    private void refresh() {
        totalSpacesLabel.setText(String.valueOf(context.getQueryService().getParkingSpaces().size()));
        availableSpacesLabel.setText(String.valueOf(
                context.getQueryService().getAvailableParkingSpaces().size()));
        occupiedSpacesLabel.setText(String.valueOf(
                context.getQueryService().getOccupiedParkingSpaces().size()));
        revenueLabel.setText(context.getQueryService().getTotalRevenue().toPlainString());
    }
}
