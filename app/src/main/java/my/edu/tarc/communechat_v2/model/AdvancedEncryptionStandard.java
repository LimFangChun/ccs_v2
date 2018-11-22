package my.edu.tarc.communechat_v2.model;

import android.util.Base64;
import android.util.Log;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AdvancedEncryptionStandard{
	private byte[] key;

	private static final String ALGORITHM = "AES";

	public AdvancedEncryptionStandard(String key) {
		byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
		this.key = keyBytes;
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
	public String encrypt(String text) //byte[] str.getBytes() to convert from String to byte array
	{
		//getting an instance of "AES" will never result in an exception
		//the try/catch block is just to deny the exception warning
		byte[] encrypted = null;
		byte[] textBytes = text.getBytes();
		try {
			SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			encrypted = cipher.doFinal(textBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Base64.encodeToString(encrypted, Base64.DEFAULT);
	}

	/**
	 * Decrypts the given byte array
	 *
	 * @param cipherText The data to decrypt
	 */
	public String decrypt(String cipherText) //remember to cast to String using new String(byte[]) for messages
	{
		//getting an instance of "AES" will never result in an exception
		//the try/catch block is just to deny the exception warning
		byte[] decrypted = null;
		byte[] encrypted = Base64.decode(cipherText, Base64.DEFAULT);
		try{
			SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			decrypted = cipher.doFinal(encrypted);
		}catch (Exception e){
			e.printStackTrace();
		}
		return new String(decrypted);
	}

	public String getKey() {
		return Base64.encodeToString(key, Base64.DEFAULT);
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
