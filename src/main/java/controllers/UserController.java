package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import cache.UserCache;
import model.User;
import utils.Hashing;
import utils.Log;

//Added - To make tokens possible - UserEndpoints TO DO(99,6)
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

public class UserController {

  private static DatabaseController dbCon;

  public UserController() {
    dbCon = new DatabaseController();
  }

  public static User getUser(int id) {

    // Check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build the query for DB
    String sql = "SELECT * FROM user where id=" + id;

    // Actually do the query
    ResultSet rs = dbCon.query(sql);
    User user = null;

    try {
      // Get first object, since we only have one
      if (rs.next()) {
        user =
            new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("created_at"));

        // Return the create object
        return user;
      } else {
        System.out.println("No user found");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return null
    return user;
  }

  /**
   * Get all users in database
   *
   * @return
   */
  public static ArrayList<User> getUsers() {

    // Check for DB connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build SQL
    String sql = "SELECT * FROM user";

    // Do the query and initialyze an empty list for use if we don't get results
    ResultSet rs = dbCon.query(sql);
    ArrayList<User> users = new ArrayList<User>();

    try {
      // Loop through DB Data
      while (rs.next()) {
        User user =
            new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("created_at"));

        // Add element to list
        users.add(user);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return the list of users
    return users;
  }

  public static User createUser(User user) {

    // Write in log that we've reach this step
    Log.writeLog(UserController.class.getName(), user, "Actually creating a user in DB", 0);

    // Set creation time for user.
    user.setCreatedTime(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Insert the user in the DB
    // TODO: Hash the user password before saving it. FIX
    int userID = dbCon.insert(
        "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
            + user.getFirstname()
            + "', '"
            + user.getLastname()
            + "', '"
            //Added - Hashing the users password before saving it when a user is created - UserController TO DO(124,6)
            + Hashing.shaSalt(user.getPassword())
            + "', '"
            + user.getEmail()
            + "', "
            + user.getCreatedTime()
            + ")");

    if (userID != 0) {
      //Update the userid of the user before returning
      user.setId(userID);
    } else{
      // Return null if user has not been inserted into database
      return null;
    }

    return user;
  }

  //Added - Deleting a user in the database - UserEndpoints TO DO(120,6)
  public static boolean delete(int id)
  {
    // Write in log that we've reach this step
    Log.writeLog(UserController.class.getName(), id, "Deleting a user in DB", 0);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    //Added - Initializing the users information to the information we get from the users id in the path
    User user = UserController.getUser(id);

    if (user != null)
    {
      dbCon.deleteUpdate("DELETE FROM user WHERE id =" + id);

      return true;

    } else {
      return false;
    }
  }

  //Added - Updating af user in the database - UserEndpoints TO DO(154,6)
  public static boolean update(User user ,int id)
  {
    // Write in log that we've reach this step
    Log.writeLog(UserController.class.getName(), id, "Updating a user in DB", 0);

    // Check for DB Connection
    if (dbCon == null)
    {
      dbCon = new DatabaseController();
    }

    //Added - Checks if the user has been initialized, and updates if so
    if (user != null)
    {
      dbCon.deleteUpdate("UPDATE user SET first_name ='" + user.getFirstname() +
      "', last_name ='" + user.getLastname() +
      "', email ='" + user.getEmail() +
      "', password ='" + Hashing.shaSalt(user.getPassword()) +
      "'where id=" + id);

      return true;

    } else {
      return false;
    }
  }

  public static String login(User loginUser)
  {

    Log.writeLog(UserController.class.getName(), loginUser, "Logging in a user", 0);

    // Check for DB Connection
    if (dbCon == null)
    {
      dbCon = new DatabaseController();
    }

    UserCache userCache = new UserCache();

    ArrayList<User> users = userCache.getUsers(false);

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    for (User user : users)
    {
      if (user.getEmail().equals(loginUser.getEmail()) && user.getPassword().equals(Hashing.shaSalt(loginUser.getPassword())))
      {

        try
        {
          //Added - Implementing JWT token - UserEndpoints TO DO(99,6)
          Algorithm algorithm = Algorithm.HMAC256("token");

          //Added - Makes sure that a new token is created every time a user is logged in - UserEndpoints TO DO(99,6)
          String token = JWT.create().withIssuer("auth0").withClaim("id", user.getId()).withClaim("timestamp", timestamp).sign(algorithm);

          //Added - Sets the token to the user object - UserEndpoints TO DO(99,6)
          user.setToken(token);

          return token;

        } catch (JWTCreationException e)
        {
          //Invalid Signing configuration / Couldn't convert Claims.
          e.getMessage();
        }
      }
    }

    return null;
  }

  //Added - Makes it possible to verify a user through a token - UserEndpoints TO DO(99,6)
  public static DecodedJWT verifier(String user) {

    Log.writeLog(UserController.class.getName(), user, "Verifying a token", 0);

    String token = user;

    try {
      //Added - Implementing JWT token
      Algorithm algorithm = Algorithm.HMAC256("token");
      //Added - Verifying my token
      JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build();
      DecodedJWT jwt = verifier.verify(token);

      return jwt;
    }

    catch (JWTVerificationException e) {
      //Invalid claims
      e.getMessage();
    }

    return null;
  }

}