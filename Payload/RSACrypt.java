import java.net.*;
import java.io.*;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

import java.security.*;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.spec.X509EncodedKeySpec;




public class RSACrypt {
  private PublicKey public_key;
  private SecretKey AESKey;
  private String AESString;
  private Cipher AES_cipher;
  private Cipher RSA_cipher;

  public RSACrypt(PublicKey public_key) {
    gen();
    this.public_key = public_key;
    try {
      RSA_cipher = Cipher.getInstance("RSA");
      AES_cipher = Cipher.getInstance("AES");
    } catch (Exception e) {
      System.out.println(e);
    }

  }

  public void gen() { // generate 256 bit AES key
    try {
      SecureRandom random = new SecureRandom();
      KeyGenerator generator = KeyGenerator.getInstance("AES");
      generator.init(256, random);
      AESKey = generator.generateKey();

      AESString = KeyToString(AESKey);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public String KeyToString(SecretKey key) {
    byte[] data = public_key.getEncoded();
    return Base64.getEncoder().encodeToString(data);
  }
  public PublicKey StringToPubKey(String keyString) {
    try {
      byte[] pub = Base64.getDecoder().decode(keyString);
      X509EncodedKeySpec spec = new X509EncodedKeySpec(pub);
      KeyFactory factory = KeyFactory.getInstance("RSA");
      PublicKey public_key = factory.generatePublic(spec);
      return public_key;
    } catch (Exception e) {
      return null;
    }

  }

  public String getPubKeyString() { // return the encoded key
    return AESString;
  }

  public void setPubKey(String keyString) { // set the key with a string
    public_key = StringToPubKey(keyString);
  }

  public void encrypt(File f) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("Encrypting file: " + f.getName());
		AES_cipher.init(Cipher.ENCRYPT_MODE, AESKey);
		writeToFile(f);
	}

  public void decrypt(File f) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("Decrypting file: " + f.getName());
		AES_cipher.init(Cipher.DECRYPT_MODE, AESKey);
		writeToFile(f);
	}

  public void SaveAESKey() {
    try {
      RSA_cipher.init(Cipher.ENCRYPT_MODE, public_key);
      byte[] output = RSA_cipher.doFinal(AESKey.getEncoded());

      File f = new File("Key_protected.key");
      if (!f.exists()) {
          f.createNewFile();
      }
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(output);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public void writeToFile(File f) throws IOException, IllegalBlockSizeException, BadPaddingException {
		FileInputStream in = new FileInputStream(f);
		byte[] input = new byte[(int) f.length()];
		in.read(input);

		FileOutputStream out = new FileOutputStream(f);
		byte[] output = AES_cipher.doFinal(input);
		out.write(output);

		out.flush();
		out.close();
		in.close();
	}

  public static void main(String[] args) {
    ;
  }

}
