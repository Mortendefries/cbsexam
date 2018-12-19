package model;

import utils.Hashing;

public class User {

  public int id;
  public String firstname;
  public String lastname;
  public String email;
  private String password;
  private long createdTime;
  //Added - Creates the attribute token - UserEndpoints TO DO(99,6)
  private String token;

  public User(int id, String firstname, String lastname, String password, String email, Long createdTime) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    this.password = password;
    this.email = email;
    this.createdTime = createdTime;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(long createdTime) {
    this.createdTime = createdTime;
  }

  //Added - get method added to the attribute token - UserEndpoints TO DO(99,6)
  public String getToken() {
    return token;
  }

  //Added - set method added to the attribute token - UserEndpoints TO DO(99,6)
  public void setToken(String token) {
    this.token = token;
  }
}
