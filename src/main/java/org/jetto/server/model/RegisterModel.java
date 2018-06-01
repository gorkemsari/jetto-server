package org.jetto.server.model;

/**
 *
 * @author gorkemsari - jetto.org
 */
public class RegisterModel extends Model{

    private int publicKey;
    private String id;

    /**
     * @return the publicKey
     */
    public int getPublicKey() {
        return publicKey;
    }

    /**
     * @param publicKey the publicKey to set
     */
    public void setPublicKey(int publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the uniqueId to set
     */
    public void setId(String id) {
        this.id = id;
    }
}