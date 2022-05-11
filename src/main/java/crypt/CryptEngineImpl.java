package crypt;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class CryptEngineImpl implements CryptEngine{
    private final int ITERATIONS = 1000;
    private final int BLOCK_SIZE = 16;
    private final int KEY_LENGTH = 64 * 4;
    private char [] password;
    private byte [] iv;
    private byte [] mainKey;

    public CryptEngineImpl(){
        this.password = null;
        this.iv = null;
        this.mainKey = null;
    }

    public CryptEngineImpl(String password){
        this.password = password.toCharArray();
        this.generateIV();
        this.generateKey();
    }

    public CryptEngineImpl(String password, String hexedIv){
        this.password = password.toCharArray();
        this.iv = CryptUtils.hexStringToByteArray(hexedIv);
        this.generateKey();
    }

    @Override
    public void generateKeyFromPassword(String password){
        this.savePasswordAsCharArray(password);
        this.generateIV();
        this.generateKey();
    }

    @Override
    public void regenerateKeyWithPasswordAndIv(String password, String hexIv){
        this.savePasswordAsCharArray(password);
        this.saveHexIvAsByteArray(hexIv);
        this.generateKey();
    }

    @Override
    public String getIvAsHex() {
        return CryptUtils.byteArrayToHexString(this.iv);
    }

    @Override
    public String encrypt(String plainText){
        try {
            IvParameterSpec iv = new IvParameterSpec(this.iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(this.mainKey, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String decrypt(String cipherText){
        try {
            IvParameterSpec iv = new IvParameterSpec(this.iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(this.mainKey, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(plainText);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void savePasswordAsCharArray(String password){
        this.password = password.toCharArray();
    }

    private void saveHexIvAsByteArray(String hexIv){
        this.iv = CryptUtils.hexStringToByteArray(hexIv);
    }

    private void generateIV(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[this.BLOCK_SIZE];
        secureRandom.nextBytes(iv);
        this.iv = iv;
    }

    private void generateKey(){
        PBEKeySpec pbeKeySpec = new PBEKeySpec(this.password, this.iv, this.ITERATIONS, this.KEY_LENGTH);
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            this.mainKey = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public String getKey(){
        return CryptUtils.byteArrayToHexString(this.mainKey);
    }
}
