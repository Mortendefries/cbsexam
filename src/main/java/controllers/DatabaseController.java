package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import utils.Config;

public class DatabaseController {

  private static Connection connection;

  public DatabaseController() {
    connection = getConnection();
  }

  /**
   * Get database connection
   *
   * @return a Connection object
   */
  public static Connection getConnection() {
    /*Added - Due to an 8 hour timer at my SQL server, i have added this to renew the connection if it has timed out.
    The ideal solution would have been to closing my connection after each statement in my controllers, but i
    could not make it work due to time pressure*/
    if (connection != null) {
      return connection;
    }

    try {
      // Set the database connect with the data from the config
      String url =
          "jdbc:mysql://"
              + Config.getDatabaseHost()
              + ":"
              + Config.getDatabasePort()
              + "/"
              + Config.getDatabaseName()
              + "?serverTimezone=CET";

      String user = Config.getDatabaseUsername();
      String password = Config.getDatabasePassword();

      // Register the driver in order to use it
      DriverManager.registerDriver(new com.mysql.jdbc.Driver());

      // Create a connection to the database
      connection = DriverManager.getConnection(url, user, password);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return connection;
  }

  /**
   * Do a query in the database
   *
   * @return a ResultSet or Null if Empty
   */
  public ResultSet query(String sql) {

    // Check if we have a connection
    if (connection == null)
      connection = getConnection();

    // We set the resultset as empty
    ResultSet rs = null;

    try {
      // Build the statement as a prepared statement
      PreparedStatement stmt = connection.prepareStatement(sql);

      // Actually fire the query to the DB
      rs = stmt.executeQuery();

      // Return the results
      return rs;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    // Return the resultset which at this point will be null
    return rs;
  }

  public int insert(String sql) {

    // Set key to 0 as a start
    int result = 0;

    // Check that we have connection
    if (connection == null)
      connection = getConnection();

    try {
      // Build the statement up in a safe way
      PreparedStatement statement =
          connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

      // Execute query
      result = statement.executeUpdate();

      // Get our key back in order to update the user
      ResultSet generatedKeys = statement.getGeneratedKeys();
      if (generatedKeys.next()) {
        return generatedKeys.getInt(1);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    // Return the resultset which at this point will be null
    return result;
  }

  //Added - Makes it possible to delete and update from the database - UserEndpoints TO DO(122,6) and TO DO(156,6)
  public boolean deleteUpdate(String sqlStatement)
  {
    // Check if we have a connection
    if (connection == null)
      connection = getConnection();

    try{
      PreparedStatement deleteUpdate = connection.prepareStatement(sqlStatement);

      deleteUpdate.executeUpdate();

      return true;
    }
    catch (SQLException e){
      e.printStackTrace();

      return false;
    }
  }
}
