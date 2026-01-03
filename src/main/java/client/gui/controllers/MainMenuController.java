package client.gui.controllers;

import client.gui.ClientApp;
import client.network.Client;
import common.models.messages.fromClientToServer.ChoiceCharacterMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML private Button pyromancerCard;
    @FXML private Button cryomancerCard;
    @FXML private Button geomancerCard;
    @FXML private Button startButton;
    @FXML private TextField nameField;

    private String selectedMageType = null;
    private ClientApp clientApp;
    private Stage primaryStage;

    @FXML
    public void initialize() {
        nameField.textProperty().addListener((obs, oldVal, newVal) -> updateStartButtonState());
    }

    @FXML
    private void onMageSelected(javafx.event.ActionEvent event) {
        Button clicked = (Button) event.getSource();

        pyromancerCard.getStyleClass().remove("selected");
        cryomancerCard.getStyleClass().remove("selected");
        geomancerCard.getStyleClass().remove("selected");

        clicked.getStyleClass().add("selected");

        if (clicked == pyromancerCard) selectedMageType = "PYROMANCER";
        else if (clicked == cryomancerCard) selectedMageType = "CRYOMANCER";
        else if (clicked == geomancerCard) selectedMageType = "GEOMANCER";

        updateStartButtonState();
    }

    @FXML
    private void onStartGame() {
        String playerName = nameField.getText().trim();
        if (playerName.isEmpty() || selectedMageType == null) return;

        try {
            Client client = new Client(clientApp, "localhost", 1234, playerName);

            client.sendMessage(new ChoiceCharacterMessage(selectedMageType, playerName));

            clientApp.showWaiting(client);

        } catch (IOException e) {
            clientApp.onConnectionError("Не удалось подключиться к серверу: " + e.getMessage());
        }
    }

    private void updateStartButtonState() {
        startButton.setDisable(selectedMageType == null || nameField.getText().trim().isEmpty());
    }

    // Для связи с ClientApp
    public void setClientApp(ClientApp clientApp, Stage stage) {
        this.clientApp = clientApp;
        this.primaryStage = stage;
    }
}