package my.edu.tarc.communechat_v2.model;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA{
	//TODO: merge with chat engine
	private static final String ALGORITHM = "RSA";
	public static final int RSA_CONSTRUCT_WITH_PUBLIC = 101;
	public static final int RSA_CONSTRUCT_WITH_PRIVATE = 102;
	private byte[] pubKey;
	private byte[] privateKey;

	public RSA() {
		// generate new public and private keys, only when key expired/ new device
		KeyPair keyPair = buildKeyPair();
		this.pubKey = keyPair.getPublic().getEncoded(); //share dis so others can send to u
		this.privateKey = keyPair.getPrivate().getEncoded(); //kennot be shared, seeleos problem if shared
	}

	public RSA(String key, int constructWith) {
		byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
		switch (constructWith) {
			case RSA_CONSTRUCT_WITH_PUBLIC:
				this.pubKey = keyBytes;
				break;
			case RSA_CONSTRUCT_WITH_PRIVATE:
				this.privateKey = keyBytes;
				break;
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

			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
			keyPairGenerator.initialize(keySize);

			keyPair = keyPairGenerator.genKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyPair;
	}


	public byte[] encrypt(byte[] text) { //byte[] str.getBytes() to convert from String to byte array

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

	public String encryptKey(String key) {

		//getting an instance of "RSA" will never result in an exception
		//the try/catch block is just to deny the exception warning
		byte[] encrypted = null;
		try {
			PublicKey pubKey = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(this.pubKey));
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);

			encrypted = cipher.doFinal(Base64.decode(key, Base64.DEFAULT));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Base64.encodeToString(encrypted, Base64.DEFAULT);
	}

	public byte[] decrypt(byte[] cipherText) { //new String(byte[]) for converting to String

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

	public String decryptKey(String key) {

		//getting an instance of "RSA" will never result in an exception
		//the try/catch block is just to deny the exception warning
		byte[] encrypted = null;
		try {
			PrivateKey privateKey = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(this.privateKey));
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			encrypted = cipher.doFinal(Base64.decode(key, Base64.DEFAULT));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Base64.encodeToString(encrypted, Base64.DEFAULT);
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

	//        public static void main(String[] args) throws Exception {
//            //testing integration with AES
//            RSA rsa1 = new RSA(); //user 2 generate key pair
//
//            RSA rsa = new RSA(rsa1.getPubKey(), RSA_CONSTRUCT_WITH_PUBLIC); //user 1 get pubkey from user2
//            AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard(); //user 1 generate aes key
//            byte[] message = aes.encrypt(new String("666666").getBytes()); //encrypt message with aes key
//            String secret = aes.getKey(); //chat room's aes key
//            String encryptedSecret = rsa.encryptKey(secret); //encrypt aes key with user2's rsa
//            //String encryptedSecretString = Base64.encode(encryptedSecret); //converted to String for MQTT message
//            //-----another device
//            //byte[] secret1 = rsa1.decrypt(encryptedSecret); //decrypt aes key with user2's private key
//            //byte[] encryptedSecretBytes = Base64.decode(encryptedSecretString);
//            String secret1 = rsa1.decryptKey(encryptedSecret); //decrypt aes key with user2's private key
//            AdvancedEncryptionStandard aes1 = new AdvancedEncryptionStandard(secret1); //instantiate aes decryptor
//            System.out.println(new String(aes1.decrypt(message)));
//        }
	public String getPubKey() {
		return Base64.encodeToString(pubKey, Base64.DEFAULT);
	}

	public String getPrivateKey() {
		return Base64.encodeToString(privateKey, Base64.DEFAULT);
	}
}
