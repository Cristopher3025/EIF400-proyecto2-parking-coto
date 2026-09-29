package controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import enums.ParkingSpaceType;
import enums.TicketStatus;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import javafx.fxml.FXML;

import javafx.scene.Node;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;

import model.ticket.ParkingTicket;

import service.ParkingContext;
import service.QueryService;

public class ReportsViewController {

    private final ParkingContext context;

    @FXML
    private AnchorPane AP_REPORTS;

    @FXML
    private Label LBL_TOTAL_TICKETS;

    @FXML
    private Label LBL_TOTAL_TICKETS_CLOSE;

    @FXML
    private Label LBL_TOTAL_TICKETS_PAYMENT;

    @FXML
    private Label LBL_TOTAL_TICKETS_ACTIVE;

    @FXML
    private Label LBL_TOTAL_REVENUE;

    @FXML
    private BarChart<String, Number> BC_TYPE_SPACE;

    public ReportsViewController(
            ParkingContext context) {

        this.context =
                Objects.requireNonNull(
                        context,
                        "Parking context cannot be null"
                );
    }

    @FXML
    private void initialize() {

        BC_TYPE_SPACE.setAnimated(false);

        BC_TYPE_SPACE.setLegendVisible(false);

        refresh();

        animateReport();
    }

    private void refresh() {

        QueryService queryService =
                context.getQueryService();

        List<ParkingTicket> tickets =
                queryService.getTickets();

        LBL_TOTAL_TICKETS.setText(
                String.valueOf(
                        tickets.size()
                )
        );

        LBL_TOTAL_TICKETS_ACTIVE.setText(
                String.valueOf(
                        queryService
                                .getActiveTicketCount()
                )
        );

        long closedTickets =
                tickets.stream()
                        .filter(ticket ->
                                ticket.getStatus()
                                        == TicketStatus.CLOSED
                        )
                        .count();

        LBL_TOTAL_TICKETS_CLOSE.setText(
                String.valueOf(
                        closedTickets
                )
        );

        long paidTickets =
                tickets.stream()
                        .filter(ticket ->
                                ticket.getStatus()
                                        == TicketStatus.PAID
                        )
                        .count();

        LBL_TOTAL_TICKETS_PAYMENT.setText(
                String.valueOf(
                        paidTickets
                )
        );

        LBL_TOTAL_REVENUE.setText(
                " "
                + queryService
                        .getTotalRevenue()
                        .toPlainString()
        );

        refreshOccupancyChart();
    }

    private void refreshOccupancyChart() {

        QueryService queryService =
                context.getQueryService();

        Map<ParkingSpaceType, Long> occupancy =
                queryService
                        .getOccupancyByType();

        long cars =
                occupancy.getOrDefault(
                        ParkingSpaceType.CAR,
                        0L
                );

        long motorcycles =
                occupancy.getOrDefault(
                        ParkingSpaceType.MOTORCYCLE,
                        0L
                );

        long cargo =
                occupancy.getOrDefault(
                        ParkingSpaceType.CARGO,
                        0L
                );

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        series.setName(
                "Espacios ocupados"
        );

        XYChart.Data<String, Number> carData =
                new XYChart.Data<>(
                        "Automóvil",
                        0
                );

        XYChart.Data<String, Number> motorcycleData =
                new XYChart.Data<>(
                        "Motocicleta",
                        0
                );

        XYChart.Data<String, Number> cargoData =
                new XYChart.Data<>(
                        "Carga",
                        0
                );

        series.getData().addAll(
                carData,
                motorcycleData,
                cargoData
        );

        BC_TYPE_SPACE
                .getData()
                .clear();

        BC_TYPE_SPACE
                .getData()
                .add(
                        series
                );

        BC_TYPE_SPACE.setLegendVisible(
                false
        );

        PauseTransition wait =
                new PauseTransition(
                        javafx.util.Duration.millis(
                                350
                        )
                );

        wait.setOnFinished(event -> {

            animateBar(
                    carData,
                    cars,
                    700
            );

            PauseTransition motorcycleDelay =
                    new PauseTransition(
                            javafx.util.Duration.millis(
                                    130
                            )
                    );

            motorcycleDelay.setOnFinished(e ->

                    animateBar(
                            motorcycleData,
                            motorcycles,
                            700
                    )
            );

            motorcycleDelay.play();

            PauseTransition cargoDelay =
                    new PauseTransition(
                            javafx.util.Duration.millis(
                                    260
                            )
                    );

            cargoDelay.setOnFinished(e ->

                    animateBar(
                            cargoData,
                            cargo,
                            700
                    )
            );

            cargoDelay.play();
        });

        wait.play();
    }

    private void animateBar(
            XYChart.Data<String, Number> data,
            long finalValue,
            double durationMillis) {

        DoubleProperty animatedValue =
                new SimpleDoubleProperty(
                        0
                );

        animatedValue.addListener(
                (observable,
                 oldValue,
                 newValue) -> {

                    data.setYValue(
                            newValue.doubleValue()
                    );
                }
        );

        Timeline timeline =
                new Timeline(

                        new KeyFrame(
                                javafx.util.Duration.ZERO,

                                new KeyValue(
                                        animatedValue,
                                        0
                                )
                        ),

                        new KeyFrame(
                                javafx.util.Duration.millis(
                                        durationMillis
                                ),

                                new KeyValue(
                                        animatedValue,
                                        finalValue
                                )
                        )
                );

        timeline.play();
    }

    private void animateReport() {

        prepareNode(
                LBL_TOTAL_TICKETS
        );

        prepareNode(
                LBL_TOTAL_TICKETS_ACTIVE
        );

        prepareNode(
                LBL_TOTAL_TICKETS_CLOSE
        );

        prepareNode(
                LBL_TOTAL_TICKETS_PAYMENT
        );

        prepareNode(
                LBL_TOTAL_REVENUE
        );

        SequentialTransition totalTickets =
                createFadeAndSlide(
                        LBL_TOTAL_TICKETS,
                        260
                );

        SequentialTransition activeTickets =
                createFadeAndSlide(
                        LBL_TOTAL_TICKETS_ACTIVE,
                        260
                );

        SequentialTransition closedTickets =
                createFadeAndSlide(
                        LBL_TOTAL_TICKETS_CLOSE,
                        260
                );

        SequentialTransition paidTickets =
                createFadeAndSlide(
                        LBL_TOTAL_TICKETS_PAYMENT,
                        260
                );

        SequentialTransition revenue =
                createFadeAndSlide(
                        LBL_TOTAL_REVENUE,
                        400
                );

        SequentialTransition completeSequence =
                new SequentialTransition(

                        totalTickets,

                        activeTickets,

                        closedTickets,

                        paidTickets,

                        new PauseTransition(
                                javafx.util.Duration.millis(
                                        100
                                )
                        ),

                        revenue
                );

        completeSequence.play();
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

    private SequentialTransition createFadeAndSlide(
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

        fade.setOnFinished(event ->
                node.setTranslateY(
                        0.0
                )
        );

        return new SequentialTransition(
                fade,
                movement
        );
    }
}
