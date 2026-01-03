package client.network;

import client.gui.ClientApp;
import common.models.messages.GameMessage;
import common.utils.JsonUtils;

import java.io.*;
import java.net.Socket;

public class Client {
    private final ClientApp clientApp;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String username; // Новое поле

    public Client(ClientApp clientApp, String host, int port, String username) throws IOException {
        this.clientApp = clientApp;
        this.username = username; // Сохраняем имя при подключении
        this.socket = new Socket(host, port);
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        startListening();
    }

    private void startListening() {
        new Thread(() -> {
            try {
                String json;
                while ((json = reader.readLine()) != null) {
                    GameMessage message = JsonUtils.parseMessage(json);
                    clientApp.handleMessage(message);
                }
            } catch (Exception e) {
                // Если ошибка произошла не из-за намеренного закрытия сокета
                if (socket != null && !socket.isClosed()) {
                    clientApp.onConnectionClosed();
                }
            } finally {
                close();
            }
        }).start();
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(GameMessage message) {
        try {
            String json = JsonUtils.toJson(message);
            writer.write(json);
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            clientApp.onConnectionError(e.getMessage());
        }
    }

    public void close() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}