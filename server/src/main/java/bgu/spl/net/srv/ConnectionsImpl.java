package bgu.spl.net.srv;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.net.impl.stomp.MESSAGEframe;

public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer,ConnectionHandler<T>>ActiveClients;
    private ConcurrentHashMap<String,ConcurrentHashMap<Integer,Integer>> ChannelSub;
    private ConcurrentHashMap<String,Boolean> activeUsers;
    private ConcurrentHashMap<Integer,String> clientsLog;
    private AtomicInteger messageId;
    public ConnectionsImpl(){
        ActiveClients=new ConcurrentHashMap<>();
        ChannelSub=new ConcurrentHashMap<>();
        activeUsers=new ConcurrentHashMap<>();
        clientsLog=new ConcurrentHashMap<>();
        messageId=new AtomicInteger(0);
    }
    public void subscribe(String topic, int connectionId,int subscribeID){
        ChannelSub.putIfAbsent(topic, new ConcurrentHashMap<>());
        (ChannelSub.get(topic)).put(connectionId,subscribeID);
    }
    public void addActiveClient (int id,ConnectionHandler<T> connectionHandler){
        ActiveClients.put(id, connectionHandler);
    }
    public boolean addActiveClientLog(int id,String userName){
        String res=clientsLog.putIfAbsent(id,userName);
        if(res==null)
        return true;
        return false;
    }
    public boolean addActiveUser(String userName){
        Boolean result=activeUsers.putIfAbsent(userName,true);
        if(result==null ||(activeUsers.get(userName)==false))
        return true;
        else 
        return false;
    }
    public boolean send(int connectionId, String msg){
        if(ActiveClients.containsKey(connectionId)){
        ConnectionHandler<T> connectionHandler=ActiveClients.get(connectionId);
        connectionHandler.send(msg);
        return true;}
        return false;


    }

    public void send(String channel, String msg){
        ConcurrentHashMap<Integer,Integer> subsicripers=ChannelSub.get(channel);
        subsicripers.forEach((key,value)->{
            String mess=(new MESSAGEframe(channel, msg, value, messageId.getAndIncrement())).getFrameMess();
            ActiveClients.get(key).send(mess);
        }
        );
    }

    public void disconnect(int connectionId,String user){
        Iterator<Map.Entry<String,ConcurrentHashMap<Integer,Integer>>>iterator=ChannelSub.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,ConcurrentHashMap<Integer,Integer>> entry = iterator.next();
            entry.getValue().remove(connectionId);    
        } 
        activeUsers.remove(user);
        activeUsers.put(user, false);
    }
    @Override
    public boolean containUser(int id) {
        return clientsLog.containsKey(id);
    }
    @Override
    public void unsubscribe(int id,int ConnectionId) {
        Iterator<Map.Entry<String,ConcurrentHashMap<Integer,Integer>>>iterator=ChannelSub.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,ConcurrentHashMap<Integer,Integer>> entry = iterator.next();
            if (entry.getValue().containsKey(ConnectionId)&&(entry.getValue().get(ConnectionId).equals(id))) {
                entry.getValue().remove(ConnectionId);
                break;  
            }
        }
    }
    @Override
    public boolean cheaksSub(int connectionId, String topic) {
        ConcurrentHashMap<Integer,Integer>checks=ChannelSub.get(topic);
        if(checks!=null){
            if(checks.containsKey(connectionId))
            return true;
            else
            return false;
        }
        return false;
    }
    public void remove(int id){
        ActiveClients.remove(id);
        clientsLog.remove(id);
    }
    
    
}
