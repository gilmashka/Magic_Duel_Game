package client.gui.controllers;

import client.gui.ClientApp;
import client.network.Client;
import common.models.Card;
import common.models.messages.fromClientToServer.PlayCardMessage;
import common.models.messages.fromServerToClient.RoundResultMessage;
import common.services.CardService;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.*;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.List;

public class GameController {

    @FXML private Label opponentNameLabel;
    @FXML private Label playerNameLabel;
    @FXML private ProgressBar opponentHpBar;
    @FXML private ProgressBar playerHpBar;
    @FXML private Label playerHpText;
    @FXML private Label opponentHpText;
    @FXML private HBox cardsContainer;
    @FXML private HBox battleField;
    @FXML private StackPane playerSlot;
    @FXML private StackPane opponentSlot;
    @FXML private Label playerDamageText;
    @FXML private Label opponentDamageText;

    private ClientApp clientApp;
    private Client client;
    private final double MAX_HP = 30.0;
    private Stage primaryStage;

    public void setClientApp(ClientApp clientApp, Client client) {
        this.clientApp = clientApp;
        this.client = client;
    }

    /*
      Инициализация игры при получении GameStartMessage
     */
    public void initGame(String oppName, List<Integer> cardIds) {
        Platform.runLater(() -> {
            opponentNameLabel.setText(oppName);
            updateHpDisplays(30, 30);

            cardsContainer.getChildren().clear();
            playerSlot.getChildren().clear();
            opponentSlot.getChildren().clear();

            for (Integer id : cardIds) {
                Node cardView = createCardView(id);
                if (cardView != null) {
                    cardsContainer.getChildren().add(cardView);
                }
            }
        });
    }

    /*
      Создание визуального представления карты
     */
    private Node createCardView(int cardId) {
        common.models.Card card = common.services.CardService.getCardById(cardId);

        VBox cardNode = new VBox(15);
        cardNode.setAlignment(javafx.geometry.Pos.CENTER);
        cardNode.setPrefSize(140, 200);

        cardNode.setStyle(
                "-fx-background-color: #1e1e1e; " +
                        "-fx-border-color: #03DAC5; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );

        Label title = new Label(card.getName().toUpperCase());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        VBox statsBox = new VBox(5);
        statsBox.setAlignment(javafx.geometry.Pos.CENTER);

        Label atkLabel = new Label("АТАКА: " + card.getAttack_parameter());
        atkLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");

        Label defLabel = new Label("ЗАЩИТА: " + card.getDefence_parameter());
        defLabel.setStyle("-fx-text-fill: #4ecdc4; -fx-font-weight: bold;");

        statsBox.getChildren().addAll(atkLabel, defLabel);

        cardNode.getChildren().addAll(title, statsBox);

        cardNode.setUserData(cardId);
        makeDraggable(cardNode, cardId);

        return cardNode;
    }

    private void makeDraggable(Node cardNode, int cardId) {
        cardNode.setOnDragDetected(event -> {
            Dragboard db = cardNode.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(cardId)); // Передаем ID карты
            db.setContent(content);

            cardNode.setOpacity(0.5);
            event.consume();
        });

        cardNode.setOnDragDone(event -> {
            cardNode.setOpacity(1.0);
        });
    }

    @FXML
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    @FXML
    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasString()) {
            int cardId = Integer.parseInt(db.getString());

            try {
                client.sendMessage(new PlayCardMessage(cardId));

                renderCardInSlot(playerSlot, cardId);

                cardsContainer.getChildren().removeIf(node ->
                        node.getUserData() != null && node.getUserData().equals(cardId)
                );

                cardsContainer.setDisable(true);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void renderCardInSlot(StackPane slot, int cardId) {
        slot.getChildren().clear();

        Node cardUI = createCardView(cardId);

        slot.getChildren().add(cardUI);

        cardUI.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(300), cardUI);
        ft.setToValue(1.0);
        ft.play();
    }

    /*
      Обновление состояния после раунда
     */
    public void onRoundResult(RoundResultMessage msg) {
        Platform.runLater(() -> {
            renderCardInSlot(opponentSlot, msg.getOpponentCardId());

            animateDamage(playerDamageText, msg.getDamageToYou());
            animateDamage(opponentDamageText, msg.getDamageToOpponent());

            updateHpDisplays(msg.getYourHP(), msg.getOpponentHP());

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
            pause.setOnFinished(e -> {
                playerSlot.getChildren().clear();
                opponentSlot.getChildren().clear();
                cardsContainer.setDisable(false);
            });
            pause.play();
        });
    }

    private void animateDamage(Label label, int damage) {
        if (damage <= 0) return;

        Platform.runLater(() -> {
            label.setText("-" + damage);
            label.setOpacity(1.0);
            label.setTranslateY(0);

            javafx.animation.TranslateTransition moveUp = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(1000), label);
            moveUp.setByY(-50);


            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.millis(1000), label);
            fadeOut.setToValue(0);

            javafx.animation.ParallelTransition parallel = new javafx.animation.ParallelTransition(moveUp, fadeOut);
            parallel.setOnFinished(e -> label.setText("")); // Очищаем текст после завершения
            parallel.play();
        });
    }

    public void onGameOver(String winner, String reason) {
        cardsContainer.setDisable(true);

        clientApp.showGameOverScreen(winner, reason);
    }

    private void updateHpDisplays(int p1Hp, int p2Hp) {
        playerHpBar.setProgress(p1Hp / MAX_HP);
        opponentHpBar.setProgress(p2Hp / MAX_HP);

        if (playerHpText != null) playerHpText.setText(p1Hp + " / 30");
        if (opponentHpText != null) opponentHpText.setText(p2Hp + " / 30");
    }

}