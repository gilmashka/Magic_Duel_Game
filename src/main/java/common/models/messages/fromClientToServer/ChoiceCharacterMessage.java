package common.models.messages.fromClientToServer;

import common.models.messages.GameMessage;

public class ChoiceCharacterMessage extends GameMessage {
    private String wizardType;
    private String nameOfPlayer;

    public ChoiceCharacterMessage(String wizardType, String nameOfPlayer) {
        this.wizardType = wizardType;
        this.nameOfPlayer = nameOfPlayer;
    }

    public ChoiceCharacterMessage() {}

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
