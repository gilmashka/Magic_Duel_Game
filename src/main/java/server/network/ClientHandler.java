package server.network;

import common.models.messages.GameMessage;
import common.utils.JsonUtils;
import server.game.GameManager;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

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


    //задача потока: ожидание информации от клиента
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                String jsonFromClient = bufferedReader.readLine();

                if (jsonFromClient == null) {
                    System.out.println("Клиент " + clientId + " отключился (получен null)");
                    gameManager.disconnectProcessing(this);
                    break;
                }

                System.out.println("Получено от " + clientId + ": " + jsonFromClient);

                try {
                    GameMessage message = JsonUtils.parseMessage(jsonFromClient);
                    gameManager.handleMessage(this, message);
                } catch (Exception parseEx) {
                    System.err.println("Ошибка парсинга сообщения от " + clientId + ": " + parseEx.getMessage());
                }
            }
        } catch (java.net.SocketException e) {
            System.out.println("Соединение с клиентом " + clientId + " потеряно (SocketException)");
            gameManager.disconnectProcessing(this);
        } catch (Exception e) {
            System.out.println("Критическая ошибка в потоке клиента " + clientId + ": " + e.getMessage());
            e.printStackTrace();
            gameManager.disconnectProcessing(this);
        } finally {
            System.out.println("Завершение обработки для " + clientId);
            close();
        }
    }

    //метод закрытия полей класса читай отключения клиента от сервера
    public void close() {

        System.out.println("DEBUG: Метод close() вызван для " + clientId + " из потока: " + Thread.currentThread().getName());

        try {
            Thread.sleep(1000);

            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка при закрытии ClientHandler для " + clientId + ": " + e.getMessage());
        }
    }

    //метод отправки сообщения клиенту от сервера
    public void sendMessage(String json) {
        try {
            if (socket.isClosed()) return;

            bufferedWriter.write(json);
            bufferedWriter.newLine(); // важно для readLine() на клиенте
            bufferedWriter.flush();
            System.out.println("Отправлено клиенту " + clientId + ": " + json);
        } catch (IOException e) {
            System.out.println("Не удалось отправить сообщение клиенту " + clientId);
        }
    }


    public void stop() {
        close();
    }

    //геттер
    public String getClientId() {
        return clientId;
    }
}