package common.models.messages.fromServerToClient;

import common.models.messages.GameMessage;

public class GameEndDueDisconnectMessage extends GameMessage {

    private String disconnectPlayerName;


    public GameEndDueDisconnectMessage(String remainingPlayerName){
        this.disconnectPlayerName = remainingPlayerName;
    }


    @Override
    public String getType() {
        return "DISCONNECT";
    }
}
