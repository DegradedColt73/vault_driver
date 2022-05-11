package crypt;

public interface CryptEngine {
    void generateKeyFromPassword(String password);
    void regenerateKeyWithPasswordAndIv(String password, String hexIv);
    String getIvAsHex();
    String encrypt(String plainText);
    String decrypt(String cipherText);
    String getKey();
}
