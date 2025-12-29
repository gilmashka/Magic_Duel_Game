package client.network;

import common.models.messages.fromClientToServer.ChoiceCharacterMessage;
import common.models.messages.fromClientToServer.PlayCardMessage;

/*
Класс-сервис для отправки сообщений от клиента к серверу
 */
public class ClientMessageSender {

    private final Client client;

    public ClientMessageSender(Client client) {
        this.client = client;
    }

    // Отправка выбора мага
    public void sendChoiceCharacter(String wizardType, String playerName) {
        try {
            ChoiceCharacterMessage message = new ChoiceCharacterMessage(wizardType, playerName);
            client.sendMessage(message);
        } catch (Exception e) {
            System.err.println("Ошибка при отправке выбора мага: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Отправка хода картой
    public void sendPlayCard(int cardId) {
        try {
            PlayCardMessage message = new PlayCardMessage(cardId);
            client.sendMessage(message);
        } catch (Exception e) {
            System.err.println("Ошибка при отправке хода: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
