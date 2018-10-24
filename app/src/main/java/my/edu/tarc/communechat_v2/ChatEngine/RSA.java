package my.edu.tarc.communechat_v2.ChatEngine;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Key;

import javax.crypto.Cipher;

public class RSA {
//TODO: merge with chat engine
    private KeyPair keyPair;
    private PublicKey pubKey;
    private PrivateKey privateKey;

    public RSA() throws Exception{
        // generate public and private keys
        keyPair = buildKeyPair();
        pubKey = keyPair.getPublic(); //share dis so others can send to u
        privateKey = keyPair.getPrivate(); //kennot be shared, seeleos problem if shared
    }
    
    public RSA(PublicKey pubKey){
        this.pubKey = pubKey; //received public key
    }
    
    public RSA(PrivateKey privateKey){
        this.privateKey = privateKey;
    }
    
    
    private KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 1024;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);      
        return keyPairGenerator.genKeyPair();
    }


    public byte[] encrypt(byte[] message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        return cipher.doFinal(message);
    }

    public byte[] decrypt(byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(encrypted);
    }
    
//    public static void main(String[] args) throws Exception {
//         //test function
//        RSA rsa = new E2EE_RSA();
//        PublicKey publicKey = rsa.getPubKey(); //for key negotiation
//        PrivateKey privateKey = rsa.getPrivateKey(); //save in local for creating instance in future
//
//        // sign the message with another instance
//        RSA rsa1 = new RSA(publicKey);
//        byte [] signed;
//        signed = rsa1.encrypt(rsa1.getPubKey(), "六六六");
//        System.out.println(new String(signed));  // <<signed message>>
//
//        // verify the message using another instance
//        byte[] verified;
//        verified = rsa.decrypt(rsa.getPrivateKey(), signed);
//        System.out.println(new String(verified));     // This is a secret message
//    }

//  public static void main(String[] args) throws Exception {
//        //testing integration with AES
//        RSA rsa1 = new E2EE_RSA();
//        RSA rsa = new E2EE_RSA(rsa1.getPubKey());
//        AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard();
//        byte[] message = aes.encrypt(new String("666666").getBytes());
//        byte[] secret = aes.getKey(); //get aes key
//        byte[] encryptedSecret = rsa.encrypt(secret); //encrypt aes key with rsa
//        String encryptedSecretString = new String(encryptedSecret); //converted to String for MQTT message
//        //-----another device
//        byte[] secret1 = rsa1.decrypt(encryptedSecret);
//        AdvancedEncryptionStandard aes1 = new AdvancedEncryptionStandard(secret1);
//        System.out.println(new String(aes1.decrypt(message)));
//    }
    public PublicKey getPubKey() {
        return pubKey;
    }
}