package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Server;

public class StompServer {

    public static void main(String[] args) {
        if(args[1].equals("tpc")){
        Server.threadPerClient(
            Integer.parseInt(args[0]), 
            () -> new MessagingProtocolIMPL(), 
            () -> new MessageEncoderDecoderIMPL(),new ConnectionsImpl<String>())
    .serve();
      }
    if(args[1].equals("reactor")){
        Server.reactor(Runtime.getRuntime().availableProcessors(),
            Integer.parseInt(args[0]), 
            () -> new MessagingProtocolIMPL(),
            () -> new MessageEncoderDecoderIMPL(),new ConnectionsImpl<String>())
        .serve();
    }
    }
    }

