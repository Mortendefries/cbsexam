package utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Config {

  private static String DATABASE_HOST;
  private static int DATABASE_PORT;
  private static String DATABASE_USERNAME;
  private static String DATABASE_PASSWORD;
  private static String DATABASE_NAME;
  private static boolean ENCRYPTION;
  private static String SOLR_HOST;
  private static int SOLR_PORT;
  private static String SOLR_PATH;
  private static String SOLR_CORE;
  private static long PRODUCT_TTL;
  //Added - To make UserCache possible - UserCache TO DO(9,3)
  private static long USER_TTL;
  //Added - To make OrderCache possible - OrderCache TO DO(9,3)
  private static long ORDER_TTL;
  //Added - Makes the encryption more complex - Encryption TO DO(11,10)
  private static String ENCRYPTION_KEY;

  public static long getProductTtl() {
    return PRODUCT_TTL;
  }

  //Added - To make UserCache possible - UserCache TO DO(9,3)
  public static long getUserTtl(){
    return USER_TTL;
  }

  //Added - To make OrderCache possible - OrderCache TO DO(9,3)
  public static long getOrderTtl(){
    return ORDER_TTL;
  }

  public static String getDatabaseHost() {
    return DATABASE_HOST;
  }

  //Added - Makes the encryption more complex - Encryption TO DO(11,10)
  public static char[] getEncryptionKey() {
    return ENCRYPTION_KEY.toCharArray();
  }

  public static int getDatabasePort() {
    return DATABASE_PORT;
  }

  public static String getDatabaseUsername() {
    return DATABASE_USERNAME;
  }

  public static String getDatabasePassword() {
    return DATABASE_PASSWORD;
  }

  public static String getDatabaseName() {
    return DATABASE_NAME;
  }

  public static Boolean getEncryption() {
    return ENCRYPTION;
  }

  public static String getSolrHost() {
    return SOLR_HOST;
  }

  public static int getSolrPort() {
    return SOLR_PORT;
  }

  public static String getSolrPath() {
    return SOLR_PATH;
  }

  public static String getSolrCore() {
    return SOLR_CORE;
  }

  public static void initializeConfig() throws IOException {

    // Init variables to parse JSON
    JsonObject json;
    JsonParser parser = new JsonParser();

    // Read File and store input
    InputStream input = Config.class.getResourceAsStream("/config.json");
    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

    // Go through the lines one by one
    StringBuffer stringBuffer = new StringBuffer();
    String str;

    // Read file one line at a time
    while ((str = reader.readLine()) != null) {
      stringBuffer.append(str);
    }

    // Konverterer json til variabler ved at typecaste til JsonObject
    json = (JsonObject) parser.parse(stringBuffer.toString());

    // Hiv teksten ud og sæt klassens variable til senere brug
    DATABASE_HOST = json.get("DATABASE_HOST").toString().replace("\"", "");
    DATABASE_PORT = Integer.parseInt(json.get("DATABASE_PORT").toString().replace("\"", ""));
    DATABASE_USERNAME = json.get("DATABASE_USERNAME").toString().replace("\"", "");
    DATABASE_PASSWORD = json.get("DATABASE_PASSWORD").toString().replace("\"", "");
    DATABASE_NAME = json.get("DATABASE_NAME").toString().replace("\"", "");
    ENCRYPTION = json.get("ENCRYPTION").getAsBoolean();
    SOLR_HOST = json.get("SOLR_HOST").toString().replace("\"", "");
    SOLR_PORT = Integer.parseInt(json.get("SOLR_PORT").toString().replace("\"", ""));
    SOLR_PATH = json.get("SOLR_PATH").toString().replace("\"", "");
    SOLR_CORE = json.get("SOLR_CORE").toString().replace("\"", "");
    PRODUCT_TTL = json.get("PRODUCT_TTL").getAsLong();
    //Added - To make OrderCache possible - UserCache TO DO(9,3)
    USER_TTL = json.get("USER_TTL").getAsLong();
    //Added - To make OrderCache possible - OrderCache TO DO(9,3)
    ORDER_TTL = json.get("ORDER_TTL").getAsLong();
    //Added - Makes the encryption more complex - Encryption TO DO(11,10)
    ENCRYPTION_KEY = json.get("ENCRYPTION_KEY").getAsString();
  }
}