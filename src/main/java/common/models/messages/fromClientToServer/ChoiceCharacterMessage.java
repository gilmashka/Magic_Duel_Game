package common.models.messages.fromClientToServer;

import common.models.messages.GameMessage;

public class ChoiceCharacterMessage extends GameMessage {
    private String wizardType;
    private String nameOfPlayer;

    @Override
    public String getType(){
        return "CHOICE_OF_CHARACTER";
    }

    public String getNameOfPlayer() {
        return nameOfPlayer;
    }

    public void setNameOfPlayer(String nameOfPlayer) {
        this.nameOfPlayer = nameOfPlayer;
    }

    public String getWizardType() {
        return wizardType;
    }

    public void setWizardType(String wizardType) {
        this.wizardType = wizardType;
    }
}
