package com.example.chat.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {
    private static final String AES = "AES";
    private static final String SECRET_KEY_STRING = "0123456789abcdef0123456789abcdef"; // 32-char (256-bit) key

    private final SecretKey secretKey;

    public EncryptionService() {
        this.secretKey = new SecretKeySpec(SECRET_KEY_STRING.getBytes(), AES);
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }
}

