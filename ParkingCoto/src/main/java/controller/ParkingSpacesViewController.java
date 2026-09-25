/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.parking.ParkingSpace;
import service.ParkingContext;

public class ParkingSpacesViewController {

    private final ParkingContext context;

    @FXML
    private TableView<ParkingSpace> parkingSpacesTable;

    @FXML
    private TableColumn<ParkingSpace, String> numberColumn;

    @FXML
    private TableColumn<ParkingSpace, String> typeColumn;

    @FXML
    private TableColumn<ParkingSpace, String> statusColumn;

    public ParkingSpacesViewController(ParkingContext context) {
        this.context = Objects.requireNonNull(context, "Parking context cannot be null");
    }

    @FXML
    private void initialize() {
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        refresh();
    }

    @FXML
    private void refresh() {
        parkingSpacesTable.setItems(FXCollections.observableArrayList(
                context.getQueryService().getParkingSpaces()));
    }
}
