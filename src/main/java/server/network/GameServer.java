package server.network;

import server.game.GameManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    //server_socket
    private ServerSocket serverSocket;
    private GameManager gameManager;

    // конструктор
    public GameServer(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
        this.gameManager = new GameManager();
    }

    /*
    основные методы сервера
     */
    //ожидание подключение, создание подключения
    public void start(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, gameManager);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException e){
            if(!serverSocket.isClosed()){
                System.out.println("Ошибка при попытке подключения"); //***логирование***
            }
        }
    }

    //сворачивание сервера
    public void stop() throws IOException{
        if(serverSocket != null){
            serverSocket.close();
        }
    }



    /*
    запуск сервера
    */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        GameServer gameServer = new GameServer(serverSocket);
        gameServer.start();
    }

}

