package my.edu.tarc.communechat_v2.ChatEngine;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AdvancedEncryptionStandard {
	private byte[] key;

	private static final String ALGORITHM = "AES";

	public AdvancedEncryptionStandard(byte[] key)
	{
		this.key = key;
	}

	public AdvancedEncryptionStandard() throws Exception
	{
		KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
		keyGen.init(128); // for example
		SecretKey secretKey = keyGen.generateKey();
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(),ALGORITHM);
		this.key = secret.getEncoded();
	}

	/**
	 * Encrypts the given plain text
	 *
	 * @param plainText The plain text to encrypt
	 */
	public byte[] encrypt(byte[] plainText) throws Exception
	{
		SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		return cipher.doFinal(plainText);
	}

	/**
	 * Decrypts the given byte array
	 *
	 * @param cipherText The data to decrypt
	 */
	public byte[] decrypt(byte[] cipherText) throws Exception //remember to cast to String using new String(byte[]) for messages
	{
		SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);

		return cipher.doFinal(cipherText);
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
