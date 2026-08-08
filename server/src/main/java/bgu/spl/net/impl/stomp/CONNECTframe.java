package bgu.spl.net.impl.stomp;

public class CONNECTframe {
    private String version;
    private String host;
    private String login;
    private String passcode;
    public void convert(String msg) {
        String[] myArray=msg.split("\n");
        String []versionArray=myArray[1].split(":");
        version=versionArray[1];
        String[] hostArray=myArray[2].split(":");
        host=hostArray[1];
        String[] loginArray=myArray[3].split(":");
        login=loginArray[1];
        String[] passcodeArray=myArray[4].split(":");
        passcode=passcodeArray[1];}
    public String getVersion(){
        return version;
    }
    public String getHost(){
        return host;
    }
    public String getLogin(){
        return login;
    }
    public String getPasscode(){
        return passcode;
    }
    
}
