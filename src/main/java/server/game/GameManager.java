package server.game;


import common.models.Player;
import common.models.messages.fromClientToServer.ChoiceCharacterMessage;
import common.models.messages.GameMessage;
import common.models.messages.fromClientToServer.PlayCardMessage;
import common.services.WizardFactory;
import server.network.ClientHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
Класс-менеджер для управления всеми игровыми сессиями,
хранит в себе все игровые сессии и управляет ими,
также получает сообщения от всех клиентов, и, сортируя,
отправляет в конкретные сессии.
Иначе говоря, управляет матчмейкингом и получением сообщений от всех клиентов
 */
public class GameManager {

    //поля
    private Queue<Player> waitingPlayers = new ConcurrentLinkedQueue<>();//список всех подлюченных игроков (по их Handler-ам)
    private WizardFactory wizardFactory = new WizardFactory();//фабрика по созданию магов
    private Map<Player, GameSession> currentGameList = new ConcurrentHashMap<>(); //список всех текущих игр с указанием того, кто там играет
    private Map<ClientHandler, Player> currentPlayerList = new ConcurrentHashMap<>();//список всех игроков, которые либо ожидают, либо играют




    public void handleMessage(ClientHandler clientHandler, GameMessage message) throws Exception {
        if(message instanceof ChoiceCharacterMessage){
            Player player = new Player(clientHandler, wizardFactory.createWizard(
                    ((ChoiceCharacterMessage) message).getWizardType()), ((ChoiceCharacterMessage) message).getNameOfPlayer());//создание нового Player
            MessageSender.sendAddToQueue(player);
            waitingPlayers.add(player);//добавляем игрока в очередь ожидания
            currentPlayerList.put(player.getClientHandler(), player); // добавляем игрока в список всех подключенных игроков !!!важно, не путать с очередью на поиск игры
            tryToCreateNewGameSession();//проверяем возможность создания нового матча

        }else if(message instanceof PlayCardMessage){
            Player retiredPlayer = currentPlayerList.get(clientHandler);
            GameSession gameSession = currentGameList.get(retiredPlayer);
            gameSession.saveCard(((PlayCardMessage) message).getCardId(), clientHandler.getClientId());

        }else throw new Exception("Некорректное сообщение");
    }

    /*
    Вспомогательные методы
     */
    //метод матчмейкинга
    private void tryToCreateNewGameSession(){
        if(waitingPlayers.size() == 2){
            List<Player> pair = new ArrayList<>();
            pair.add(waitingPlayers.poll());
            pair.add(waitingPlayers.poll());
            GameSession gameSession = new GameSession(pair.get(0), pair.get(1), this);
            currentGameList.put(pair.get(0), gameSession);
            currentGameList.put(pair.get(1), gameSession);
            System.out.println("Создана новая игровая сессия: " + gameSession.getSessionId());//***лоирование***
            System.out.println("В которую были подключены игроки: " + pair.get(0).getClientHandler().getClientId() + "; " + pair.get(1).getClientHandler().getClientId());//***логирование***
            MessageSender.sendGameStart(pair.get(0), pair.get(1));
        }
    }

    //метод, определяющий поведение при отключении клиента
    public void disconnectProcessing(ClientHandler clientHandler){
        Player player = currentPlayerList.get(clientHandler);
        if (player == null){
            return;
        }
        if(waitingPlayers.contains(player)){
            waitingPlayers.remove(player);
        } else if (currentGameList.containsKey(player)) {
            GameSession gameSession = currentGameList.get(player);
            Player player1 = gameSession.getPlayer1();
            Player player2 = gameSession.getPlayer2();
            if(!player1.getClientHandler().getClientId().equals(player.getClientHandler().getClientId())){//отключившийся игрок - player2
                MessageSender.sendOpponentDisconnect(player1, player2);
                endGameSession(gameSession);
            }
            else if(!player2.getClientHandler().getClientId().equals(player.getClientHandler().getClientId())){//отключившийся игрок - player1
                MessageSender.sendOpponentDisconnect(player2, player1);
                endGameSession(gameSession);
            }
        }
    }

    //метод удаления игровой сесии, если игра завершена или кто-то отключился
    public void endGameSession(GameSession gameSession){
        //удалить текущую сессию из списка идущих матей + удаляем игроков из списка текущих игроков, которые либо играют, либо ожидают
        Player player1 = gameSession.getPlayer1();
        Player player2 = gameSession.getPlayer2();
        if(player1 != null){
            currentGameList.remove(player1);
            currentPlayerList.remove(player1.getClientHandler());
        }
        if(player2 != null){
            currentGameList.remove(player2);
            currentPlayerList.remove(player2.getClientHandler());
        }
        System.out.println("сессия " + gameSession.getSessionId() + " завершена");//***логирование***

    }

}
