package org.jetto.server.model;

import org.jetto.server.worker.Writer;

/**
 *
 * @author gorkemsari - jetto.org
 */
public class PoolModel {

    private int privateKey;
    private String aesKey;
    private Writer writer;

    /**
     * @return the privateKey
     */
    public int getPrivateKey() {
        return privateKey;
    }

    /**
     * @param privateKey the privateKey to set
     */
    public void setPrivateKey(int privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * @return the aesKey
     */
    public String getAesKey() {
        return aesKey;
    }

    /**
     * @param aesKey the aesKey to set
     */
    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    /**
     * @return the writer
     */
    public Writer getWriter() {
        return writer;
    }

    /**
     * @param writer the writer to set
     */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }
}