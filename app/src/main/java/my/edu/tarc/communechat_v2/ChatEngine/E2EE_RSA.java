package my.edu.tarc.communechat_v2.ChatEngine;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Key;

import javax.crypto.Cipher;

public class E2EE_RSA {
//TODO: merge with chat engine
    private KeyPair keyPair;
    private PublicKey pubKey;
    private PrivateKey privateKey;

    public E2EE_RSA() throws Exception{
        // generate public and private keys
        keyPair = buildKeyPair();
        pubKey = keyPair.getPublic(); //share dis so others can send to u
        privateKey = keyPair.getPrivate(); //kennot be shared, seeleos problem if shared
    }
    
    public E2EE_RSA(PublicKey pubKey, PrivateKey privateKey) throws Exception{
        // generate public and private keys
        keyPair = new KeyPair(pubKey, privateKey);
        this.pubKey = keyPair.getPublic(); //share dis so others can send to u
        this.privateKey = keyPair.getPrivate(); //kennot be shared, seeleos problem if shared
    }
    
    public E2EE_RSA(PublicKey pubKey){
        this.pubKey = pubKey; //received public key
    }
    
    public E2EE_RSA(PrivateKey privateKey){
        this.privateKey = privateKey;
    }
    
    
    private KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 512;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);      
        return keyPairGenerator.genKeyPair();
    }

    public byte[] encrypt(Key key, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.ENCRYPT_MODE, key);  

        return cipher.doFinal(message.getBytes());  
    }
    
    public byte[] decrypt(Key key, byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        return cipher.doFinal(encrypted);
    }
    
//    public static void main(String[] args) throws Exception {
//         //test function
//        E2EE_RSA rsa = new E2EE_RSA();
//        PublicKey publicKey = rsa.getPubKey(); //for key negotiation
//        PrivateKey privateKey = rsa.getPrivateKey(); //save in local for creating instance in future
//
//        // sign the message with another instance
//        E2EE_RSA rsa1 = new E2EE_RSA(publicKey);
//        byte [] signed;
//        signed = rsa1.encrypt(rsa1.getPubKey(), "六六六");
//        System.out.println(new String(signed));  // <<signed message>>
//
//        // verify the message using another instance
//        byte[] verified;
//        verified = rsa.decrypt(rsa.getPrivateKey(), signed);
//        System.out.println(new String(verified));     // This is a secret message
//    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}