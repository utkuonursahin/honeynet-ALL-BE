package me.utku.emailhoneypot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;


@Service
@Slf4j
public class KeyService {
    private static byte[] getHashedKey(){
        try{
            String secretKey = "Bu2h94tvCDO5fmNnm82gs2pcQXaSYWJ2";
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            return sha.digest(keyBytes);
        }catch (Exception e) {
            log.error("KeyService getHashedKey exception: {}", e.getMessage());
            return null;
        }
    }
    public static String encrypt(String plainText){
        try{
            SecretKeySpec secretKeySpec = new SecretKeySpec(getHashedKey(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }catch (Exception e) {
            log.error("KeyService encrypt exception: {}", e.getMessage());
            return null;
        }
    }

    public static String decrypt(String encryptedText) {
        try{
            SecretKeySpec secretKeySpec = new SecretKeySpec(getHashedKey(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        }catch (Exception e){
            log.error("KeyService decrypt exception: {}", e.getMessage());
            return null;
        }
    }

}
