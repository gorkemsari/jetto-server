package org.jetto.server.endpoint;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.jetto.server.common.Common;
import org.jetto.server.common.Enums;
import org.jetto.server.crypto.Aes;
import org.jetto.server.crypto.DiffieHellman;
import org.jetto.server.listener.ServerListener;
import org.jetto.server.listener.ThreadListener;
import org.jetto.server.model.MessageModel;
import org.jetto.server.model.PoolModel;
import org.jetto.server.model.RegisterModel;
import org.jetto.server.parser.Parser;
import org.jetto.server.worker.Reader;
import org.jetto.server.worker.Writer;

/**
 *
 * @author gorkemsari - jetto.org
 */
public class ServerEndpoint implements ThreadListener{
    private ServerListener serverListener;
    private final DiffieHellman dh;
    private final Aes aes;
    private final Parser parser;
    private final int port;
    private boolean running;
    private final HashMap<String, PoolModel> map;

    public ServerEndpoint(int port){
        this.port = port;
        this.dh = new DiffieHellman();
        this.aes = new Aes();
        this.parser = new Parser();
        this.map = new HashMap<>();
    }

    public void setServerListener(ServerListener serverListener){
        this.serverListener = serverListener;
    }

    public void start(){
        running = true;
        new ServerThread().start();
    }

    public void stop(){
        running = false;
        this.serverListener = null;
        map.clear();
        serverListener.onStop("");
    }

    private void forward(MessageModel messageModel){
        String idF = "";
        try {
            for (String id : messageModel.getTo()) {
                PoolModel poolModel = map.get(id);
                byte[] encryptedData = aes.encrypt(parser.toJson(messageModel), poolModel.getAesKey());
                poolModel.getWriter().write(encryptedData);
            }
        }  catch (Exception e) {
            serverListener.onError(e.getMessage(), idF);
        }
    }

    public void write(String message, String id){
        try {
            MessageModel messageModel = new MessageModel();
            messageModel.setFrom(Enums.Type.SERVER.toString());
            messageModel.setMessage(message);

            PoolModel poolModel = map.get(id);
            byte[] encryptedData = aes.encrypt(parser.toJson(messageModel), poolModel.getAesKey());
            poolModel.getWriter().write(encryptedData);
        }  catch (Exception e) {
            serverListener.onError(e.getMessage(), id);
        }
    }

    public void write(String message, final List<String> id){
        String id_c = "";
        try {
            MessageModel messageModel = new MessageModel();
            messageModel.setMessage(message);

            for(String clientId : id) {
                id_c = clientId;
                PoolModel poolModel = map.get(clientId);
                byte[] encryptedData = aes.encrypt(parser.toJson(messageModel), poolModel.getAesKey());
                poolModel.getWriter().write(encryptedData);
            }
        }  catch (Exception e) {
            serverListener.onError(e.getMessage(), id_c);
        }
    }

    public void write(String message){
        String id = "";
        try {
            MessageModel messageModel = new MessageModel();
            messageModel.setMessage(message);

            for(HashMap.Entry<String, PoolModel> entry : map.entrySet()) {
                id = entry.getKey();
                PoolModel poolModel = entry.getValue();
                byte[] encryptedData = aes.encrypt(parser.toJson(messageModel), poolModel.getAesKey());
                poolModel.getWriter().write(encryptedData);
            }
        }  catch (Exception e) {
            serverListener.onError(e.getMessage(), id);
        }
    }

    @Override
    public void onMessage(byte[] message, String id) {
        try{
            PoolModel poolModel = map.get(id);
            MessageModel messageModel = parser.toModel(aes.decrypt(message, poolModel.getAesKey()), MessageModel.class);

            if(messageModel.getType() == Enums.Type.SERVER.Value){
                int subType = parser.getType(messageModel.getMessage());
                if (subType == Enums.SubType.REGISTER.Value) {
                    RegisterModel registerModel = parser.toModel(messageModel.getMessage(), RegisterModel.class);
                    int clientPublicKey = registerModel.getPublicKey();
                    int commonKey = dh.getNewCommonKey(clientPublicKey, poolModel.getPrivateKey());
                    poolModel.setAesKey(id + "-" + commonKey);
                    map.put(id, poolModel);
                } else {
                    serverListener.onMessage(messageModel.getMessage(), messageModel.getFrom());
                }
            }
            else if(messageModel.getType() == Enums.Type.FORWARD.Value){
                forward(messageModel);
            }
        } catch (Exception e) {
            serverListener.onError(e.getMessage(), id);
        }
    }

    @Override
    public void onError(String message, String id) {
        serverListener.onError(message, id);
        serverListener.onStop(id);
    }

    private class ServerThread extends Thread implements Runnable{
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (running) {
                    Socket socket = serverSocket.accept();

                    String id = UUID.randomUUID().toString();
                    int privateKey = dh.getNewPrivateKey();
                    int publicKey = dh.getNewPublicKey(privateKey);

                    Reader reader = new Reader(socket, ServerEndpoint.this);
                    reader.setId(id);
                    Writer writer = new Writer(socket, ServerEndpoint.this);
                    writer.setId(id);

                    PoolModel poolModel = new PoolModel();
                    poolModel.setWriter(writer);
                    poolModel.setPrivateKey(privateKey);
                    poolModel.setAesKey(Common.DEFAULT_KEY);
                    map.put(id, poolModel);

                    RegisterModel registerModel = new RegisterModel();
                    registerModel.setPublicKey(publicKey);
                    registerModel.setId(id);
                    registerModel.setType(Enums.SubType.REGISTER.Value);

                    write(parser.toJson(registerModel), id);
                    serverListener.onStart(id);
                }
            } catch (IOException e) {
                serverListener.onError(e.getMessage(), "");
            }
        }
    }
}