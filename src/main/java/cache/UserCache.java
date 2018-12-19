package cache;

import controllers.UserController;
import model.User;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it. FIX
//Added - Whole class is added to make caching of the users possible - UserCache TO DO(9,3)
public class UserCache {

    private ArrayList<User> users;

    //Time cache should live
    private long ttl;

    // Sets when the cache has been created
    private long created;

    public UserCache(){
        this.ttl = Config.getUserTtl();
    }

    public ArrayList<User> getUsers (Boolean forceUpdate){

        /*If we wish to clear cache, we can set force update.
        Otherwise we look at the age of the cache and figure out if we should update.
        If the list is empty we also check for new products*/
        if (forceUpdate
                || ((this.created + this.ttl) <= (System.currentTimeMillis() / 1000L))
                //Added - Instead of .isEmpty() i check if the list is == null
                || this.users == null) {


            //Get users from controller, since we wish to update
            ArrayList<User> users = UserController.getUsers();

            //Sets users for the instance and set created timestamp
            this.users = users;
            this.created = System.currentTimeMillis() / 1000L;
        }

        //Return the documents
        return this.users;
    }

}
