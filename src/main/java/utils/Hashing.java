package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.util.encoders.Hex;

public final class Hashing {

  //Tilføjet
  private String salt;
  private static MessageDigest hashing;

  // TODO: You should add a salt and make this secure FIX
  //Tilføjet for at tilføje salt til kodeordet i md5
  public static String md5Salt (String str) {
    String salt = "dsf89sd8897dsf78ds";
    String hashedPassword = str + salt;
    return md5(hashedPassword);
  }

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

      //Convert back to a single string and return
      return sb.toString();

    } catch (java.security.NoSuchAlgorithmException e) {

      //If somethings breaks
      System.out.println("Could not hash string");
    }

    return null;
  }

  // TODO: You should add a salt and make this secure FIX
  //Added - To add salt to the password in sha
  public static String shaSalt (String str) {
    String salt = "dsf89sd8897dsf78ds";
    String hashedPassword = str + salt;
    return sha(hashedPassword);
  }

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
}