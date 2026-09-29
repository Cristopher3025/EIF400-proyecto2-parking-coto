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

    private final ParkingContext context;

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
    private ProgressBar PB_CAR;

    @FXML
    private ProgressBar PB_MOTORCYCLE;

    @FXML
    private ProgressBar PB_CARGO_VEHICLE;

    @FXML
    private AnchorPane AP_INFORMATION_2;

    private List<Node> dashboardInformationNodes;

    private double carProgress;

    private double motorcycleProgress;

    private double cargoProgress;

    public MainViewController(
            ParkingContext context) {

        this.context =
                Objects.requireNonNull(
                        context,
                        "Parking context cannot be null"
                );
    }

    @Override
    public void initialize(
            URL url,
            ResourceBundle rb) {

        saveDashboardInformation();

        configureNavigation();

        refreshDashboard();

        animateDashboard();
    }

    private void saveDashboardInformation() {

        dashboardInformationNodes =
                new ArrayList<>(
                        AP_INFORMATION_2
                                .getChildren()
                );
    }

    private void showDashboard() {

        AP_INFORMATION_2
                .getChildren()
                .setAll(
                        dashboardInformationNodes
                );

        refreshDashboard();

        UiAnimations.fadeView(
                AP_INFORMATION_2
        );

        animateDashboard();
    }

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

        LBL_TOTAL_SPACES.setText(
                String.valueOf(
                        spaces.size()
                )
        );

        LBL_SPACES_OCCUPATED.setText(
                String.valueOf(
                        occupiedSpaces.size()
                )
        );

        LBL_SPACES_AVAILABLE.setText(
                String.valueOf(
                        availableSpaces.size()
                )
        );

        LBL_ACTIVE_TICKETS.setText(
                String.valueOf(
                        activeTickets.size()
                )
        );

        LBL_TOTAL_REVENUE.setText(
                "Ôéí "
                + queryService
                        .getTotalRevenue()
                        .toPlainString()
        );

        updateOccupancyByType(
                spaces,
                occupiedSpaces
        );
    }

    private void updateOccupancyByType(
            List<ParkingSpace> spaces,
            List<ParkingSpace> occupiedSpaces) {

        carProgress =
                updateTypeInformation(
                        ParkingSpaceType.CAR,
                        spaces,
                        occupiedSpaces,
                        LBL_SPACE_OCCUPIED_CAR,
                        LBL_PERCENTAGE_CAR
                );

        motorcycleProgress =
                updateTypeInformation(
                        ParkingSpaceType.MOTORCYCLE,
                        spaces,
                        occupiedSpaces,
                        LBL_SPACE_OCCUPIED_MOTORCYCLE,
                        LBL_PERCENTAGE_MOTORCYCLE
                );

        cargoProgress =
                updateTypeInformation(
                        ParkingSpaceType.CARGO,
                        spaces,
                        occupiedSpaces,
                        LBL_SPACE_OCCUPIED_CARGO_VEHICLE,
                        LBL_PERCENTAGE_CARGO_VEHICLE
                );

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

    private double updateTypeInformation(
            ParkingSpaceType type,
            List<ParkingSpace> spaces,
            List<ParkingSpace> occupiedSpaces,
            Label occupiedLabel,
            Label percentageLabel) {

        long total =
                spaces.stream()
                        .filter(
                                space ->
                                        space.getType()
                                                == type
                        )
                        .count();

        long occupied =
                occupiedSpaces.stream()
                        .filter(
                                space ->
                                        space.getType()
                                                == type
                        )
                        .count();

        double percentage =
                total == 0
                        ? 0.0
                        : (double) occupied / total;

        occupiedLabel.setText(
                occupied
                + "/"
                + total
        );

        percentageLabel.setText(
                String.format(
                        "%.0f%%",
                        percentage * 100
                )
        );

        return percentage;
    }

    private void animateDashboard() {

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

        PB_CAR.setProgress(
                0.0
        );

        PB_MOTORCYCLE.setProgress(
                0.0
        );

        PB_CARGO_VEHICLE.setProgress(
                0.0
        );

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

        ParallelTransition carRow =
                new ParallelTransition(
                        carInformation,
                        carBar
                );

        ParallelTransition motorcycleRow =
                new ParallelTransition(
                        motorcycleInformation,
                        motorcycleBar
                );

        ParallelTransition cargoRow =
                new ParallelTransition(
                        cargoInformation,
                        cargoBar
                );

        ParallelTransition activeTicketsAnimation =
                createFadeAndSlide(
                        LBL_ACTIVE_TICKETS,
                        400
                );

        SequentialTransition completeAnimation =
                new SequentialTransition(

                        informationSequence,

                        new PauseTransition(
                                Duration.millis(
                                        100
                                )
                        ),

                        carRow,

                        new PauseTransition(
                                Duration.millis(
                                        70
                                )
                        ),

                        motorcycleRow,

                        new PauseTransition(
                                Duration.millis(
                                        70
                                )
                        ),

                        cargoRow,

                        new PauseTransition(
                                Duration.millis(
                                        80
                                )
                        ),

                        activeTicketsAnimation
                );

        completeAnimation.play();
    }

    private void prepareNode(
            Node node) {

        node.setOpacity(
                0.0
        );

        node.setTranslateY(
                7.0
        );
    }

    private ParallelTransition createFadeAndSlide(
            Node node,
            double durationMillis) {

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

    private Timeline createProgressAnimation(
            ProgressBar progressBar,
            double finalProgress,
            double durationMillis) {

        double safeProgress =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                finalProgress
                        )
                );

        progressBar.setProgress(
                0.0
        );

        KeyFrame start =
                new KeyFrame(

                        Duration.ZERO,

                        new KeyValue(
                                progressBar
                                        .progressProperty(),
                                0.0
                        )
                );

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

    private void loadView(
            String fxml) {

        try {

            Parent view =
                    App.loadFXML(
                            fxml
                    );

            AP_INFORMATION_2
                    .getChildren()
                    .setAll(
                            view
                    );

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

    private void configureNavigation() {

        BTN_DASHBOARD.setOnAction(
                event ->
                        showDashboard()
        );

        BTN_VEHICLES.setOnAction(
                event ->
                        showVehicles()
        );

        BTN_SPACES.setOnAction(
                event ->
                        showParkingSpaces()
        );

        BTN_RECORD_ENTRY.setOnAction(
                event ->
                        showEntry()
        );

        BTN_RECORD_EXIT.setOnAction(
                event ->
                        showExit()
        );

        BTN_RECORD_PAYMENT.setOnAction(
                event ->
                        showPayment()
        );

        BTN_TICKETS.setOnAction(
                event ->
                        showTickets()
        );

        BTN_REPORTS.setOnAction(
                event ->
                        showReports()
        );
    }
}
