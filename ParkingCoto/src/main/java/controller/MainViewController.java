package controller;

import com.jfoenix.controls.JFXButton;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import una.ac.cr.parkingcoto.App;

public class MainViewController implements Initializable {

    // =========================================================
    // MENU
    // =========================================================

    @FXML
    private JFXButton BTN_DASHBOARD;

    @FXML
    private JFXButton BTN_VEHICLES;

    @FXML
    private JFXButton BTN_SPACES;

    @FXML
    private JFXButton BTN_RECORD_ENTRY;

    @FXML
    private JFXButton BTN_RECORD_EXIT;

    @FXML
    private JFXButton BTN_RECORD_PAYMENT;

    @FXML
    private JFXButton BTN_TICKETS;

    @FXML
    private JFXButton BTN_REPORTS;

    @FXML
    private JFXButton BTN_STATE_SYSTEM;

    // =========================================================
    // GENERAL INFORMATION
    // =========================================================

    @FXML
    private Label LBL_TOTAL_SPACES;

    @FXML
    private Label LBL_OCCUPIED;

    @FXML
    private Label LBL_AVAILABLE;

    @FXML
    private Label LBL_TOTAL_REVENUE;

    // =========================================================
    // OCCUPANCY
    // =========================================================

    @FXML
    private Label LBL_SPACE_OCCUPIED_CAR;

    @FXML
    private Label LBL_SPACE_OCCUPIED_MOTORCYCLE;

    @FXML
    private Label LBL_SPACE_OCCUPIED_CARGO_VEHICLE;

    @FXML
    private Label LBL_PERCENTAGE_CAR;

    @FXML
    private Label LBL_PERCENTAGE_MOTORCYCLE;

    @FXML
    private Label LBL_PERCENTAGE_CARGO_VEHICLE;

    @FXML
    private Label LBL_ACTIVE_TICKETS;

    // =========================================================
    // PROGRESS BARS
    // =========================================================

    @FXML
    private ProgressBar PB_CAR;

    @FXML
    private ProgressBar PB_MOTORCYCLE;

    @FXML
    private ProgressBar PB_CARGO_VEHICLE;

    // =========================================================
    // DASHBOARD CONTAINERS
    // =========================================================

    @FXML
    private StackPane SP_INFORMATION_1;

    /*
     * Dynamic application content area.
     *
     * The original Dashboard nodes contained here are saved
     * during initialization.
     */
    @FXML
    private AnchorPane AP_INFORMATION_2;

    private List<Node> dashboardInformationNodes;

    // =========================================================
    // INITIALIZATION
    // =========================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        saveDashboardInformation();

        configureNavigation();
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    private void saveDashboardInformation() {

        dashboardInformationNodes =
                new ArrayList<>(
                        AP_INFORMATION_2.getChildren()
                );
    }

    private void showDashboard() {

        AP_INFORMATION_2
                .getChildren()
                .setAll(dashboardInformationNodes);
    }

    // =========================================================
    // APPLICATION VIEWS
    // =========================================================

    private void showVehicles() {
        loadView("VehiclesView");
    }

    private void showParkingSpaces() {
        loadView("ParkingSpacesView");
    }

    private void showEntry() {
        loadView("EntryView");
    }

    private void showExit() {
        loadView("ExitView");
    }

    private void showPayment() {
        loadView("PaymentView");
    }

    /*
     * These two will be activated once their respective
     * interfaces have been created.
     */
    private void showTickets() {
        loadView("TicketsView");
    }

    private void showReports() {
        loadView("ReportsView");
    }

    // =========================================================
    // VIEW LOADER
    // =========================================================

    /**
     * Loads a view using App's shared FXMLLoader configuration.
     *
     * Therefore controllers that require ParkingContext receive
     * the same context used by the entire application.
     */
    private void loadView(String fxml) {

        try {

            Parent view =
                    App.loadFXML(fxml);

            AP_INFORMATION_2
                    .getChildren()
                    .setAll(view);

            /*
             * Makes the loaded view occupy the complete
             * available AnchorPane area.
             */
            AnchorPane.setTopAnchor(
                    view,
                    0.0
            );

            AnchorPane.setBottomAnchor(
                    view,
                    0.0
            );

            AnchorPane.setLeftAnchor(
                    view,
                    0.0
            );

            AnchorPane.setRightAnchor(
                    view,
                    0.0
            );

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Unable to load view: " + fxml,
                    exception
            );
        }
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    private void configureNavigation() {

        BTN_DASHBOARD.setOnAction(
                event -> showDashboard()
        );

        BTN_VEHICLES.setOnAction(
                event -> showVehicles()
        );

        BTN_SPACES.setOnAction(
                event -> showParkingSpaces()
        );

        BTN_RECORD_ENTRY.setOnAction(
                event -> showEntry()
        );

        BTN_RECORD_EXIT.setOnAction(
                event -> showExit()
        );

        BTN_RECORD_PAYMENT.setOnAction(
                event -> showPayment()
        );

        /*
         * Activate these when their FXML files exist.
         */

        // BTN_TICKETS.setOnAction(
        //         event -> showTickets()
        // );

        // BTN_REPORTS.setOnAction(
        //         event -> showReports()
        // );
    }
}