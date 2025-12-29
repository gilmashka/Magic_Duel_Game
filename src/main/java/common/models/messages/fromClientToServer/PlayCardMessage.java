package common.models.messages.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import common.models.messages.GameMessage;

public class PlayCardMessage extends GameMessage {
    private int cardId;

    public PlayCardMessage(int cardId) {
        this.cardId = cardId;
    }

    public PlayCardMessage() {}

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
