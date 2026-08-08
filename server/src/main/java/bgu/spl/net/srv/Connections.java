package bgu.spl.net.srv;

//import java.io.IOException;

public interface Connections<T> {

    boolean send(int connectionId, String msg);

    void send(String channel, String msg);

    void disconnect(int connectionId,String user);
    void addActiveClient(int id,ConnectionHandler<T> connectionHandler);
    boolean addActiveClientLog(int id,String userName);
    boolean addActiveUser(String userName);
    boolean containUser(int id);
    void subscribe(String topic, int connectionId,int subscribeID);
    void unsubscribe(int id,int ConnectionId);
    boolean cheaksSub(int connectionId,String topic);
    void remove(int id);
}
