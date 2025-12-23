package common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.models.messages.*;
import common.models.messages.fromClientToServer.ChoiceCharacterMessage;
import common.models.messages.fromClientToServer.PlayCardMessage;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    //Из JSON в объект
    public static GameMessage parseMessage(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        String type = root.get("type").asText();

        if (type.equals("CHOICE_OF_CHARACTER")) {
            return mapper.readValue(json, ChoiceCharacterMessage.class);
        } else if (type.equals("PLAY_CARD")) {
            return mapper.readValue(json, PlayCardMessage.class);
        } else if(type == null){
            throw new Exception("Пустое сообщение");
        }
        else {
            throw new Exception("Неизвестный тип: " + type);
        }
    }

    //Из объекта в JSON
    public static String toJson(GameMessage message) throws Exception {
        return mapper.writeValueAsString(message);
    }
}