package com.cbsexam;

import cache.UserCache;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Log;

@Path("user")
public class UserEndpoints {

  //Tilføjet - Opretter en instans af cachen når UserEndpoints bliver initialiseret, hvilket den kun bliver en gang
  private static UserCache userCache = new UserCache();

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON FIX
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);
    //Tilføjet
    json = Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down?
    return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON FIX
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);
    //Tilføjet
    json = Encryption.encryptDecryptXOR(json);

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      userCache.getUsers(true);
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system. FIX
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {

    User loginUser = new Gson().fromJson(body, User.class);

    String token = UserController.login(loginUser);

    if (token != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("The user is logged in\n" + token).build();
    }
    else {
      return Response.status(400).entity("Unsuccesful login due to an incorrect email og password").build();
    }
  }

  // TODO: Make the system able to delete users FIX
  @DELETE
  @Path("/delete/{idUser}")
  public Response deleteUser(@PathParam("idUser") int id, String body) {

    DecodedJWT token = UserController.verifier(body);

    Boolean delete = UserController.delete(token.getClaim("test").asInt());

    //Added
    if(delete) {
      //Added - We got to update our ArrayList because we have deleted a user
      userCache.getUsers(true);

      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Deleted the user with the id: " + id).build();
    }

      return Response.status(400).entity("Unsuccesful delete possible due to an incorrect id or  token").build();

  }

  // TODO: Make the system able to update users FIX
  @POST
  @Path("/update/{idUser}")
  public Response updateUser(@PathParam("idUser") int id, String body) {

    /*DecodedJWT token = UserController.verifier(body);

    Boolean delete = UserController.delete(token.getClaim("test").asInt());*/

    //Added - Takes the writin update and converts it from Json
    User user = new Gson().fromJson(body, User.class);

    Boolean update = UserController.update(user, id);

    //Added - We got to update our ArrayList because we have updated a user
    userCache.getUsers(true);

    if(update) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Updated the user with the id: " + id).build();
    } else
    {
      // Return a response with status 200 and JSON as type
      return Response.status(400).entity("The user was not found").build();
    }
  }
}
