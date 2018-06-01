package org.jetto.server.listener;

/**
 *
 * @author gorkemsari - jetto.org
 */
public interface ThreadListener {
    void onMessage(byte[] message, String id);
    void onError(String message, String id);
}