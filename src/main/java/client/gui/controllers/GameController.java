package client.gui.controllers;

import client.gui.ClientApp;
import client.network.Client;
import common.models.Card;
import common.models.messages.fromClientToServer.PlayCardMessage;
import common.models.messages.fromServerToClient.RoundResultMessage;
import common.services.CardService;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
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
    @FXML private StackPane gameOverOverlay;
    @FXML private Label resultTitleLabel;
    @FXML private Label resultDetailsLabel;

    private ClientApp clientApp;
    private Client client;
    private final double MAX_HP = 15.0;
    private volatile boolean isGameOver = false;

    private static final java.util.Map<String, String> CARD_IMAGES = java.util.Map.ofEntries(
            java.util.Map.entry("Точечный импульс", "Point_impulse"),
            java.util.Map.entry("Кинетическая волна", "Kinetic_wave"),
            java.util.Map.entry("Искажающий луч", "Distorting_beam"),
            java.util.Map.entry("Резонаторная сфера", "Resonance_sphere"),
            java.util.Map.entry("Призрачный барьер", "Ghost_barrier"),
            java.util.Map.entry("Изолирующая оболочка", "Insulating_shell"),
            java.util.Map.entry("Инфернальный луч", "Infernal_ray"),
            java.util.Map.entry("Магматический щит", "Magma_shield"),
            java.util.Map.entry("Абсолютный нуль", "Absolute_zero"),
            java.util.Map.entry("Пронизывающий холод", "Biting_cold"),
            java.util.Map.entry("Сталактитная завеса", "Stalactite_curtain"),
            java.util.Map.entry("Литосферный щит", "Lithospheric_shield")
    );

    public void setClientApp(ClientApp clientApp, Client client) {
        this.clientApp = clientApp;
        this.client = client;
    }

    public void initGame(String oppName, String myName, List<Integer> cardIds) {
        Platform.runLater(() -> {
            isGameOver = false;
            opponentNameLabel.setText(oppName);
            playerNameLabel.setText(myName);
            updateHpDisplays(15, 15);

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

    private Node createCardView(int cardId) {
        Card card = CardService.getCardById(cardId);

        StackPane cardRoot = new StackPane();
        cardRoot.setPrefSize(180, 260);
        cardRoot.setMinSize(180, 260);
        cardRoot.setMaxSize(180, 260);
        cardRoot.setUserData(cardId);

        String fileName = CARD_IMAGES.getOrDefault(card.getName(), String.valueOf(cardId)) + ".png";
        String imagePath = "/images/cards/" + fileName;

        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(260);
        imageView.setPreserveRatio(false);

        var resource = getClass().getResource(imagePath);
        if (resource != null) {
            imageView.setImage(new javafx.scene.image.Image(resource.toExternalForm()));
        }

        VBox infoBox = new VBox(2);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setMouseTransparent(true);
        infoBox.setMaxHeight(65);
        infoBox.setPrefHeight(65);
        infoBox.setMinWidth(180);
        infoBox.setMaxWidth(180);

        StackPane.setAlignment(infoBox, Pos.BOTTOM_CENTER);
        StackPane.setMargin(infoBox, new Insets(0, 0, 15, 0));

        Label nameLabel = new Label(card.getName().toUpperCase());
        nameLabel.setPrefWidth(160);
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        nameLabel.setStyle("-fx-text-fill: #2b1b0e; -fx-font-size: 10px; -fx-font-weight: bold; -fx-alignment: center;");

        HBox statsBox = new HBox(12);
        statsBox.setAlignment(Pos.CENTER);

        Label atkLabel = new Label("Урон: " + card.getAttack_parameter());
        atkLabel.setStyle("-fx-text-fill: #0d47a1; -fx-font-size: 10px; -fx-font-weight: bold;");

        Label defLabel = new Label("Защита: " + card.getDefence_parameter());
        defLabel.setStyle("-fx-text-fill: #b00020; -fx-font-size: 10px; -fx-font-weight: bold;");

        statsBox.getChildren().addAll(atkLabel, defLabel);
        infoBox.getChildren().addAll(nameLabel, statsBox);

        cardRoot.getChildren().addAll(imageView, infoBox);

        // Добавляем скругление для самой карты, чтобы ImageView не вылезал за углы
        Rectangle clip = new Rectangle(180, 260);
        clip.setArcWidth(25);
        clip.setArcHeight(25);
        cardRoot.setClip(clip);

        makeDraggable(cardRoot, cardId);
        return cardRoot;
    }

    private void makeDraggable(Node cardNode, int cardId) {
        cardNode.setOnDragDetected(event -> {
            Dragboard db = cardNode.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(cardId));
            db.setContent(content);
            cardNode.setOpacity(0.5);
            event.consume();
        });
        cardNode.setOnDragDone(event -> cardNode.setOpacity(1.0));
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
        StackPane.setAlignment(cardUI, Pos.CENTER);
        slot.setPrefHeight(300);
        slot.setMinHeight(300);
        slot.getChildren().add(cardUI);
        cardUI.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(300), cardUI);
        ft.setToValue(1.0);
        ft.play();
    }

    public void onRoundResult(RoundResultMessage msg) {
        Platform.runLater(() -> {
            renderCardInSlot(opponentSlot, msg.getOpponentCardId());
            animateDamage(playerDamageText, msg.getDamageToYou());
            animateDamage(opponentDamageText, msg.getDamageToOpponent());
            updateHpDisplays(msg.getYourHP(), msg.getOpponentHP());

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> {
                if (!isGameOver) { // Не очищаем поле, если игра закончилась
                    playerSlot.getChildren().clear();
                    opponentSlot.getChildren().clear();
                    cardsContainer.setDisable(false);
                }
            });
            pause.play();
        });
    }

    private void animateDamage(Label label, int damage) {
        if (damage <= 0) return;
        Platform.runLater(() -> {
            label.setText("-" + damage);
            label.setOpacity(1.0);
            javafx.animation.TranslateTransition moveUp = new javafx.animation.TranslateTransition(Duration.millis(1000), label);
            moveUp.setByY(-50);
            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(Duration.millis(1000), label);
            fadeOut.setToValue(0);
            javafx.animation.ParallelTransition parallel = new javafx.animation.ParallelTransition(moveUp, fadeOut);
            parallel.setOnFinished(e -> label.setText(""));
            parallel.play();
        });
    }

    private void updateHpDisplays(int p1Hp, int p2Hp) {
        playerHpBar.setProgress(p1Hp / MAX_HP);
        opponentHpBar.setProgress(p2Hp / MAX_HP);
        if (playerHpText != null) playerHpText.setText(p1Hp + " / 15");
        if (opponentHpText != null) opponentHpText.setText(p2Hp + " / 15");
    }

    public void onGameOver(String winner, String reason) {
        isGameOver = true;
        Platform.runLater(() -> {
            gameOverOverlay.setManaged(true);
            gameOverOverlay.setVisible(true);
            cardsContainer.setDisable(true);

            String myName = playerNameLabel.getText();

            if (winner == null || winner.isEmpty() || winner.equals("DRAW") || "DRAW".equals(reason)) {
                resultTitleLabel.setText("НИЧЬЯ");
                resultTitleLabel.setStyle("-fx-text-fill: #FFEB3B; -fx-font-size: 48px; -fx-font-weight: bold;");
                resultDetailsLabel.setText("Оба мага пали в честном бою!");
            }
            else {
                boolean isWin = winner.equals(myName);

                if (isWin) {
                    resultTitleLabel.setText("ПОБЕДА!");
                    resultTitleLabel.setStyle("-fx-text-fill: #03DAC5; -fx-font-size: 48px; -fx-font-weight: bold;");
                } else {
                    resultTitleLabel.setText("ПОРАЖЕНИЕ");
                    resultTitleLabel.setStyle("-fx-text-fill: #cf6679; -fx-font-size: 48px; -fx-font-weight: bold;");
                }

                if ("OPPONENT_DISCONNECTED".equals(reason)) {
                    resultDetailsLabel.setText("Противник покинул дуэль. Техническая победа!");
                } else {
                    resultDetailsLabel.setText("Маг " + winner + " оказался сильнее!");
                }
            }

            // Анимация появления
            FadeTransition ft = new FadeTransition(Duration.millis(500), gameOverOverlay);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        });
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    @FXML private void handleMinimize(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML private void handleWindowSize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    @FXML private void handleClose(ActionEvent event) { System.exit(0); }
    @FXML private void handleReturnToMenu() { clientApp.showMainMenu(); }
    @FXML private void handleExitGame() { System.exit(0); }

    public void setGameOver(boolean b) {
        this.isGameOver = b;
    }
}