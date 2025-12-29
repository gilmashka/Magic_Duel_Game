package common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.models.messages.*;
import common.models.messages.fromClientToServer.ChoiceCharacterMessage;
import common.models.messages.fromClientToServer.PlayCardMessage;
import common.models.messages.fromServerToClient.AddToQueueMessage;
import common.models.messages.fromServerToClient.GameEndMessage;
import common.models.messages.fromServerToClient.GameStartMessage;
import common.models.messages.fromServerToClient.RoundResultMessage;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static GameMessage parseMessage(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        String type = root.get("type").asText();

        return switch (type) {
            // От клиента
            case "CHOICE_OF_CHARACTER" -> mapper.readValue(json, ChoiceCharacterMessage.class);
            case "PLAY_CARD" -> mapper.readValue(json, PlayCardMessage.class);

            // От сервера
            case "ADD_TO_QUEUE" -> mapper.readValue(json, AddToQueueMessage.class);
            case "GAME_START" -> mapper.readValue(json, GameStartMessage.class);
            case "ROUND_RESULT" -> mapper.readValue(json, RoundResultMessage.class);
            case "GAME_END" -> mapper.readValue(json, GameEndMessage.class);
            case "GAME_OVER" -> mapper.readValue(json, GameEndMessage.class);

            default -> throw new Exception("Неизвестный тип: " + type);
        };
    }

    public static String toJson(GameMessage message) throws Exception {
        return mapper.writeValueAsString(message);
    }
}