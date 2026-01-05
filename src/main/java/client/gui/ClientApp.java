package client.gui;

import client.gui.controllers.*;
import client.network.Client;
import common.models.messages.GameMessage;
import common.models.messages.fromServerToClient.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ClientApp extends Application {

    private Stage primaryStage;
    private Client client;
    private Object currentController;

    private volatile boolean isIntentionalLogout = false;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.setTitle("Магическая дуэль");

        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Нажмите ESC для выхода из полноэкранного режима");
        showMainMenu();
    }

    // =============== ЭКРАНЫ ===============

    public void showMainMenu() {
        isIntentionalLogout = true;

        Platform.runLater(() -> {
            try {
                if (this.client != null) {
                    this.client.close();
                    this.client = null;
                }

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
                    primaryStage.setScene(scene);
                } else {
                    primaryStage.getScene().setRoot(root);
                    primaryStage.getScene().getStylesheets().clear();
                }

                String css = getClass().getResource("/styles/MainMenu.css").toExternalForm();
                if (!primaryStage.getScene().getStylesheets().contains(css)) {
                    primaryStage.getScene().getStylesheets().add(css);
                }

                primaryStage.setFullScreen(true);
                primaryStage.show();

            } catch (Exception e) {
                System.err.println("Критическая ошибка при загрузке MainMenu:");
                e.printStackTrace();
            }
        });
    }

    public void showWaiting(Client client) {
        this.client = client;
        this.isIntentionalLogout = false;
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

    public void showGame(Client client, GameStartMessage gsm) {
        this.client = client;
        this.isIntentionalLogout = false;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/Game.fxml"));
            Pane root = loader.load();

            GameController controller = loader.getController();
            controller.setClientApp(this, client);
            controller.initGame(gsm.getOpponentName(), client.getUsername(), gsm.getCards());

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
        if (message instanceof GameEndMessage ||
                message instanceof GameEndDueDisconnectMessage ||
                message instanceof GameEndDrawMessage) {

            if (currentController instanceof GameController gameCtrl) {
                gameCtrl.setGameOver(true);
                System.out.println("DEBUG: Игра помечена как завершенная (GameOver = true)");
            }
        }

        Platform.runLater(() -> {
            if (message instanceof AddToQueueMessage) {
                showWaiting(client);
            } else if (message instanceof GameStartMessage gsm) {
                showGame(client, gsm);
            } else if (message instanceof RoundResultMessage rrm) {
                if (currentController instanceof GameController gameCtrl) {
                    gameCtrl.onRoundResult(rrm);
                }
            } else if (message instanceof GameEndMessage gem) {
                if (currentController instanceof GameController gameCtrl) {
                    gameCtrl.onGameOver(gem.getWinnerName(), gem.getReason());
                } else {
                    showMainMenu();
                }
            } else if (message instanceof GameEndDueDisconnectMessage) {
                if (currentController instanceof GameController gameCtrl) {
                    gameCtrl.onGameOver(client.getUsername(), "OPPONENT_DISCONNECTED");
                }
            } else if (message instanceof GameEndDrawMessage) {
                if (currentController instanceof GameController gameCtrl) {
                    gameCtrl.onGameOver(null, "DRAW");
                }
            }
        });
    }

    public void onConnectionClosed() {
        if (isIntentionalLogout || client == null) {
            System.out.println("DEBUG: Соединение закрыто штатно.");
            isIntentionalLogout = false; // сброс для следующей сессии
            return;
        }

        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

            Platform.runLater(() -> {
                if (isIntentionalLogout) return;

                if (currentController instanceof GameController gc) {
                    if (gc.isGameOver()) {
                        return;
                    }
                }

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Внимание");
                alert.setHeaderText(null);
                alert.setContentText("Соединение потеряно");
                alert.showAndWait();

                showMainMenu();
            });
        }).start();
    }

    public void onConnectionError(String error) {
        if (isIntentionalLogout) return;

        Platform.runLater(() -> {
            new Alert(Alert.AlertType.ERROR, "Ошибка: " + error).showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}