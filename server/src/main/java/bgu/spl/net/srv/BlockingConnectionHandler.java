package bgu.spl.net.srv;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.stomp.MessageEncoderDecoderIMPL;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final StompMessagingProtocol<T>  protocol;
    private final MessageEncoderDecoderIMPL encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoderIMPL reader, StompMessagingProtocol<T>  protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                String nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                    
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    } 

    

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }
    public StompMessagingProtocol<T> gStompMessagingProtocol(){
        return protocol;
    }

    @Override
    public void send(String msg){
        try {
            out.write(encdec.encode(msg));
            out.flush();
        } catch (IOException e) {
            // Handle the exception (e.g., log the error, retry, or clean up resources)
            System.err.println("Error writing to the socket: " + e.getMessage());
           e.printStackTrace(); // Optional for debugging purposes
        }
    }
}
