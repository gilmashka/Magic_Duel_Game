package common.models.messages.fromServerToClient;

import common.models.messages.GameMessage;

public class GameEndDrawMessage extends GameMessage {

    String yourName;
    String opponentName;

    public GameEndDrawMessage(String yourName, String opponentName){
        this.yourName = yourName;
        this.opponentName = opponentName;
    }

    @Override
    public String getType() {
        return "GAME_OVER_DRAW";
    }
}
