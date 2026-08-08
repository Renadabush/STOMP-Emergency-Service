package bgu.spl.net.srv;

import java.util.concurrent.ConcurrentHashMap;

public class usersDataBase {
    private ConcurrentHashMap<String,String> users;
    private usersDataBase(){
        users=new ConcurrentHashMap<>();
    }
    private static class SingletonHelper {
        private static final usersDataBase INSTANCE = new usersDataBase();
    }
    public static usersDataBase getInstance() {
        return SingletonHelper.INSTANCE;
    }
    public boolean addUser (String login,String passCode){
        users.putIfAbsent(login,passCode);
        if((users.get(login)).equals(passCode)==true){
            //if the user exist with the right login and passWord
            //if we add new user successfuly
            return true;
        }
        //Wrong passWord
        return false;
    }
    
}
