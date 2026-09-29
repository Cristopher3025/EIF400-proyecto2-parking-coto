package controller;

import com.jfoenix.controls.JFXTextField;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;

import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import javafx.scene.layout.AnchorPane;

import model.ticket.ParkingTicket;

import service.ParkingContext;

public class TicketsViewController implements Initializable {

    private final ParkingContext context;

    private final ObservableList<ParkingTicket> tickets =
            FXCollections.observableArrayList();

    private FilteredList<ParkingTicket> filteredTickets;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    @FXML
    private AnchorPane AP_TICKETS_VIEW;

    @FXML
    private TableView<ParkingTicket> TV_TICKET_MANAGEMENT;

    @FXML
    private TableColumn<ParkingTicket, String> TV_RW_TICKET;

    @FXML
    private TableColumn<ParkingTicket, String> TV_RW_PLATE;

    @FXML
    private TableColumn<ParkingTicket, String> TV_RW_SPACE;

    @FXML
    private TableColumn<ParkingTicket, String> TV_RW_TYPE_VEHICLE;

    @FXML
    private TableColumn<ParkingTicket, String> TV_RW_ENTRY;

    @FXML
    private TableColumn<ParkingTicket, String> TV_RW_EXIT;

    @FXML
    private TableColumn<ParkingTicket, String> TV_RW_STATE_PAYMENT;

    @FXML
    private JFXTextField TF_SEARCH_TICKER_OR_PLATE;

    public TicketsViewController(
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

        configureTable();

        configureSearch();

        configureRowAnimation();

        refresh();

        animateTableEntrance();
    }

    private void configureTable() {

        TV_RW_TICKET.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getId()
                )
        );

        TV_RW_PLATE.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getVehicle()
                                .getLicensePlate()
                )
        );

        TV_RW_SPACE.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getParkingSpace()
                                .getNumber()
                )
        );

        TV_RW_TYPE_VEHICLE.setCellValueFactory(data ->
                new SimpleStringProperty(
                        getVehicleTypeName(
                                data.getValue()
                        )
                )
        );

        TV_RW_ENTRY.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getEntryTime()
                                .format(
                                        DATE_FORMATTER
                                )
                )
        );

        TV_RW_EXIT.setCellValueFactory(data -> {

            if (data.getValue()
                    .getExitTime() == null) {

                return new SimpleStringProperty(
                        "-"
                );
            }

            return new SimpleStringProperty(
                    data.getValue()
                            .getExitTime()
                            .format(
                                    DATE_FORMATTER
                            )
            );
        });

        TV_RW_STATE_PAYMENT.setCellValueFactory(data ->
                new SimpleStringProperty(
                        getTicketStatusName(
                                data.getValue()
                        )
                )
        );
    }

    private void configureSearch() {

        filteredTickets =
                new FilteredList<>(
                        tickets,
                        ticket -> true
                );

        TV_TICKET_MANAGEMENT.setItems(
                filteredTickets
        );

        TF_SEARCH_TICKER_OR_PLATE
                .textProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            filterTickets(
                                    newValue
                            );

                            animateSearchResult();
                        }
                );
    }

    private void filterTickets(
            String searchText) {

        if (searchText == null
                || searchText.isBlank()) {

            filteredTickets.setPredicate(
                    ticket -> true
            );

            return;
        }

        String search =
                searchText
                        .trim()
                        .toLowerCase();

        filteredTickets.setPredicate(ticket -> {

            String ticketId =
                    ticket.getId()
                            .toLowerCase();

            String licensePlate =
                    ticket.getVehicle()
                            .getLicensePlate()
                            .toLowerCase();

            return ticketId.contains(search)
                    || licensePlate.contains(search);
        });
    }

    private void refresh() {

        tickets.setAll(
                context
                        .getQueryService()
                        .getTickets()
        );
    }

    private void configureRowAnimation() {

        TV_TICKET_MANAGEMENT.setRowFactory(
                tableView -> {

                    TableRow<ParkingTicket> row =
                            new TableRow<>();

                    row.itemProperty()
                            .addListener(
                                    (observable,
                                     oldTicket,
                                     newTicket) -> {

                                        if (newTicket == null) {

                                            row.setOpacity(
                                                    1.0
                                            );

                                            row.setTranslateX(
                                                    0.0
                                            );

                                            return;
                                        }

                                        animateRow(
                                                row
                                        );
                                    }
                            );

                    return row;
                }
        );
    }

    private void animateRow(
            TableRow<ParkingTicket> row) {

        row.setOpacity(
                0.0
        );

        row.setTranslateX(
                15.0
        );

        FadeTransition fade =
                new FadeTransition(
                        javafx.util.Duration.millis(
                                320
                        ),
                        row
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
                                320
                        ),
                        row
                );

        movement.setFromX(
                15.0
        );

        movement.setToX(
                0.0
        );

        fade.play();

        movement.play();
    }

    private void animateTableEntrance() {

        TV_TICKET_MANAGEMENT.setOpacity(
                0.0
        );

        TV_TICKET_MANAGEMENT.setTranslateY(
                12.0
        );

        PauseTransition pause =
                new PauseTransition(
                        javafx.util.Duration.millis(
                                100
                        )
                );

        FadeTransition fade =
                new FadeTransition(
                        javafx.util.Duration.millis(
                                500
                        ),
                        TV_TICKET_MANAGEMENT
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
                                500
                        ),
                        TV_TICKET_MANAGEMENT
                );

        movement.setFromY(
                12.0
        );

        movement.setToY(
                0.0
        );

        pause.setOnFinished(
                event -> {

                    fade.play();

                    movement.play();
                }
        );

        pause.play();
    }

    private void animateSearchResult() {

        FadeTransition fade =
                new FadeTransition(
                        javafx.util.Duration.millis(
                                160
                        ),
                        TV_TICKET_MANAGEMENT
                );

        fade.setFromValue(
                0.65
        );

        fade.setToValue(
                1.0
        );

        fade.playFromStart();
    }

    private String getVehicleTypeName(
            ParkingTicket ticket) {

        switch (ticket
                .getVehicle()
                .getRequiredSpaceType()) {

            case CAR:

                return "Autom├│vil";

            case MOTORCYCLE:

                return "Motocicleta";

            case CARGO:

                return "Veh├¡culo de carga";

            default:

                return "Desconocido";
        }
    }

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
}
