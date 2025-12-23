package server.game;

import common.models.Card;
import common.models.Player;
import common.services.CardService;

/*
Класс, каждый экземпляр которого представляет собой отдельную игровую сессию,
в которой происходит противостояние двух магов
 */
public class GameSession {

    //поля
    //поля "постоянного типа", не изменяются в рамках одной сессии
    private final int sessionId;
    private final Player player1;
    private final Player player2;

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    //поля, реализующие сыгранные карты
    private Card player1Card;
    private Card player2Card;
    //сеттеры для установки карты на каждый ход
    public void setPlayer1Card(Card player1Card) {
        this.player1Card = player1Card;
    }
    public void setPlayer2Card(Card player2Card) {
        this.player2Card = player2Card;
    }
    //поля для проверки единственности хода
    private boolean player1IsWent;
    private boolean player2IsWent;

    public void setPlayer1IsWent(boolean player1IsWent) {
        this.player1IsWent = player1IsWent;
    }

    public void setPlayer2IsWent(boolean player2IsWent) {
        this.player2IsWent = player2IsWent;
    }

    //поле для автоматического присвоения уникального id для каждой игровой сессии
    private static int currentId = 1;

    //поле-ссылка на gameManager
    private final GameManager gameManager;


    //конструктор
    public GameSession(Player player1, Player player2, GameManager gameManager){
        this.sessionId = currentId++;
        this.player1 = player1;
        this.player2 = player2;
        this.player1IsWent = false;
        this.player2IsWent = false;
        this.gameManager = gameManager;
    }

    //геттер
    public int getSessionId(){
        return sessionId;
    }

    /*
    вспомогательные методы
     */
    //метод, сохраняющий карту и решающий, к какому игроку она принаджелит
    public void saveCard(int cardId, String clientId) throws Exception{
        try{
            if(clientId.equals(player1.getClientHandler().getClientId())){
                if(isValidCard(player1, cardId) && !player1IsWent){
                    setPlayer1Card(CardService.getFullCardList().get(cardId));
                    setPlayer1IsWent(true);
                }
                else if(!isValidCard(player1, cardId)){
                    MessageSender.sendError(player1, "INCORRECT_CARD");
                }
                else MessageSender.sendError(player1, "ALREADY_WENT");
            }
            else if(clientId.equals(player2.getClientHandler().getClientId())){
                if(isValidCard(player2, cardId) && !player2IsWent){
                    setPlayer2Card(CardService.getFullCardList().get(cardId));
                    setPlayer2IsWent(true);
                }
                else if(!isValidCard(player2, cardId)){
                    MessageSender.sendError(player2, "INCORRECT_CARD");
                }
                else MessageSender.sendError(player2, "ALREADY_WENT");
            }
        }catch (Exception e){
            System.err.println("неверный id клиента");
            e.printStackTrace();
        }
        //если оба игрока сходили (читай оба поля с картами не null), запускаем начало раунда
        if(player1Card !=null && player2Card !=null){
            try {
                round();
            }
            finally {
                player1Card = null; player2Card = null; //если ход совершен, обнуляем все карты
                player1IsWent = false; player2IsWent = false; //обнуление на следующий раунд
            }
        }
    }

    /*
    методы, реализующие игровые моменты
     */
    //метод раунда
    public void round(){
        try {
            server.game.GameLogic.ResultsDTO resultsDTO =  GameLogic.roundResult(player1Card, player2Card);
            player1.getWizard().playCard(player1Card); player2.getWizard().playCard(player2Card); //маги "играют" карты, уменьшая их количество в своих колодах
            player1.getWizard().takeDamage(resultsDTO.getWizard1ChangeHp());//получение урона первым от второго
            player2.getWizard().takeDamage(resultsDTO.getWizard2ChangeHp());//получение урона вторым от первого
        /*
        проверка состояний, при которых игра объявляется законченной
         */
            //проверка "живости"
            if(!player1.getWizard().isAlive() || !player2.getWizard().isAlive()){
                if(!player1.getWizard().isAlive()){
                    MessageSender.sendGameEnd(player1, player2, player2.getName(), "GAME_OVER_REASON_DEATH");
                    gameManager.endGameSession(this);
                }
                else {
                    MessageSender.sendGameEnd(player1, player2, player1.getName(), "GAME_OVER_REASON_DEATH");
                    gameManager.endGameSession(this);
                }

            }
            //проверка наличия карт (читай проверка, сыграно ли 8 раундов и все карты)
            else if(!player1.getWizard().deckIsNotEmpty() || !player2.getWizard().deckIsNotEmpty()){
                if(!player1.getWizard().deckIsNotEmpty()){
                    MessageSender.sendGameEnd(player1, player2, player2.getName(), "GAME_OVER_REASON_NO_MORE_CARD");
                    gameManager.endGameSession(this);
                }
                else {
                    MessageSender.sendGameEnd(player1, player2, player1.getName(), "GAME_OVER_REASON_NO_MORE_CARD");
                    gameManager.endGameSession(this);
                }
            }

            //если ничего из вышеперечисленного не произошло
            else{
                MessageSender.sendRoundResult(player1, player2, player1Card, player2Card, resultsDTO.getWizard1ChangeHp(), resultsDTO.getWizard2ChangeHp());
            }
        }catch (Exception e){
            System.err.println("некорректная обработка результатов раунда");
            e.printStackTrace();
        }

    }

    //вспомогательные методы
    private boolean isValidCard(Player player, Integer cardId){
        if(CardService.isInCardList(cardId) && player.getWizard().hasCard(cardId)){
            return true;
        }
        else return false;
    }

}
