package client.gui;

import client.gui.controllers.*;
import client.network.Client;
import common.models.messages.GameMessage;
import common.models.messages.fromServerToClient.AddToQueueMessage;
import common.models.messages.fromServerToClient.GameEndMessage;
import common.models.messages.fromServerToClient.GameStartMessage;
import common.models.messages.fromServerToClient.RoundResultMessage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {

    private Object currentScreen;
    private Stage primaryStage;
    private Client client;
    private Object currentController;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.setTitle("Магическая дуэль");

        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Нажмите ESC для выхода из полноэкранного режима");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        showMainMenu();
    }

    // =============== ЭКРАНЫ ===============

    public void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/MainMenu.fxml"));
            Pane root = loader.load();

            MainMenuController controller = loader.getController();
            controller.setClientApp(this, primaryStage);
            currentController = controller;

            if (primaryStage.getScene() == null) {
                Scene scene = new Scene(root);

                scene.setOnKeyPressed(event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.F11) {
                        primaryStage.setFullScreen(!primaryStage.isFullScreen());
                    }
                });

                scene.getStylesheets().add(getClass().getResource("/styles/MainMenu.css").toExternalForm());
                primaryStage.setScene(scene);
            } else {
                primaryStage.getScene().setRoot(root);
                primaryStage.getScene().getStylesheets().clear();
                primaryStage.getScene().getStylesheets().add(getClass().getResource("/styles/MainMenu.css").toExternalForm());
            }

            primaryStage.setFullScreen(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showWaiting(Client client) {
        this.client = client;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/Waiting.fxml"));
            Pane root = loader.load();

            WaitingController controller = loader.getController();
            controller.setClientApp(this, client);
            currentController = controller;

            primaryStage.getScene().setRoot(root);
            primaryStage.getScene().getStylesheets().clear();
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/styles/waiting.css").toExternalForm());
            primaryStage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGame(Client client, common.models.messages.fromServerToClient.GameStartMessage gsm) {
        this.client = client;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/Game.fxml"));
            Pane root = loader.load();

            GameController controller = loader.getController();
            controller.setClientApp(this, client);

            controller.initGame(gsm.getOpponentName(), gsm.getCards());

            currentController = controller;

            primaryStage.getScene().setRoot(root);
            primaryStage.getScene().getStylesheets().clear();
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/styles/game.css").toExternalForm());
            primaryStage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============== СЕТЕВЫЕ МЕТОДЫ ===============

    public void handleMessage(GameMessage message) {
        Platform.runLater(() -> {
            if (message instanceof AddToQueueMessage) {
                showWaiting(client);
            }
            else if (message instanceof GameStartMessage gsm) {
                showGame(client, gsm);
            }
            else if (message instanceof RoundResultMessage rrm) {
                if (currentController instanceof GameController gameCtrl) {
                    gameCtrl.onRoundResult(rrm);
                }
            }
            else if (message instanceof GameEndMessage gem) {
                if (currentController instanceof GameController gameCtrl) {
                    gameCtrl.onGameOver(gem.getWinnerName(), gem.getReason());
                } else {
                    showMainMenu();
                }
            }
        });
    }

    public void showStartMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/MainMenu.fxml"));
            Parent root = loader.load();

            primaryStage.getScene().setRoot(root);

            this.currentController = loader.getController();

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке начального меню");
            e.printStackTrace();
        }
    }

    // Обработка отключения
    public void onConnectionClosed() {
        Platform.runLater(() -> {
            new Alert(Alert.AlertType.WARNING, "Соединение потеряно").showAndWait();
            showMainMenu();
        });
    }

    // Обработка ошибки
    public void onConnectionError(String error) {
        Platform.runLater(() -> {
            new Alert(Alert.AlertType.ERROR, "Ошибка: " + error).showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}