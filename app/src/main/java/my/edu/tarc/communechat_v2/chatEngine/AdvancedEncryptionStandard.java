package my.edu.tarc.communechat_v2.chatEngine;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import my.edu.tarc.communechat_v2.ADT.CryptoDecryptInterface;
import my.edu.tarc.communechat_v2.ADT.CryptoEncryptInterface;

public class AdvancedEncryptionStandard implements CryptoEncryptInterface, CryptoDecryptInterface {
	private byte[] key;

	private static final String ALGORITHM = "AES";

	public AdvancedEncryptionStandard(byte[] key)
	{
		this.key = key;
	}

	public AdvancedEncryptionStandard()
	{
		//getting an instance of "AES" will never result in an exception
		//the try/catch block is just to deny the exception warning
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
			keyGen.init(128); // for example
			SecretKey secretKey = keyGen.generateKey();
			SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
			this.key = secret.getEncoded();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Encrypts the given plain text
	 *
	 * @param text The plain text to encrypt
	 */
	public byte[] encrypt(byte[] text) //byte[] str.getBytes() to convert from String to byte array
	{
		//getting an instance of "AES" will never result in an exception
		//the try/catch block is just to deny the exception warning
		byte[] encrypted = null;
		try {
			SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			encrypted = cipher.doFinal(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}

	/**
	 * Decrypts the given byte array
	 *
	 * @param cipherText The data to decrypt
	 */
	public byte[] decrypt(byte[] cipherText) //remember to cast to String using new String(byte[]) for messages
	{
		//getting an instance of "AES" will never result in an exception
		//the try/catch block is just to deny the exception warning
		byte[] decrypted = null;
		try{
			SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			decrypted = cipher.doFinal(cipherText);
		}catch (Exception e){
			e.printStackTrace();
		}
		return decrypted;
	}

	public byte[] getKey() {
		return key;
	}
//    public static void main(String[] args) throws Exception {
//		//test function
//        AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard();
//        String txt = "Hello warudo!";
//        System.out.println(txt);
//        byte[] encrypted = aes.encrypt(txt.getBytes());
//        System.out.println(new String(encrypted));
//        byte[] decrypted = aes.decrypt(encrypted);
//        System.out.println(new String(decrypted));
//    }
}
