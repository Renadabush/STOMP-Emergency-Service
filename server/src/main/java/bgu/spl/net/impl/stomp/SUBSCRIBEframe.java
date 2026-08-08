package bgu.spl.net.impl.stomp;

public class SUBSCRIBEframe {
    private String destination;
    private int id;
    private int receipt;
    public SUBSCRIBEframe(String input){
        String[] myArray=input.split("\n");
        String []destinationArray=myArray[1].split(":");
        destination=destinationArray[1];
        String[] idArray=myArray[2].split(":");
        id=Integer.parseInt(idArray[1]);
        String[] receiptArray=myArray[3].split(":");
        receipt=Integer.parseInt(receiptArray[1]);
    }
    public String getDestination(){
        return destination;
    }
    public int getId(){
        return id;
    }
    public int receipt(){
        return receipt;
    }
}
