package bgu.spl.net.impl.stomp;

public class DISCONNECTframe {
    int receipt;
    public DISCONNECTframe(String input){
        String[] myArray=input.split("\n");
        String[] receiptArray=myArray[1].split(":");
        receipt=Integer.parseInt(receiptArray[1]);
    }
    public int getReceipt(){
        return receipt;
    }
}
