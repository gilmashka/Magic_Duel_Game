package common.models.messages.fromServerToClient;

import common.models.messages.GameMessage;

import java.util.List;

/*
 * Сообщение о начале игры
 */
public class GameStartMessage extends GameMessage {
    private String opponentName;
    private int yourHP;
    private int opponentHP;
    private String yourWizardType;
    private String opponentWizardType;
    List<Integer> cards;

    public GameStartMessage() {}

    public GameStartMessage(String opponentName, int yourHP, int opponentHP,
                            String yourWizardType, String opponentWizardType) {
        this.opponentName = opponentName;
        this.yourHP = yourHP;
        this.opponentHP = opponentHP;
        this.yourWizardType = yourWizardType;
        this.opponentWizardType = opponentWizardType;
    }

    public GameStartMessage(String opponentName, int yourHP, int opponentHP,
                            String yourWizardType, String opponentWizardType, List<Integer> cards1) {
        this.opponentName = opponentName;
        this.yourHP = yourHP;
        this.opponentHP = opponentHP;
        this.yourWizardType = yourWizardType;
        this.opponentWizardType = opponentWizardType;
        this.cards = cards1;
    }

    @Override
    public String getType() {
        return "GAME_START";
    }

    // Геттеры и сеттеры
    public String getOpponentName() { return opponentName; }
    public void setOpponentName(String opponentName) { this.opponentName = opponentName; }

    public int getYourHP() { return yourHP; }
    public void setYourHP(int yourHP) { this.yourHP = yourHP; }

    public int getOpponentHP() { return opponentHP; }
    public void setOpponentHP(int opponentHP) { this.opponentHP = opponentHP; }

    public String getYourWizardType() { return yourWizardType; }
    public void setYourWizardType(String yourWizardType) { this.yourWizardType = yourWizardType; }

    public List<Integer> getCards() {return cards;}
    public void setCards(List<Integer> cards) {this.cards = cards;}

    public String getOpponentWizardType() { return opponentWizardType; }
    public void setOpponentWizardType(String opponentWizardName) { this.opponentWizardType = opponentWizardName; }
}