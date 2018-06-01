package org.jetto.server.worker;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import org.jetto.server.listener.ThreadListener;

/**
 *
 * @author gorkemsari - jetto.org
 */
public class Reader implements Runnable {

    private boolean isRunning;
    private final Socket socket;
    private final ThreadListener threadListener;
    private DataInputStream stream;
    private String id;

    public Reader(Socket socket, ThreadListener threadListener) {
        this.isRunning = true;
        this.socket = socket;
        this.threadListener = threadListener;
        start();
    }

    @Override
    public void run() {
        try {
            int bytesRead;
            byte[] data;
            stream = new DataInputStream(socket.getInputStream());

            while (isRunning) {
                sleep();
                long size = stream.readLong();
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                while (size > 0 && (bytesRead = stream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                    output.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                }
                data = output.toByteArray();
                threadListener.onMessage(data, getId());
            }
        } catch (IOException e) {
            close();
            threadListener.onError(e.getMessage(), getId());
        }
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void close() {
        try {
            isRunning = false;
            stream.close();
        } catch (IOException e) {
            threadListener.onError(e.getMessage(), getId());
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void start() {
        Thread t = new Thread(this);
        t.start();
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            threadListener.onError(e.getMessage(), getId());
        }
    }
}