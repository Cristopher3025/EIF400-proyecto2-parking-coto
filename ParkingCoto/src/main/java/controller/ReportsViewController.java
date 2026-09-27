/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import service.ParkingContext;

public class ReportsViewController {

    private final ParkingContext context;

    private Label totalSpacesLabel;

    private Label availableSpacesLabel;

    private Label occupiedSpacesLabel;

    private Label revenueLabel;
    @FXML
    private AnchorPane AP_REPORTS;
    @FXML
    private Label LBL_TOTAL_TICKETS;
    @FXML
    private Label LBL_TOTAL_TICKETS_CLOSE;
    @FXML
    private Label LBL_TOTAL_TICKETS_PAYMENT;
    @FXML
    private BarChart<?, ?> BC_TYPE_SPACE;
    @FXML
    private Label LBL_TOTAL_TICKETS_ACTIVE;
    @FXML
    private Label LBL_TOTAL_REVENUE;

    public ReportsViewController(ParkingContext context) {
        this.context = Objects.requireNonNull(context, "Parking context cannot be null");
    }

    private void initialize() {
        refresh();
    }

    private void refresh() {
        totalSpacesLabel.setText(String.valueOf(context.getQueryService().getParkingSpaces().size()));
        availableSpacesLabel.setText(String.valueOf(
                context.getQueryService().getAvailableParkingSpaces().size()));
        occupiedSpacesLabel.setText(String.valueOf(
                context.getQueryService().getOccupiedParkingSpaces().size()));
        revenueLabel.setText(context.getQueryService().getTotalRevenue().toPlainString());
    }
}
