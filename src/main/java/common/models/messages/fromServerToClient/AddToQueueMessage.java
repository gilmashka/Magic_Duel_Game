package common.models.messages.fromServerToClient;

import common.models.Player;
import common.models.messages.GameMessage;

public class AddToQueueMessage extends GameMessage {
    private String playerName;

    public AddToQueueMessage(String playerName){
        this.playerName = playerName;
    }

    @Override
    public String getType() {
        return "ADD_TO_QUEUE";
    }
}
