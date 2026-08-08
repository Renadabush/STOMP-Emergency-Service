package bgu.spl.net.impl.stomp;

public class CONNECTEDframe {
    private String version;
    public CONNECTEDframe(){
        this.version="1.2";
    }
    public String getCONNECTEDframe(){
        String output="CONNECTED\nversion:" + version + "\n";
        return output;
    }
}
