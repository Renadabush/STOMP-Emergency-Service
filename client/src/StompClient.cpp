#include <string>
#include <iostream>
#include <mutex>
#include <vector>
#include "../include/StompProtocol.h"
#include <thread>
std::mutex socketMutex;
void ReadFromSocket(StompProtocol &protocol, ConnectionHandler &handler,bool &terminate,bool &connect){
	std::string response;
    while(!terminate){
        response ="";
		handler.getLine(response);
		std::string output = protocol.serverResponse(response);
		if(output=="saved")
		continue;
        std::cout << output << std::endl;
        if(output=="Logout successful"||(protocol.splitByNewline(response).at(0))=="ERROR")
        {
            terminate=true;
			connect=false;
            handler.close();
        }
    }
    

}
int main(int argc, char *argv[]) {
	bool connect=false;
	std::string input;
	while(!connect){
		StompProtocol stompP;
		bool terminate=false;
		ConnectionHandler connectionHandler(stompP.getHost(), stompP.getPort());
		std::getline(std::cin, input);
		std::string toSend=stompP.sendsCommands(input);
		std::string res="";
		connectionHandler.host_=stompP.getHost();
		connectionHandler.port_=stompP.getPort();
		if(stompP.parseCommands(input)[0]=="login"){	
        if (!connectionHandler.connect()) {
            std::cerr << "Cannot connect to " << stompP.getHost() << ":" << stompP.getPort() << std::endl;
            continue;
        }
		connectionHandler.sendLine(toSend);
        connectionHandler.getLine(res);
        std::string output=stompP.serverResponse(res);
		std::cout<<output<<std::endl;
        if(output!="Login successful"){
            connectionHandler.close();
            continue;
        }
		stompP.setUserName(stompP.parseCommands(input)[2]);
		connect = true;
		}else{
			std::cout<<"Should login fisrt!"<<std::endl;
		}

		std::thread reader(&ReadFromSocket, std::ref(stompP), std::ref(connectionHandler),std::ref(terminate),std::ref(connect));

    while(!terminate){
		getline(std::cin, input);
		std::string output=stompP.sendsCommands(input);
		if(output=="send"){
			std::string path=(stompP.reportparse(input));
			std::vector<std::string> sends=stompP.sendEvents(path);
			for(std::string toS:sends){
				connectionHandler.sendLine(toS);
			}
			std::cout<<"reported"<<std::endl;
			}
		else if(output==""){
			continue;
		}
			
		else{
			connectionHandler.sendLine(output);
		}
        if(input=="logout"){
        reader.join();}
}}
return 0;
}






