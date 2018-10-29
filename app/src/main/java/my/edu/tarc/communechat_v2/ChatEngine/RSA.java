package my.edu.tarc.communechat_v2.ChatEngine;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import my.edu.tarc.communechat_v2.ADT.CryptoDecryptInterface;
import my.edu.tarc.communechat_v2.ADT.CryptoEncryptInterface;

public class RSA implements CryptoEncryptInterface, CryptoDecryptInterface {
	//TODO: merge with chat engine
	private static final String ALGORITHM = "RSA";
	public static final int RSA_CONSTRUCT_WITH_PUBLIC = 101;
	public static final int RSA_CONSTRUCT_WITH_PRIVATE = 102;
	private KeyPair keyPair;
	private byte[] pubKey;
	private byte[] privateKey;

	public RSA(){
		// generate new public and private keys, only when key expired/ new device
		this.keyPair = buildKeyPair();
		this.pubKey = keyPair.getPublic().getEncoded(); //share dis so others can send to u
		this.privateKey = keyPair.getPrivate().getEncoded(); //kennot be shared, seeleos problem if shared
	}

	public RSA(byte[] key, int constructWith){
		switch(constructWith){
			case RSA_CONSTRUCT_WITH_PUBLIC:
				this.pubKey = key;break;
			case RSA_CONSTRUCT_WITH_PRIVATE:
				this.privateKey=key;break;
			default:
				System.out.println("Invalid parameter!");
		}
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
			PublicKey pubKey = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(this.pubKey));
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
			PrivateKey privateKey = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(this.privateKey));
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
//        RSA rsa = new RSA();
//        byte[] publicKey = rsa.getPubKey(); //for key negotiation
//        byte[] privateKey = rsa.getPrivateKey(); //save in local for creating instance in future
//
//        // sign the message with another instance
//        RSA rsa1 = new RSA(publicKey, RSA_CONSTRUCT_WITH_PUBLIC);
//        byte [] signed;
//        signed = rsa1.encrypt(new String("六六六").getBytes());
//        System.out.println(new String(signed));  // <<signed message>>
//
//        // verify the message using another instance
//        byte[] verified;
//        verified = rsa.decrypt(signed);
//        System.out.println(new String(verified));     // This is a secret message
//    }

	//    public static void main(String[] args) throws Exception {
//        //testing integration with AES
//        RSA rsa1 = new RSA();
//        RSA rsa = new RSA(rsa1.getPubKey(), RSA_CONSTRUCT_WITH_PUBLIC);
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

	public byte[] getPubKey() {
		return pubKey;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}
}