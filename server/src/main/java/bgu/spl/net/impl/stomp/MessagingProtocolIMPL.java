package bgu.spl.net.impl.stomp;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.usersDataBase;

public class MessagingProtocolIMPL implements StompMessagingProtocol<String> {
    private int connectionId;
    private Connections<String> connections;
    private usersDataBase dataBase;
    private boolean terminate;
    private String user;

    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId=connectionId;
        this.connections=connections;
        dataBase=usersDataBase.getInstance();
    }

    @Override
    public void process(String msg) {
        String[] StringArray=msg.split("\n");
        if(StringArray[0].equals("CONNECT")){
            CONNECTframe connect=new CONNECTframe();
            connect.convert(msg);
            if(!connections.containUser(connectionId)){
                if(dataBase.addUser(connect.getLogin(),connect.getPasscode())){
                boolean isActive=connections.addActiveUser(connect.getLogin());
                if(isActive){
                    connections.addActiveClientLog(connectionId, connect.getLogin());
                    String output=(new CONNECTEDframe()).getCONNECTEDframe();
                    connections.send(connectionId,output);
                    user=connect.getLogin();      
                }
                else{
                    String output=(new ERRORframe()).getActiveError();
                    connections.send(connectionId,output);
                }
            }
            else{
                String output=(new ERRORframe()).getPasswordError();
                connections.send(connectionId,output);
            }}
            else{
                String output=(new ERRORframe()).getClientError();
                connections.send(connectionId,output);
            }
        }else if(StringArray[0].equals("SUBSCRIBE")){
            SUBSCRIBEframe subscrib=(new SUBSCRIBEframe(msg));
            if(connections.cheaksSub(connectionId,subscrib.getDestination())){
                connections.send(connectionId,new ERRORframe().getSubscribe());
            }
            else{
            connections.subscribe(subscrib.getDestination(),connectionId, subscrib.getId());
            String output=(new RECEIPTframe(subscrib.receipt()).getRECEIPframe());
            connections.send(connectionId,output);}}
        
        else if(StringArray[0].equals("UNSUBSCRIBE")){
            UNSUBSCRIBEframe unsubscrib=new UNSUBSCRIBEframe(msg);
            connections.unsubscribe(unsubscrib.getId(),connectionId);
            String output=(new RECEIPTframe(unsubscrib.receipt())).getRECEIPframe();
            connections.send(connectionId,output);
        }
        else if(StringArray[0].equals("SEND")){
           SENDframe send=new SENDframe(msg);
           boolean isSub=connections.cheaksSub(connectionId, send.getDestiation());
           if(isSub){
            connections.send(send.getDestiation(), send.getMess());
           }
           else{
            String output=(new ERRORframe().getUnSubscribe());
            connections.send(connectionId,output);
        }}
        else{
            DISCONNECTframe disconnec=new DISCONNECTframe(msg);
            connections.disconnect(connectionId,user);
            String output=(new RECEIPTframe(disconnec.getReceipt())).getRECEIPframe();
            connections.send(connectionId,output);
            connections.remove(connectionId);
        }}

    
    @Override
    public boolean shouldTerminate() {
        return false;
    }
    
}
