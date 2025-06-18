package com.aim.utils;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;

public class EncryptDecryptUtils {

	final static Logger logger = Logger.getLogger(EncryptDecryptUtils.class);
	
	private static final String PASS_PHRASE = "KWH360ThisIs#KWH360This_IsWebsOptiThis#IsKWH360";
	private static final byte[] SALT_BYTE = "WEBS_KWH/OPTI_360".getBytes();
	private static final String UNICODE_FORMAT = "UTF-8";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final int iterations = 15000;
	private static final String SECRET_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";
	
    private static Cipher cipher;
    private static SecretKeySpec serkey; 
    private static final byte[] IV = { 0, 10, 20, 80, 50, 30, 90, 70, 80, 50, 60, 80, 04, 05, 03, 05 };
    private static final IvParameterSpec IVSPEC = new IvParameterSpec(IV);
    
    static { 
    	try {
        	SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_FACTORY_ALGORITHM);
        	SecretKey tmp = factory.generateSecret(new PBEKeySpec(PASS_PHRASE.toCharArray(), SALT_BYTE, iterations, 128));
        	serkey = new SecretKeySpec(tmp.getEncoded(), "AES");
	        cipher = Cipher.getInstance(TRANSFORMATION);
		} catch (Exception e) {
			//e.printStackTrace();
		}
    }


    /**
     *  This is a encrypt method which is used to encrypt string
     * 
     * @param unencryptedString
     * @return
     */
    public static String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, serkey, IVSPEC);
            byte[] encryptedText = cipher.doFinal(unencryptedString.getBytes(UNICODE_FORMAT));
            encryptedString = Base64.encodeBase64URLSafeString(encryptedText);
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
        return encryptedString;
    }


    /**
     * This is a decrypt method which is used to decrypt string
     * 
     * @param encryptedString
     * @return
     */
    public static String decrypt(String encryptedString) {
        String decryptedText=null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, serkey, IVSPEC);
            byte[] encryptedText = Base64.decodeBase64(encryptedString.getBytes());
            decryptedText = new String(cipher.doFinal(encryptedText));
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
        return decryptedText;
    }
    	
}
