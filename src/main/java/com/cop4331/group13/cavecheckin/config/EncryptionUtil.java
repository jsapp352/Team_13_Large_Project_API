package com.cop4331.group13.cavecheckin.config;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;

public class EncryptionUtil {
	private Cipher cipher;
	private SecretKey secretKey;

	public EncryptionUtil()	{
		String keyGenString = System.getenv("AES_SECRET");

		byte[] encryptionKeyBytes = keyGenString.getBytes();

		try
		{
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			secretKey = new SecretKeySpec(encryptionKeyBytes, "AES");
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	public String decrypt(String encryptedMessage) {
		return decrypt(DatatypeConverter.parseHexBinary(encryptedMessage));
	}

	public String decrypt(byte[] encryptedMessage) {
		String message;

		try
		{
			message = new String(decryptMessage(encryptedMessage));
		}
		catch (Exception e)
		{
			System.out.println(e);
			message = null;
		}

		return message;
	}

	public String encrypt(String message) {
		byte[] encryptedMessage;

		try
		{
			encryptedMessage = encryptMessage(message.getBytes());
		}
		catch (Exception e)
		{
			System.out.println(e);
			encryptedMessage = null;
		}

		return DatatypeConverter.printHexBinary(encryptedMessage);
	}

	public byte[] encryptMessage(byte[] message) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException	{
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(message);
	}

	public byte[] decryptMessage(byte[] encryptedMessage) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(encryptedMessage);
	}
}
