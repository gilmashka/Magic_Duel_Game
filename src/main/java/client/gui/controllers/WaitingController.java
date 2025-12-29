package client.gui.controllers;

import client.gui.ClientApp;
import client.network.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WaitingController {
    @FXML private Label statusLabel;
    private ClientApp clientApp;
    private Client client;
    private Stage primaryStage;


    public void setClientApp(ClientApp clientApp, Client client) {
        this.clientApp = clientApp;
        this.client = client;
    }


    public void updateStatus(String text) {
        statusLabel.setText(text);
    }
}