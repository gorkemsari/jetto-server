package org.jetto.server.worker;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import org.jetto.server.listener.ThreadListener;

/**
 *
 * @author gorkemsari - jetto.org
 */
public class Writer implements Runnable {

    private boolean isRunning;
    private final Socket socket;
    private final ThreadListener threadListener;
    private DataOutputStream stream;
    private final LinkedBlockingQueue<byte[]> queue;
    private String id;

    public Writer(Socket socket, ThreadListener threadListener) {
        this.isRunning = true;
        this.socket = socket;
        this.queue = new LinkedBlockingQueue<>();
        this.threadListener = threadListener;
        start();
    }

    @Override
    public void run() {
        try {
            stream = new DataOutputStream(socket.getOutputStream());

            while (isRunning) {
                sleep();
                if (!queue.isEmpty()) {
                    byte[] data = queue.poll();
                    stream.writeLong(data.length);
                    stream.write(data);
                    stream.flush();
                }
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

    public void write(byte[] message) {
        queue.add(message);
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