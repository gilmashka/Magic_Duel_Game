package common.models.messages.fromServerToClient;

import common.models.messages.GameMessage;

/*
 * Сообщение о результате раунда
 */
public class RoundResultMessage extends GameMessage {
    private int yourHP;
    private int opponentHP;
    private int yourCardId;
    private int opponentCardId;
    private int damageToYou;
    private int damageToOpponent;

    public RoundResultMessage() {}

    public RoundResultMessage(int yourHP, int opponentHP, int yourCardId, int opponentCardId,
                              int damageToYou, int damageToOpponent) {
        this.yourHP = yourHP;
        this.opponentHP = opponentHP;
        this.yourCardId = yourCardId;
        this.opponentCardId = opponentCardId;
        this.damageToYou = damageToYou;
        this.damageToOpponent = damageToOpponent;
    }

    @Override
    public String getType() {
        return "ROUND_RESULT";
    }

    // Геттеры и сеттеры
    public int getYourHP() { return yourHP; }
    public void setYourHP(int yourHP) { this.yourHP = yourHP; }

    public int getOpponentHP() { return opponentHP; }
    public void setOpponentHP(int opponentHP) { this.opponentHP = opponentHP; }

    public int getYourCardId() { return yourCardId; }
    public void setYourCardId(int yourCardId) { this.yourCardId = yourCardId; }

    public int getOpponentCardId() { return opponentCardId; }
    public void setOpponentCardId(int opponentCardId) { this.opponentCardId = opponentCardId; }

    public int getDamageToYou() { return damageToYou; }
    public void setDamageToYou(int damageToYou) { this.damageToYou = damageToYou; }

    public int getDamageToOpponent() { return damageToOpponent; }
    public void setDamageToOpponent(int damageToOpponent) { this.damageToOpponent = damageToOpponent; }

}