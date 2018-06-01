package org.jetto.server.listener;

/**
 *
 * @author gorkemsari - jetto.org
 */
public interface ServerListener {
    void onMessage(String message, String id);
    void onStart(String id);
    void onStop(String id);
    void onError(String message, String id);
}