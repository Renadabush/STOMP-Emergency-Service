package bgu.spl.net.impl.stomp;

public class SENDframe {
    private String destination;
    private String message;
    public SENDframe(String input){
        String[]arrays=input.split("\n");
        destination=arrays[1].substring(12);
        int index=input.indexOf("user:");
        message=input.substring(index);
    }
    public String getDestiation(){
        return destination;
    }
    public String getMess(){
        return message;
    }
    
}
