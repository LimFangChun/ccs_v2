package my.edu.tarc.communechat_v2.ChatEngine;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import my.edu.tarc.communechat_v2.ADT.CryptoDecryptInterface;
import my.edu.tarc.communechat_v2.ADT.CryptoEncryptInterface;

public class RSA implements CryptoEncryptInterface, CryptoDecryptInterface {
	//TODO: merge with chat engine
	private static final String ALGORITHM = "AES";
	private KeyPair keyPair;
	private PublicKey pubKey;
	private PrivateKey privateKey;

	public RSA() throws Exception {
		// generate new public and private keys, only when key expired/ new device
		keyPair = buildKeyPair();
		pubKey = keyPair.getPublic(); //share dis so others can send to u
		privateKey = keyPair.getPrivate(); //kennot be shared, seeleos problem if shared
	}

	public RSA(PublicKey pubKey) {
		//instance for encryption of key(AES in this project) only
		//key is to be get from others
		this.pubKey = pubKey; //received public key
	}

	public RSA(PrivateKey privateKey) {
		//instance for decrypting message sent to this device
		this.privateKey = privateKey;
	}


	private KeyPair buildKeyPair() {
		//getting an instance of "RSA" will never result in an exception
		//the try/catch block is just to deny the exception warning
		KeyPair keyPair = null;
		try {
			final int keySize = 1024;

			KeyPairGenerator keyPairGenerator= KeyPairGenerator.getInstance(ALGORITHM);
			keyPairGenerator.initialize(keySize);

			keyPair = keyPairGenerator.genKeyPair();
		}catch (Exception e){
			e.printStackTrace();
		}
		return keyPair;
	}


	public byte[] encrypt(byte[] text){ //byte[] str.getBytes() to convert from String to byte array

		//getting an instance of "RSA" will never result in an exception
		//the try/catch block is just to deny the exception warning
		byte[] encrypted = null;
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);

			encrypted = cipher.doFinal(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}

	public byte[] decrypt(byte[] cipherText){ //new String(byte[]) for converting to String

		//getting an instance of "RSA" will never result in an exception
		//the try/catch block is just to deny the exception warning
		byte[] decrypted = null;
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			decrypted = cipher.doFinal(cipherText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypted;
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