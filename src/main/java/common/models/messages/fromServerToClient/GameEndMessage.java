package common.models.messages.fromServerToClient;

import common.models.messages.GameMessage;

/*
 * Сообщение об окончании игры
 * Используется для всех типов окончания:
 * - смерть мага
 * - закончились карты
 */
public class GameEndMessage extends GameMessage {
    private String winnerName;
    private String reason; // "death", "cards"
    private int yourFinalHP;
    private int opponentFinalHP;

    public GameEndMessage() {}

    public GameEndMessage(String winnerName, String reason,
                          int yourFinalHP, int opponentFinalHP) {
        this.winnerName = winnerName;
        this.reason = reason;
        this.yourFinalHP = yourFinalHP;
        this.opponentFinalHP = opponentFinalHP;
    }

    @Override
    public String getType() {
        return "GAME_OVER";
    }

    // геттеры & сеттеры
    public String getWinnerName() { return winnerName; }
    public void setWinnerName(String winnerName) { this.winnerName = winnerName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getYourFinalHP() { return yourFinalHP; }
    public void setYourFinalHP(int yourFinalHP) { this.yourFinalHP = yourFinalHP; }

    public int getOpponentFinalHP() { return opponentFinalHP; }
    public void setOpponentFinalHP(int opponentFinalHP) { this.opponentFinalHP = opponentFinalHP; }
}