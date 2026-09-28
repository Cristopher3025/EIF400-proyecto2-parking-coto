package controller;

import com.jfoenix.controls.JFXButton;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import enums.ParkingSpaceType;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import javafx.scene.layout.AnchorPane;

import javafx.util.Duration;

import model.parking.ParkingSpace;

import service.ParkingContext;

import una.ac.cr.parkingcoto.App;

import util.UiAnimations;

public class MainViewController implements Initializable {

    // =========================================================
    // CONTEXT
    // =========================================================

    private final ParkingContext context;


    // =========================================================
    // NAVIGATION
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
    // DASHBOARD - GENERAL INFORMATION
    // =========================================================

    @FXML
    private Label LBL_TOTAL_SPACES;

    @FXML
    private Label LBL_SPACES_OCCUPATED;

    @FXML
    private Label LBL_SPACES_AVAILABLE;

    @FXML
    private Label LBL_TOTAL_REVENUE;

    @FXML
    private Label LBL_ACTIVE_TICKETS;


    // =========================================================
    // DASHBOARD - OCCUPANCY
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
    // DYNAMIC CONTENT
    // =========================================================

    @FXML
    private AnchorPane AP_INFORMATION_2;

    private List<Node> dashboardInformationNodes;


    // =========================================================
    // FINAL PROGRESS VALUES
    // =========================================================

    /*
     * These variables store the real occupancy percentage.
     *
     * Example:
     *
     * 5 occupied / 10 total = 0.50
     *
     * The ProgressBar is temporarily placed at 0 during
     * animation and then grows until this value.
     */

    private double carProgress;

    private double motorcycleProgress;

    private double cargoProgress;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MainViewController(
            ParkingContext context) {

        this.context =
                Objects.requireNonNull(
                        context,
                        "Parking context cannot be null"
                );
    }


    // =========================================================
    // INITIALIZATION
    // =========================================================

    @Override
    public void initialize(
            URL url,
            ResourceBundle rb) {

        saveDashboardInformation();

        configureNavigation();

        refreshDashboard();

        animateDashboard();
    }


    // =========================================================
    // SAVE DASHBOARD
    // =========================================================

    private void saveDashboardInformation() {

        dashboardInformationNodes =
                new ArrayList<>(
                        AP_INFORMATION_2
                                .getChildren()
                );
    }


    // =========================================================
    // SHOW DASHBOARD
    // =========================================================

    private void showDashboard() {

        /*
         * Restore original Dashboard.
         */

        AP_INFORMATION_2
                .getChildren()
                .setAll(
                        dashboardInformationNodes
                );


        /*
         * Refresh information because Entry, Exit or Payment
         * may have changed the system data.
         */

        refreshDashboard();


        /*
         * General cinematic transition.
         */

        UiAnimations.fadeView(
                AP_INFORMATION_2
        );


        /*
         * Dashboard internal animation.
         */

        animateDashboard();
    }


    // =========================================================
    // REFRESH DASHBOARD
    // =========================================================

    private void refreshDashboard() {

        var queryService =
                context.getQueryService();


        var spaces =
                queryService
                        .getParkingSpaces();


        var occupiedSpaces =
                queryService
                        .getOccupiedParkingSpaces();


        var availableSpaces =
                queryService
                        .getAvailableParkingSpaces();


        var activeTickets =
                queryService
                        .getActiveTickets();


        // =====================================================
        // TOTAL SPACES
        // =====================================================

        LBL_TOTAL_SPACES.setText(
                String.valueOf(
                        spaces.size()
                )
        );


        // =====================================================
        // OCCUPIED SPACES
        // =====================================================

        LBL_SPACES_OCCUPATED.setText(
                String.valueOf(
                        occupiedSpaces.size()
                )
        );


        // =====================================================
        // AVAILABLE SPACES
        // =====================================================

        LBL_SPACES_AVAILABLE.setText(
                String.valueOf(
                        availableSpaces.size()
                )
        );


        // =====================================================
        // ACTIVE TICKETS
        // =====================================================

        LBL_ACTIVE_TICKETS.setText(
                String.valueOf(
                        activeTickets.size()
                )
        );


        // =====================================================
        // TOTAL REVENUE
        // =====================================================

        LBL_TOTAL_REVENUE.setText(
                "₡ "
                + queryService
                        .getTotalRevenue()
                        .toPlainString()
        );


        // =====================================================
        // OCCUPANCY
        // =====================================================

        updateOccupancyByType(
                spaces,
                occupiedSpaces
        );
    }


    // =========================================================
    // OCCUPANCY BY TYPE
    // =========================================================

    private void updateOccupancyByType(
            List<ParkingSpace> spaces,
            List<ParkingSpace> occupiedSpaces) {


        // =====================================================
        // CAR
        // =====================================================

        carProgress =
                updateTypeInformation(
                        ParkingSpaceType.CAR,
                        spaces,
                        occupiedSpaces,
                        LBL_SPACE_OCCUPIED_CAR,
                        LBL_PERCENTAGE_CAR
                );


        // =====================================================
        // MOTORCYCLE
        // =====================================================

        motorcycleProgress =
                updateTypeInformation(
                        ParkingSpaceType.MOTORCYCLE,
                        spaces,
                        occupiedSpaces,
                        LBL_SPACE_OCCUPIED_MOTORCYCLE,
                        LBL_PERCENTAGE_MOTORCYCLE
                );


        // =====================================================
        // CARGO
        // =====================================================

        cargoProgress =
                updateTypeInformation(
                        ParkingSpaceType.CARGO,
                        spaces,
                        occupiedSpaces,
                        LBL_SPACE_OCCUPIED_CARGO_VEHICLE,
                        LBL_PERCENTAGE_CARGO_VEHICLE
                );


        /*
         * Put the real values initially.
         *
         * animateDashboard() will temporarily move them
         * to zero before playing the animation.
         */

        PB_CAR.setProgress(
                carProgress
        );

        PB_MOTORCYCLE.setProgress(
                motorcycleProgress
        );

        PB_CARGO_VEHICLE.setProgress(
                cargoProgress
        );
    }


    // =========================================================
    // UPDATE TYPE INFORMATION
    // =========================================================

    private double updateTypeInformation(
            ParkingSpaceType type,
            List<ParkingSpace> spaces,
            List<ParkingSpace> occupiedSpaces,
            Label occupiedLabel,
            Label percentageLabel) {


        // =====================================================
        // TOTAL
        // =====================================================

        long total =
                spaces.stream()
                        .filter(
                                space ->
                                        space.getType()
                                                == type
                        )
                        .count();


        // =====================================================
        // OCCUPIED
        // =====================================================

        long occupied =
                occupiedSpaces.stream()
                        .filter(
                                space ->
                                        space.getType()
                                                == type
                        )
                        .count();


        // =====================================================
        // PERCENTAGE
        // =====================================================

        double percentage =
                total == 0
                        ? 0.0
                        : (double) occupied / total;


        // =====================================================
        // OCCUPIED / TOTAL
        // =====================================================

        occupiedLabel.setText(
                occupied
                + "/"
                + total
        );


        // =====================================================
        // PERCENTAGE LABEL
        // =====================================================

        percentageLabel.setText(
                String.format(
                        "%.0f%%",
                        percentage * 100
                )
        );


        return percentage;
    }


    // =========================================================
    // DASHBOARD CINEMATIC ANIMATION
    // =========================================================

    private void animateDashboard() {

        /*
         * Reset all nodes before playing the sequence.
         *
         * IMPORTANT:
         *
         * We only modify opacity and translateY.
         *
         * We DO NOT modify:
         *
         * - scaleX
         * - scaleY
         * - font size
         * - width
         * - height
         *
         * Therefore text never grows or shrinks.
         */

        prepareNode(
                LBL_TOTAL_SPACES
        );

        prepareNode(
                LBL_SPACES_OCCUPATED
        );

        prepareNode(
                LBL_SPACES_AVAILABLE
        );

        prepareNode(
                LBL_TOTAL_REVENUE
        );

        prepareNode(
                LBL_ACTIVE_TICKETS
        );


        prepareNode(
                LBL_SPACE_OCCUPIED_CAR
        );

        prepareNode(
                LBL_PERCENTAGE_CAR
        );


        prepareNode(
                LBL_SPACE_OCCUPIED_MOTORCYCLE
        );

        prepareNode(
                LBL_PERCENTAGE_MOTORCYCLE
        );


        prepareNode(
                LBL_SPACE_OCCUPIED_CARGO_VEHICLE
        );

        prepareNode(
                LBL_PERCENTAGE_CARGO_VEHICLE
        );


        // =====================================================
        // RESET BARS
        // =====================================================

        PB_CAR.setProgress(
                0.0
        );

        PB_MOTORCYCLE.setProgress(
                0.0
        );

        PB_CARGO_VEHICLE.setProgress(
                0.0
        );


        // =====================================================
        // TOP CARDS
        // =====================================================

        ParallelTransition totalSpacesAnimation =
                createFadeAndSlide(
                        LBL_TOTAL_SPACES,
                        280
                );


        ParallelTransition occupiedAnimation =
                createFadeAndSlide(
                        LBL_SPACES_OCCUPATED,
                        280
                );


        ParallelTransition availableAnimation =
                createFadeAndSlide(
                        LBL_SPACES_AVAILABLE,
                        280
                );


        ParallelTransition revenueAnimation =
                createFadeAndSlide(
                        LBL_TOTAL_REVENUE,
                        280
                );


        // =====================================================
        // FIRST ROW SEQUENCE
        // =====================================================

        SequentialTransition informationSequence =
                new SequentialTransition(

                        totalSpacesAnimation,

                        new PauseTransition(
                                Duration.millis(
                                        40
                                )
                        ),

                        occupiedAnimation,

                        new PauseTransition(
                                Duration.millis(
                                        40
                                )
                        ),

                        availableAnimation,

                        new PauseTransition(
                                Duration.millis(
                                        40
                                )
                        ),

                        revenueAnimation
                );


        // =====================================================
        // CAR INFORMATION
        // =====================================================

        ParallelTransition carInformation =
                new ParallelTransition(

                        createFadeAndSlide(
                                LBL_SPACE_OCCUPIED_CAR,
                                300
                        ),

                        createFadeAndSlide(
                                LBL_PERCENTAGE_CAR,
                                300
                        )
                );


        // =====================================================
        // MOTORCYCLE INFORMATION
        // =====================================================

        ParallelTransition motorcycleInformation =
                new ParallelTransition(

                        createFadeAndSlide(
                                LBL_SPACE_OCCUPIED_MOTORCYCLE,
                                300
                        ),

                        createFadeAndSlide(
                                LBL_PERCENTAGE_MOTORCYCLE,
                                300
                        )
                );


        // =====================================================
        // CARGO INFORMATION
        // =====================================================

        ParallelTransition cargoInformation =
                new ParallelTransition(

                        createFadeAndSlide(
                                LBL_SPACE_OCCUPIED_CARGO_VEHICLE,
                                300
                        ),

                        createFadeAndSlide(
                                LBL_PERCENTAGE_CARGO_VEHICLE,
                                300
                        )
                );


        // =====================================================
        // PROGRESS BARS
        // =====================================================

        Timeline carBar =
                createProgressAnimation(
                        PB_CAR,
                        carProgress,
                        650
                );


        Timeline motorcycleBar =
                createProgressAnimation(
                        PB_MOTORCYCLE,
                        motorcycleProgress,
                        650
                );


        Timeline cargoBar =
                createProgressAnimation(
                        PB_CARGO_VEHICLE,
                        cargoProgress,
                        650
                );


        // =====================================================
        // CAR ROW
        // =====================================================

        ParallelTransition carRow =
                new ParallelTransition(
                        carInformation,
                        carBar
                );


        // =====================================================
        // MOTORCYCLE ROW
        // =====================================================

        ParallelTransition motorcycleRow =
                new ParallelTransition(
                        motorcycleInformation,
                        motorcycleBar
                );


        // =====================================================
        // CARGO ROW
        // =====================================================

        ParallelTransition cargoRow =
                new ParallelTransition(
                        cargoInformation,
                        cargoBar
                );


        // =====================================================
        // ACTIVE TICKET
        // =====================================================

        ParallelTransition activeTicketsAnimation =
                createFadeAndSlide(
                        LBL_ACTIVE_TICKETS,
                        400
                );


        // =====================================================
        // COMPLETE CINEMATIC SEQUENCE
        // =====================================================

        SequentialTransition completeAnimation =
                new SequentialTransition(

                        /*
                         * First:
                         * Dashboard statistics.
                         */

                        informationSequence,


                        /*
                         * Small cinematic pause.
                         */

                        new PauseTransition(
                                Duration.millis(
                                        100
                                )
                        ),


                        /*
                         * Automobile occupancy.
                         */

                        carRow,


                        /*
                         * Small pause.
                         */

                        new PauseTransition(
                                Duration.millis(
                                        70
                                )
                        ),


                        /*
                         * Motorcycle occupancy.
                         */

                        motorcycleRow,


                        /*
                         * Small pause.
                         */

                        new PauseTransition(
                                Duration.millis(
                                        70
                                )
                        ),


                        /*
                         * Cargo occupancy.
                         */

                        cargoRow,


                        /*
                         * Final card.
                         */

                        new PauseTransition(
                                Duration.millis(
                                        80
                                )
                        ),

                        activeTicketsAnimation
                );


        completeAnimation.play();
    }


    // =========================================================
    // PREPARE NODE
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
    // FADE + SMALL SLIDE
    // =========================================================

    private ParallelTransition createFadeAndSlide(
            Node node,
            double durationMillis) {


        // =====================================================
        // FADE
        // =====================================================

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(
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


        // =====================================================
        // MOVEMENT
        // =====================================================

        TranslateTransition movement =
                new TranslateTransition(
                        Duration.millis(
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
    // PROGRESS BAR ANIMATION
    // =========================================================

    private Timeline createProgressAnimation(
            ProgressBar progressBar,
            double finalProgress,
            double durationMillis) {


        /*
         * Ensure value remains between 0 and 1.
         */

        double safeProgress =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                finalProgress
                        )
                );


        /*
         * Begin empty.
         */

        progressBar.setProgress(
                0.0
        );


        // =====================================================
        // INITIAL FRAME
        // =====================================================

        KeyFrame start =
                new KeyFrame(

                        Duration.ZERO,

                        new KeyValue(
                                progressBar
                                        .progressProperty(),
                                0.0
                        )
                );


        // =====================================================
        // FINAL FRAME
        // =====================================================

        KeyFrame finish =
                new KeyFrame(

                        Duration.millis(
                                durationMillis
                        ),

                        new KeyValue(
                                progressBar
                                        .progressProperty(),
                                safeProgress
                        )
                );


        return new Timeline(
                start,
                finish
        );
    }


    // =========================================================
    // APPLICATION SCREENS
    // =========================================================

    private void showVehicles() {

        loadView(
                "VehiclesView"
        );
    }


    private void showParkingSpaces() {

        loadView(
                "ParkingSpacesView"
        );
    }


    private void showEntry() {

        loadView(
                "EntryView"
        );
    }


    private void showExit() {

        loadView(
                "ExitView"
        );
    }


    private void showPayment() {

        loadView(
                "PaymentView"
        );
    }


    private void showTickets() {

        loadView(
                "TicketsView"
        );
    }


    private void showReports() {

        loadView(
                "ReportsView"
        );
    }


    // =========================================================
    // VIEW LOADER
    // =========================================================

    private void loadView(
            String fxml) {

        try {

            // =================================================
            // LOAD FXML
            // =================================================

            Parent view =
                    App.loadFXML(
                            fxml
                    );


            // =================================================
            // REPLACE CURRENT CONTENT
            // =================================================

            AP_INFORMATION_2
                    .getChildren()
                    .setAll(
                            view
                    );


            // =================================================
            // FIT VIEW
            // =================================================

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


            // =================================================
            // SCREEN TRANSITION
            // =================================================

            UiAnimations.fadeView(
                    view
            );


        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Unable to load view: "
                    + fxml,
                    exception
            );
        }
    }


    // =========================================================
    // NAVIGATION
    // =========================================================

    private void configureNavigation() {


        // =====================================================
        // DASHBOARD
        // =====================================================

        BTN_DASHBOARD.setOnAction(
                event ->
                        showDashboard()
        );


        // =====================================================
        // VEHICLES
        // =====================================================

        BTN_VEHICLES.setOnAction(
                event ->
                        showVehicles()
        );


        // =====================================================
        // PARKING SPACES
        // =====================================================

        BTN_SPACES.setOnAction(
                event ->
                        showParkingSpaces()
        );


        // =====================================================
        // ENTRY
        // =====================================================

        BTN_RECORD_ENTRY.setOnAction(
                event ->
                        showEntry()
        );


        // =====================================================
        // EXIT
        // =====================================================

        BTN_RECORD_EXIT.setOnAction(
                event ->
                        showExit()
        );


        // =====================================================
        // PAYMENT
        // =====================================================

        BTN_RECORD_PAYMENT.setOnAction(
                event ->
                        showPayment()
        );


        // =====================================================
        // TICKETS
        // =====================================================

        BTN_TICKETS.setOnAction(
                event ->
                        showTickets()
        );


        // =====================================================
        // REPORTS
        // =====================================================

        BTN_REPORTS.setOnAction(
                event ->
                        showReports()
        );
    }
}