package common.models.messages.fromClientToServer;

import common.models.messages.GameMessage;

public class PlayCardMessage extends GameMessage {
    private int cardId;

    @Override
    public String getType(){
        return "PLAY_CARD";
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

}
