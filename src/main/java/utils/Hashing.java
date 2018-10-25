package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.util.encoders.Hex;

public final class Hashing {

  //Tilføjet
  private String salt;
  private static MessageDigest hashing;

  /*Tilføjet
  public Hashing(){
    this.salt = "1234";
  }*/
  // TODO: You should add a salt and make this secure
  public static String md5(String rawString) {
    try {

      // We load the hashing algoritm we wish to use.
      MessageDigest md = MessageDigest.getInstance("MD5");

      // We convert to byte array
      byte[] byteArray = md.digest(rawString.getBytes());

      // Initialize a string buffer
      StringBuffer sb = new StringBuffer();

      // Run through byteArray one element at a time and append the value to our stringBuffer
      for (int i = 0; i < byteArray.length; ++i) {
        sb.append(Integer.toHexString((byteArray[i] & 0xFF) | 0x100).substring(1, 3));
      }
      /*Tilføjet
      return Hashing.performHashing(sb.toString());*/

      //Convert back to a single string and return

      return sb.toString();

    } catch (java.security.NoSuchAlgorithmException e) {

      //If somethings breaks
      System.out.println("Could not hash string");
    }

    return null;
  }

  // TODO: You should add a salt and make this secure
  public static String sha(String rawString) {
    try {
      // We load the hashing algoritm we wish to use.
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      // We convert to byte array
      byte[] hash = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));

      // We create the hashed string
      String sha256hex = new String(Hex.encode(hash));

      // And return the string
      return sha256hex;

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    /*String hashedPassword = rawString + this.salt;
    return hash(hashedPassword);*/

    /*Tilføjet
    return Hashing.performHashing(rawString);*/

    return rawString;
  }

  //Tilføjet
  private static String performHashing(String str){
    hashing.update(str.getBytes());
    byte[] hash = hashing.digest();
    StringBuilder hexString = new StringBuilder();
    for (byte aHash : hash) {
      if ((0xff & aHash) < 0x10) {
        hexString.append("0" + Integer.toHexString((0xFF & aHash)));
      } else {
        hexString.append(Integer.toHexString(0xFF & aHash));
      }
    }
    return hexString.toString();
  }
}