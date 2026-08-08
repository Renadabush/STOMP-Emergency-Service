package bgu.spl.net.impl.stomp;

public class ERRORframe {
    public String getPasswordError(){
        return "ERROR\nmessage:Wrong password\n";
    }
    public String getActiveError(){
        return "ERROR\nmessage:User already logged in\n";
    }
    public String getClientError(){
        return "ERROR\nmessage:The client is already logged in, log out before trying again\n";
    }
    public String getUnSubscribe(){
        return "ERROR\nmessage:You are not subsscribed to this channel\n";
    }
    public String getSubscribe(){
        return "ERROR\nmessage:You are subsscribe to this channel\n";
    }


    
}
