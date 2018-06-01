package org.jetto.server.crypto;

import java.util.Random;

/**
 *
 * @author gorkemsari - jetto.org
 */
public class DiffieHellman {
    private final int root = 3;//g
    private final int mod = 97;//p

    public int getNewPrivateKey() {
        Random rand = new Random();
        int privateKey = rand.nextInt(999);
        return privateKey;
    }

    public int getNewPublicKey(int privateKey){
        int publicKey = calculate(root, privateKey, mod);
        return publicKey;
    }

    public int getNewCommonKey(int root, int pow) {
        int commonKey = calculate(root, pow, mod);
        return commonKey;
    }

    private int calculate(int root, int pow, int mod){
        int result = 1;
        for (int i = 1; i <= pow; i++) {
            result = (result * root) % mod;
        }
        return result;
    }
}