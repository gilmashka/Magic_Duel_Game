package server.network;

import common.models.messages.GameMessage;
import common.utils.JsonUtils;
import server.game.GameManager;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{

    //поля аппаратной части
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private GameManager gameManager;

    //поля клиента
    private String clientId; //для связи клиент-handler

    //конструктор
    public ClientHandler(Socket socket, GameManager gameManager) throws IOException {
        this.socket = socket;
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.clientId = "client №: " + "Socket_port: " + socket.getPort() + "; " + "C_T: " + System.currentTimeMillis() + ";"; //ID клиента типа "SOCKET_PORT ; CURR_TIME: ;"
        this.gameManager = gameManager;
        System.out.println("Клиент " + clientId + " подключился"); // ***логирование***
    }

    /*
    методы ClientHandler
     */
    //задача потока: ожидание информации от клиента
    @Override
    public void run(){
        try {
            while (!socket.isClosed()){
                String jsonFromClient = bufferedReader.readLine();

                if(jsonFromClient == null){ //если сообщение пустое, то клиент отключился
                    System.out.println("Клиент " + clientId + " отключился");//***логирование***
                    gameManager.disconnectProcessing(this);
                    break;
                }
                System.out.println(jsonFromClient); // ***логирование***
                GameMessage message = JsonUtils.parseMessage(jsonFromClient);
                gameManager.handleMessage(this, message);
            }
        }catch (Exception e){ //ошибка на стороне клиента
            System.out.println("Ошибка у клиента " + clientId +  " типа: " + e.getMessage());
        }finally {
            close();
        }
    }

    //метод закрытия полей класса читай отключения клиента от сервера
    public void close(){
        try{
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //метод отправки сообщения клиенту от сервера
    public void sendMessage(String json){
        try {
            bufferedWriter.write(json);
            bufferedWriter.newLine(); // важно для readLine() на клиенте
            bufferedWriter.flush();
            System.out.println("Отправлено клиенту " + clientId + ": " + json);
        } catch (IOException e) {
            System.out.println("Не удалось отправить сообщение клиенту " + clientId);
        }
    }

    /*
    метод прнудительного разрыва соединения,
    должен выполняться тогда, когда сервер принудительно для клиента
    разрывает соединение (например, читерство клиента)
     */
    public void stop(){
        //в настоящий момент не используется, так как пока причин для принудительного отключения клиента нет
    }

    //геттер
    public String getClientId(){
        return clientId;
    }
}
