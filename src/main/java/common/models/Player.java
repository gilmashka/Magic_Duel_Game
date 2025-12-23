package common.models;

import server.network.ClientHandler;

/*
Класс-модель, который олицетворяет собой игрока (то есть связь клиент-маг),
такой подход позволит сделать связь игрок-маг устойчивой,
что (по желанию пользователя) позволит сразу после первой игры снова вступить в очередь на подбор игроков (в GameManager.waitingPlayers)
 */
public class Player {

    //поля
    ClientHandler clientHandler;
    Wizard wizard;
    String name;

    //конструктор
    public Player(ClientHandler clientHandler, Wizard wizard, String name) {
        this.clientHandler = clientHandler;
        this.wizard = wizard;
        this.name = name;
    }

    //геттеры
    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public Wizard getWizard() {
        return wizard;
    }

    public String getName(){
        return name;
    }


}
