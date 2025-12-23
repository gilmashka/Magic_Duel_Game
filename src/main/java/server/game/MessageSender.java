package server.game;

import common.models.Card;
import common.models.Player;
import common.models.messages.fromServerToClient.*;
import common.utils.JsonUtils;

/*
класс-сервис, управляющий отправкой сообщений от сервера на клиент
 */
public class MessageSender {

    //отправка сообщения об ошибке
    public static void sendError(Player player, String errorText){
        try{
            GameErrorMessage gameErrorMessage = new GameErrorMessage(errorText, player.getWizard().getType());
            String json = JsonUtils.toJson(gameErrorMessage);
            player.getClientHandler().sendMessage(json);
        }
        catch (Exception e){
            System.err.println("не удалось обработать сообщение");
            e.printStackTrace();
        }
    }

    //отправка сообщения об окончании игры
    public static void sendGameEnd(Player player1, Player player2, String winnerName, String reason){
        try{
            GameEndMessage gameEndMessage1 = new GameEndMessage(winnerName, reason, player1.getWizard().getHp(), player2.getWizard().getHp());
            String json1 = JsonUtils.toJson(gameEndMessage1);
            player1.getClientHandler().sendMessage(json1);

            GameEndMessage gameEndMessage2 = new GameEndMessage(winnerName, reason, player2.getWizard().getHp(), player1.getWizard().getHp());
            String json2 = JsonUtils.toJson(gameEndMessage2);
            player2.getClientHandler().sendMessage(json2);
        }
        catch (Exception e){
            System.err.println("не удалось обработать сообщение об окончании игры");
            e.printStackTrace();
        }
    }

    //отправка сообщения о начале игры
    public static void sendGameStart(Player player1, Player player2){
        try{
            GameStartMessage gameStartMessage1 = new GameStartMessage(player2.getName(), player1.getWizard().getHp(), player2.getWizard().getHp(), player1.getWizard().getType(), player2.getWizard().getType());
            String json1 = JsonUtils.toJson(gameStartMessage1);
            player1.getClientHandler().sendMessage(json1);

            GameStartMessage gameStartMessage2 = new GameStartMessage(player1.getName(), player2.getWizard().getHp(), player1.getWizard().getHp(), player2.getWizard().getType(), player1.getWizard().getType());
            String json2 = JsonUtils.toJson(gameStartMessage2);
            player2.getClientHandler().sendMessage(json2);
        }
        catch (Exception e){
            System.err.println("не удалось обработать сообщение о начале игры");
            e.printStackTrace();
        }
    }

    //отправка сообщения о результатах раунда
    public static void sendRoundResult(Player player1, Player player2, Card card1, Card card2, int player1getdamage, int player2getdamage){
        try{
            RoundResultMessage roundResultMessage1 = new RoundResultMessage(player1.getWizard().getHp(), player2.getWizard().getHp(), card1.getId(), card2.getId(), player1getdamage, player2getdamage);
            String json1 = JsonUtils.toJson(roundResultMessage1);
            player1.getClientHandler().sendMessage(json1);

            RoundResultMessage roundResultMessage2 = new RoundResultMessage(player2.getWizard().getHp(), player1.getWizard().getHp(), card2.getId(), card1.getId(), player2getdamage,player1getdamage);
            String json2 = JsonUtils.toJson(roundResultMessage2);
            player2.getClientHandler().sendMessage(json2);
        }
        catch (Exception e){
            System.err.println("не удалось обработать сообщение о результатах раунда");
            e.printStackTrace();
        }
    }

    //отправка сообщения клиенту о том, что он успешно добавлен в очередь ожидания
    public static void sendAddToQueue(Player player){
        try {
            AddToQueueMessage addToQueueMessage = new AddToQueueMessage(player.getName());
            String json = JsonUtils.toJson(addToQueueMessage);
            player.getClientHandler().sendMessage(json);
        } catch (Exception e) {
            System.err.println("не удалось обработать сообщение о добавлении в очередь");
            e.printStackTrace();
        }
    }

    //отправка сообщения о том, что противник отключился
    public static void sendOpponentDisconnect(Player remainingPlayer, Player disconnectPlayer){
        try{
            GameEndDueDisconnectMessage gameEndDueDisconnectMessage = new GameEndDueDisconnectMessage(disconnectPlayer.getName());
            String json = JsonUtils.toJson(gameEndDueDisconnectMessage);
            remainingPlayer.getClientHandler().sendMessage(json);
        }catch (Exception e){
            System.err.println("не удалось обработать сообщение об отключении противника");
            e.printStackTrace();
        }
    }

    //отправка сообщения о ничьей (в случае, если кончились карты, но при этом равное hp)
    public static void sendDraw(Player player1, Player player2) throws Exception {
        try{
            GameEndDrawMessage gameEndDrawMessage1 = new GameEndDrawMessage(player1.getName(), player2.getName());
            String json1 = JsonUtils.toJson(gameEndDrawMessage1);
            player1.getClientHandler().sendMessage(json1);

            GameEndDrawMessage gameEndDrawMessage2 = new GameEndDrawMessage(player1.getName(), player2.getName());
            String json2 = JsonUtils.toJson(gameEndDrawMessage2);
            player2.getClientHandler().sendMessage(json2);
        }catch (Exception e){
            System.err.println();
            e.printStackTrace();
        }

    }


}
