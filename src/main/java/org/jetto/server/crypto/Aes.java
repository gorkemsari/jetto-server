package org.jetto.server.crypto;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author gorkemsari - jetto.org
 */
public class Aes {

    private final Charset charSet = Charset.forName("ISO-8859-1");
    private final String instance = "AES";
    private final String cipherInstance = "AES/CBC/PKCS5Padding";
    private final String hash = "SHA-256";

    public byte[] encrypt(String clean, String key) throws Exception {

        System.out.println("enc key:" + key);

        // Generating IV.
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Hashing key.
        MessageDigest digest = MessageDigest.getInstance(hash);
        digest.update(key.getBytes(charSet));
        byte[] keyBytes = new byte[16];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, instance);

        // Encrypt.
        byte[] data = clean.getBytes(charSet);
        Cipher cipher = Cipher.getInstance(cipherInstance);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(data);

        // Combine IV and encrypted part.
        byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
        System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
        System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);

        return encryptedIVAndText;
    }

    public String decrypt(byte[] encryptedIvTextBytes, String key) throws Exception {

        System.out.println("dec key:" + key);

        int ivSize = 16;
        int keySize = 16;

        // Extract IV.
        byte[] iv = new byte[ivSize];
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted part.
        int encryptedSize = encryptedIvTextBytes.length - ivSize;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

        // Hash key.
        byte[] keyBytes = new byte[keySize];
        MessageDigest md = MessageDigest.getInstance(hash);
        md.update(key.getBytes());
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, instance);

        // Decrypt.
        Cipher cipherDecrypt = Cipher.getInstance(cipherInstance);
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);
        String decryptedText = new String(decrypted, charSet);

        return decryptedText;
    }
}