package com.keepassdroid.sync.utilities;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.security.SecureRandom;
import java.util.Base64;
public class SecureSession {

    private byte[] encryptionKey;
    private byte[] decryptionKey;
    private byte[] kim;
    private static SecureSession instance;

    static {
        // Add Bouncy Castle as a security provider
        Security.addProvider(new BouncyCastleProvider());
    }

    private SecureSession(byte[] kim) {
        this.kim = kim;

        // Derive encryption and decryption keys using Bouncy Castle HKDF
        this.encryptionKey = deriveKey(kim, "ANDROID_TO_WINDOWS".getBytes(StandardCharsets.UTF_8), 32);
        this.decryptionKey = deriveKey(kim, "WINDOWS_TO_ANDROID".getBytes(StandardCharsets.UTF_8), 32);
    }

    public static void initializeSession(BigInteger kim) {
        SecureSession session = new SecureSession(kim.toByteArray());
        instance = session;
        System.out.println("====> Session Initialized");
    }

    public static SecureSession getInstance() {
        if (instance == null) {
            throw new IllegalStateException("The session is null! Check if the session has been initialized");
        }
        return instance;
    }

    public CipherTextMessage encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Generate random IV
        byte[] iv = new byte[12]; // GCM recommended IV size is 12 bytes
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(16 * 8, iv); // 16 bytes = 128 bits authentication tag
        SecretKeySpec secretKey = new SecretKeySpec(encryptionKey, "AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

        byte[] plaintextBytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] cipherTextWithTag = cipher.doFinal(plaintextBytes);

        // Extract MAC tag (last 16 bytes of the output)
        int macTagLength = 16; // 128 bits
        int cipherTextLength = cipherTextWithTag.length - macTagLength;

        byte[] cipherTextBytes = new byte[cipherTextLength];
        byte[] macTagBytes = new byte[macTagLength];

        System.arraycopy(cipherTextWithTag, 0, cipherTextBytes, 0, cipherTextLength);
        System.arraycopy(cipherTextWithTag, cipherTextLength, macTagBytes, 0, macTagLength);

        return new CipherTextMessage(
                Base64.getEncoder().encodeToString(cipherTextBytes),
                Base64.getEncoder().encodeToString(iv),
                Base64.getEncoder().encodeToString(macTagBytes)
        );
    }

    public String decrypt(CipherTextMessage cipherMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        byte[] cipherTextBytes = Base64.getDecoder().decode(cipherMessage.getCipherText());
        byte[] ivBytes = Base64.getDecoder().decode(cipherMessage.getIV());
        byte[] tagBytes = Base64.getDecoder().decode(cipherMessage.getMac());

        byte[] combinedCiphertext = new byte[cipherTextBytes.length + tagBytes.length];
        System.arraycopy(cipherTextBytes, 0, combinedCiphertext, 0, cipherTextBytes.length);
        System.arraycopy(tagBytes, 0, combinedCiphertext, cipherTextBytes.length, tagBytes.length);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, ivBytes); // tag length and iv
        SecretKeySpec secretKey = new SecretKeySpec(decryptionKey, "AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

        byte[] plaintextBytes = cipher.doFinal(combinedCiphertext);

        return new String(plaintextBytes, StandardCharsets.UTF_8);
    }

    private static byte[] deriveKey(byte[] inputKeyMaterial, byte[] info, int keyLength) {
        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(new SHA256Digest());
        hkdf.init(new HKDFParameters(inputKeyMaterial, null, info));

        byte[] derivedKey = new byte[keyLength];
        hkdf.generateBytes(derivedKey, 0, keyLength);
        return derivedKey;
    }
}

