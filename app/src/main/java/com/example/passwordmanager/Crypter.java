package com.example.passwordmanager;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypter {

    private static Crypter instance;
    private Cipher cipher;
    Context context;

    private Crypter(Context context) throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("AES");
        this.context = context;
    }

    public static synchronized Crypter getInstance(Context context) throws NoSuchAlgorithmException, NoSuchPaddingException {
        if (instance == null) {
            instance = new Crypter(context);
        }
        return instance;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generateKey(String masterPass, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {

        PBEKeySpec spec = new PBEKeySpec(masterPass.toCharArray(), salt, 1000, 256);
        SecretKey secretKey = SecretKeyFactory.getInstance("PBKDF2withHmacSHA1").generateSecret(spec);
        String key;
        key = android.util.Base64.encodeToString(secretKey.getEncoded(), android.util.Base64.DEFAULT);
        return key;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encrypt(String data) throws GeneralSecurityException, IOException {

        String secretKey = SharedPrefManager.getInstance(context).getSecretKey();
        String base64EncryptedData = "";
        //Decode de base64 string
        byte[] encodedKey = android.util.Base64.decode(secretKey, android.util.Base64.DEFAULT);
        //Rebuild the key
        SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "PBKDF2withHmacSHA1");

        try {

            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            base64EncryptedData = android.util.Base64.encodeToString(encryptedData, android.util.Base64.DEFAULT);

            Log.d("user",base64EncryptedData);

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return base64EncryptedData;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decrypt(String data) throws GeneralSecurityException, IOException {

        String secretKey = SharedPrefManager.getInstance(context).getSecretKey();
        String strDecryptedData = "";
        //Decode de base64 string
        byte[] encodedKey = android.util.Base64.decode(secretKey, android.util.Base64.DEFAULT);

        //Rebuild the key
        SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "PBKDF2withHmacSHA1");
        Log.d("key", String.valueOf(originalKey));

        try {
            cipher.init(Cipher.DECRYPT_MODE, originalKey);

            byte[] decryptedData = android.util.Base64.decode(data, android.util.Base64.DEFAULT);
            byte[] utf8 = cipher.doFinal(decryptedData);

            strDecryptedData = new String(utf8, StandardCharsets.UTF_8);
            Log.d("user", strDecryptedData);

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return strDecryptedData;

    }
}
