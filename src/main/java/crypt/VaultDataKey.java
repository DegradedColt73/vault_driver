package crypt;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class VaultDataKey {
    public final int BLOCK_SIZE = 16;
    private byte [] iv;
    private byte [] key;

    public VaultDataKey(String key) throws NoSuchAlgorithmException {
        this.generateIV();
        this.key = CryptUtils.hexStringToByteArray(key);
    }

    public VaultDataKey(String iv, String key){
        this.key = CryptUtils.hexStringToByteArray(key);
        this.iv = CryptUtils.hexStringToByteArray(iv);
    }

    private void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // for example
        SecretKey secretKey = keyGen.generateKey();
        this.key = secretKey.getEncoded();
    }

    public String encrypt(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        IvParameterSpec iv = new IvParameterSpec(this.iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        IvParameterSpec iv = new IvParameterSpec(this.iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

    private void generateIV(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[this.BLOCK_SIZE];
        secureRandom.nextBytes(iv);
        this.iv = iv;
    }

    public String getKeyAsString(){
        return CryptUtils.byteArrayToHexString(this.key);
    }

    public String getIvAsString(){
        return CryptUtils.byteArrayToHexString(this.iv);
    }
}
