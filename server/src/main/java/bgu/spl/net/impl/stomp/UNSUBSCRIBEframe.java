package bgu.spl.net.impl.stomp;

public class UNSUBSCRIBEframe {
    private int id; 
    private int receipt;
    public UNSUBSCRIBEframe(String input){
        String[] myArray=input.split("\n");
        String[] idArray=myArray[1].split(":");
        id=Integer.parseInt(idArray[1]);
        String[] receiptArray=myArray[2].split(":");
        receipt=Integer.parseInt(receiptArray[1]);  
    }
    public int getId(){
        return id;
    }
    public int receipt(){
        return receipt;
    }
}
