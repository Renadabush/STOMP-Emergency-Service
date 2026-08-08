package bgu.spl.net.impl.stomp;

public class MESSAGEframe {
    private String mess;
    private int subscription;
    private int messageId;
    private String topic;
    public MESSAGEframe(String topic,String mess,int subscription,int messageId){
        this.mess=mess;
        this.subscription=subscription;
        this.messageId=messageId;
        this.topic=topic;
    }
    public String getFrameMess(){
        String output = "MESSAGE\n" +
        "subscription:" + subscription + "\n" +
        "message-id:" + messageId + "\n" +
        "destination:"+ topic + "\n\n" 
        + mess + "\n"; 
        return output;
    }
    
}
