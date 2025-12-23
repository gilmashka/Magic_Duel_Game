package common.models.messages.fromServerToClient;

import common.models.messages.GameMessage;

/*
 * Сообщение об ошибке от сервера к клиенту
 * Используется для различных типов ошибок:
 * - карта не существует
 * - карты нет у игрока
 * - игрок уже сходил
 * - технические ошибки
 */
public class GameErrorMessage extends GameMessage {
    private String errorText;
    private String playerName; // у какого игрока ошибка

    public GameErrorMessage() {}

    public GameErrorMessage(String errorText, String playerName) {
        this.errorText = errorText;
        this.playerName = playerName;
    }

    @Override
    public String getType() {
        return "ERROR";
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}