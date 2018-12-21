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

  /*Added - Creates an instance of the cache when UserEndpoint is initialized, which is only happening once -
  UserCache TO DO(9,3)*/
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
    // Converts the user to json in order to return it to the user
    String json = new Gson().toJson(user);
    //Added - Makes the user information encrypted - UserEndpoints TO DO (33,8)
    json = Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down? FIX
    /*Added - If it is the user that have send a "bad request", then a response code 400 should be send.
    If it instead is a different kind of error, such as a "Internal Server Error", a response code
    500 should be send - UserEndpoints TO DO(40,8)*/
    if (user != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not get users").build();
    }
  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    //Added - Instead of creating a connection to the DB i use my cache - UserCache TO DO(9,3)
    ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON FIX
    // Converts the users to json in order to return it to the user
    String json = new Gson().toJson(users);
    //Added - Makes the user information encrypted - UserEndpoints TO DO(62,8)
    json = Encryption.encryptDecryptXOR(json);

    if (users != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not get users").build();
    }
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
      //Added - If a user is created i force an update of my cache, so the new user is implemented - UserCache TO DO(9,3)
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

    //Added - Assigns a token to the user when the login is succesful - UserEndpoints TO DO(101,6)
    String token = UserController.login(loginUser);

    /*Added - Checks if the user has a token value (if a token is assigned above), if so the user is then logged in
    and a token is send - UserEndpoints TO DO(101,6)*/
    if (token != null) {
      //Added - I only return a token to make testing with a token possible
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(token).build();
    }
    else {
      return Response.status(400).entity("Unsuccesful login due to an incorrect email og password").build();
    }
  }

  // TODO: Make the system able to delete users FIX
  //Added - Class is added to make it possible to delete a user - UserEndpoints TO DO(122,6)
  @DELETE
  @Path("/delete/{idUser}/{token}")
  public Response deleteUser(@PathParam("idUser") int typedId, @PathParam("token") String typedToken) {

    //Added - Because i have made me system secured with tokens, this line initializes the "token" with the typed token
    DecodedJWT token = UserController.verifier(typedToken);

    //Added - Compares the typed token with the typed id
    if (token.getClaim("id").asInt() == typedId)
    {
      //Added - Creates a boolean, so we can check if the delete can be completed
      Boolean delete = UserController.delete(token.getClaim("id").asInt());

      if (delete)
      {
        //Added - We got to update our ArrayList because we have deleted a user - UserCache TO DO(9,3)
        userCache.getUsers(true);

        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Deleted the user with the id: " + typedId).build();
      }
      else
      {
        //Added - If the delete fails, a code 400 is returned with a response
        return Response.status(400).entity("Could not delete the user").build();
      }
    }
    else {
      //Added - If the token does not equals a id, a code 400 with an explanation is returned to the user
      return Response.status(400).entity("No user is found with the typed in id and token").build();
    }
  }

  // TODO: Make the system able to update users FIX
  //Added - Class is added to make it possible to update a user - UserEndpoints TO DO(156,6)
  @PUT
  @Path("/update/{idUser}/{token}")
  public Response updateUser(@PathParam("idUser") int typedId, @PathParam("token") String typedToken, String body) {

    //Added - Takes the writing update and converts it from Json
    User user = new Gson().fromJson(body, User.class);

    //Added - Because i have made me system secured with tokens, this line initializes the "token" with the typed token
    DecodedJWT token = UserController.verifier(typedToken);

    //Added - Compares the typed token with the typed id
    if (token.getClaim("id").asInt() == typedId)
    {
      //Added - Creates a boolean, so we can check if the update can be completed
      Boolean update = UserController.update(user, token.getClaim("id").asInt());

      if(update) {
      //Added - I got to update our ArrayList because we have updated a user - UserCache TO DO(9,3)
      userCache.getUsers(true);

      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Updated the user with the id: " + typedId).build();
    }
    else {
        return Response.status(400).entity("Could not update user").build();
      }
    }
    else {
      return Response.status(400).entity("No user is found with the typed in id and token").build();
    }
  }
}

