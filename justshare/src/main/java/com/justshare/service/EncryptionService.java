package com.justshare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String AES = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static final int KEY_LENGTH = 32;

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public EncryptionService(
            @Value("${app.encryption.key}") String base64Key
    ) {
        byte[] key = Base64.getDecoder().decode(base64Key);

        if (key.length != KEY_LENGTH) {
            throw new IllegalArgumentException(
                    "app.encryption.key must be a Base64-encoded 32-byte AES-256 key"
            );
        }

        this.secretKey = new SecretKeySpec(key, AES);
    }

    /*
     * Output format:
     *
     * [12-byte IV][encrypted data + GCM authentication tag]
     */
    public byte[] encrypt(byte[] plainData) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv)
            );

            byte[] encrypted = cipher.doFinal(plainData);

            ByteBuffer result =
                    ByteBuffer.allocate(iv.length + encrypted.length);

            result.put(iv);
            result.put(encrypted);

            return result.array();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not encrypt data",
                    e
            );
        }
    }

    public byte[] decrypt(byte[] encryptedData) {
        try {
            if (encryptedData == null ||
                    encryptedData.length <= IV_LENGTH) {

                throw new IllegalArgumentException(
                        "Invalid encrypted data"
                );
            }

            byte[] iv =
                    Arrays.copyOfRange(
                            encryptedData,
                            0,
                            IV_LENGTH
                    );

            byte[] encrypted =
                    Arrays.copyOfRange(
                            encryptedData,
                            IV_LENGTH,
                            encryptedData.length
                    );

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv)
            );

            return cipher.doFinal(encrypted);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not decrypt data",
                    e
            );
        }
    }
}
