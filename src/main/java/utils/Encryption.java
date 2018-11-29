package utils;

public final class Encryption {

  public static String encryptDecryptXOR(String rawString) {

    // If encryption is enabled in Config.
    if (Config.getEncryption()) {

      // The key is predefined and hidden in code
      // TODO: Create a more complex code and store it somewhere better FIX
      //char[] key = {'C', 'B', 'S'};
      //Tilføjet for gøre encryption mere kompleks - TO DO(11,10)
      char[] key = Config.getEncryptionKey();

      // Stringbuilder enables you to play around with strings and make useful stuff
      StringBuilder thisIsEncrypted = new StringBuilder();

      // TODO: This is where the magic of XOR is happening. Are you able to explain what is going on?
      /* For every value that is going to be encrypted, this for-loop iterates through what is typed in, and adds
      the binary value of my encryption_key which is declared as "key" in linje 14. To this we use the class
      StringBuilder, which the declared in line 17 to be the instance "thisIsEncrypted". */
      for (int i = 0; i < rawString.length(); i++) {
        thisIsEncrypted.append((char) (rawString.charAt(i) ^ key[i % key.length]));
      }

      // We return the encrypted string
      return thisIsEncrypted.toString();

    } else {
      // We return without having done anything
      return rawString;
    }
  }
}
