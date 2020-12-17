package com.example.passwordmanager;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypter {


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encrypt(String data, String secretKey){

        String strEncryptedData = "";
        //Decode de base64 string
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        //Rebuild the key
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "PBKDF2WithHmacSHA1");

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);

            byte[] encryptedPass = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            strEncryptedData = Base64.getEncoder().encodeToString(encryptedPass);

            Log.d("user",strEncryptedData);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return strEncryptedData;
    }
}
