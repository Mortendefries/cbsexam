package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Address;
import model.LineItem;
import model.Order;
import model.User;
import utils.Log;

public class OrderController {

  private static DatabaseController dbCon;

  public OrderController() {
    dbCon = new DatabaseController();
  }

  public static Order getOrder(int id)
  {

    // Check for connection
    if (dbCon == null)
    {
      dbCon = new DatabaseController();
    }

    // Build SQL string to query
    String sql = "SELECT *, billing.street_address as billing, shipping.street_address as shipping\n " +
            "FROM orders\n " +
            "JOIN user on orders.user_id = user.id\n " +
            "LEFT JOIN address as billing\n " +
            "ON orders.billing_address_id = billing.id\n " +
            "LEFT JOIN address as shipping\n " +
            "ON orders.shipping_address_id = shipping.id\n " +
            "WHERE orders.id " + id;

    // Do the query in the database and create an empty object for the results
    ResultSet rs = dbCon.query(sql);
    Order order = null;

    try
    {
      if (rs.next())
      {
        ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));

        User user = new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("created_at")
        );

        Address billingAddress = new Address(
                rs.getInt("billing_address_id"),
                rs.getString("name"),
                rs.getString("billing"),
                rs.getString("city"),
                rs.getString("zipcode")
        );

        Address shippingAddress = new Address(
                rs.getInt("shipping_address_id"),
                rs.getString("name"),
                rs.getString("shipping"),
                rs.getString("city"),
                rs.getString("zipcode")
        );

        // Create an objeect instance of order from tbe DB data
        order = new Order(
                rs.getInt("id"),
                user,
                lineItems,
                billingAddress,
                shippingAddress,
                rs.getFloat("order_total"),
                rs.getLong("created_at"),
                rs.getLong("updated_at")
        );

        // Returns the build order
        return order;
      } else
      {
        System.out.println("No order found");
      }
    } catch (SQLException e)
    {
      System.out.println(e.getMessage());
    }

    return order;
  }

    /*// Build SQL string to query
    String sql = "SELECT * FROM orders where id=" + id;

    // Do the query in the database and create an empty object for the results
    ResultSet rs = dbCon.query(sql);
    Order order = null;

    try {
      if (rs.next()) {

        // Perhaps we could optimize things a bit here and get rid of nested queries.
        User user = UserController.getUser(rs.getInt("user_id"));
        ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));
        Address billingAddress = AddressController.getAddress(rs.getInt("billing_address_id"));
        Address shippingAddress = AddressController.getAddress(rs.getInt("shipping_address_id"));

        // Create an object instance of order from the database data
        order =
            new Order(
                rs.getInt("id"),
                user,
                lineItems,
                billingAddress,
                shippingAddress,
                rs.getFloat("order_total"),
                rs.getLong("created_at"),
                rs.getLong("updated_at"));

        // Returns the build order
        return order;
      } else {
        System.out.println("No order found");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Returns null
    return order;*/

  /**
   * Get all orders in database
   *
   * @return
   */
  public static ArrayList<Order> getOrders() {

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build SQL string to query
    String sql = "SELECT *, billing.street_address as billing, shipping.street_address as shipping\n " +
            "FROM orders\n " +
            "JOIN user on orders.user_id = user.id\n " +
            "LEFT JOIN address as billing\n " +
            "ON orders.billing_address_id = billing.id\n " +
            "LEFT JOIN address as shipping\n " +
            "ON orders.shipping_address_id = shipping.id\n ";

    // Do the query in the database and create an empty object for the results
    ResultSet rs = dbCon.query(sql);
    ArrayList<Order> orders =new ArrayList<>();

    try
    {
      while (rs.next())
      {
        ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));

        User user = new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("created_at")
        );

        Address billingAddress = new Address(
                rs.getInt("billing_address_id"),
                rs.getString("name"),
                rs.getString("billing"),
                rs.getString("city"),
                rs.getString("zipcode")
        );

        Address shippingAddress = new Address(
                rs.getInt("shipping_address_id"),
                rs.getString("name"),
                rs.getString("shipping"),
                rs.getString("city"),
                rs.getString("zipcode")
        );

        // Create an objeect instance of order from tbe DB data
        Order order = new Order(
                rs.getInt("id"),
                user,
                lineItems,
                billingAddress,
                shippingAddress,
                rs.getFloat("order_total"),
                rs.getLong("created_at"),
                rs.getLong("updated_at")
        );

        // Adds an order to our ArrayList
        orders.add(order);
      }
    } catch (SQLException e)
    {
      System.out.println(e.getMessage());
    }

    return orders;

    /*//Added - Changed the name from "order" to "orders" to make it the same as in the DB
    String sql = "SELECT * FROM orders";

    ResultSet rs = dbCon.query(sql);
    ArrayList<Order> orders = new ArrayList<Order>();

    try {
      while(rs.next()) {

        // Perhaps we could optimize things a bit here and get rid of nested queries.
        User user = UserController.getUser(rs.getInt("user_id"));
        ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));
        Address billingAddress = AddressController.getAddress(rs.getInt("billing_address_id"));
        Address shippingAddress = AddressController.getAddress(rs.getInt("shipping_address_id"));

        // Create an order from the database data
        Order order =
            new Order(
                rs.getInt("id"),
                user,
                lineItems,
                billingAddress,
                shippingAddress,
                rs.getFloat("order_total"),
                rs.getLong("created_at"),
                rs.getLong("updated_at"));

        // Add order to our list
        orders.add(order);

      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // return the orders
    return orders;*/
  }

  public static Order createOrder(Order order) {

    // Write in log that we've reach this step
    Log.writeLog(OrderController.class.getName(), order, "Actually creating an order in DB", 0);

    // Set creation and updated time for order.
    order.setCreatedAt(System.currentTimeMillis() / 1000L);
    order.setUpdatedAt(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // TODO: Enable transactions in order for us to not save the order if somethings fails for some of the other inserts. FIX

    //Added - To create a connection to the DB
    Connection connection = DatabaseController.getConnection();

    try
    {
      //Added - Makes sure that updates does'nt gets committed automatically
      connection.setAutoCommit(false);

      // Save addresses to database and save them back to initial order instance
      order.setBillingAddress(AddressController.createAddress(order.getBillingAddress()));
      order.setShippingAddress(AddressController.createAddress(order.getShippingAddress()));

      // Save the user to the database and save them back to initial order instance
      order.setCustomer(UserController.createUser(order.getCustomer()));

      // Insert the product in the DB
      int orderID = dbCon.insert(
              "INSERT INTO orders(user_id, billing_address_id, shipping_address_id, order_total, created_at, updated_at) VALUES("
                      + order.getCustomer().getId()
                      + ", "
                      + order.getBillingAddress().getId()
                      + ", "
                      + order.getShippingAddress().getId()
                      + ", "
                      + order.calculateOrderTotal()
                      + ", "
                      + order.getCreatedAt()
                      + ", "
                      + order.getUpdatedAt()
                      + ")");

      if (orderID != 0)
      {
        //Update the productid of the product before returning
        order.setId(orderID);
      }

      // Create an empty list in order to go trough items and then save them back with ID
      ArrayList<LineItem> items = new ArrayList<LineItem>();

      // Save line items to database
      for (LineItem item : order.getLineItems())
      {
        item = LineItemController.createLineItem(item, order.getId());
        items.add(item);
      }

      order.setLineItems(items);

      //Added - Commits the transaction
      connection.commit();

    }
    catch (SQLException e1) {
      try {
        //Added - If there is an error in the transaction, this makes sure that it is cancelled
        connection.rollback();

        System.out.println("Rollback");

        //Added - If a rollback isn't possible, i will use this catch to print a message about it
      } catch (SQLException e2) {
        System.out.println("No rollback" + e2.getMessage());
        //Added - A finally block is added to make sure that the autocommit is changed back to true
      } finally
      {
        try
        {
          connection.setAutoCommit(true);
        } catch (SQLException e3) {
          e3.printStackTrace();
        }
      }
    }

    // Return order
    return order;
  }
}