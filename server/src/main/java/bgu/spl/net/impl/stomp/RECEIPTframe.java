package bgu.spl.net.impl.stomp;

public class RECEIPTframe {
    private int receipt;
    public RECEIPTframe(int receipt){
        this.receipt=receipt;
    }
    public String getRECEIPframe(){
        String output="RECEIPT\nreceipt-id:" + receipt + "\n";
        return output;
    }
}
