/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Justin PC
 */
public class TicketsViewController implements Initializable {

    @FXML
    private AnchorPane AP_TICKETS_VIEW;
    @FXML
    private TableView<?> TV_TICKET_MANAGEMENT;
    @FXML
    private TableColumn<?, ?> TV_RW_TICKET;
    @FXML
    private TableColumn<?, ?> TV_RW_PLATE;
    @FXML
    private TableColumn<?, ?> TV_RW_SPACE;
    @FXML
    private TableColumn<?, ?> TV_RW_TYPE_VEHICLE;
    @FXML
    private TableColumn<?, ?> TV_RW_ENTRY;
    @FXML
    private TableColumn<?, ?> TV_RW_EXIT;
    @FXML
    private TableColumn<?, ?> TV_RW_STATE_PAYMENT;
    @FXML
    private JFXTextField TF_SEARCH_TICKER_OR_PLATE;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
