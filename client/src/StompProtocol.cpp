#include <iostream>
#include <sstream>
#include <vector>
#include <string>
#include <chrono>
#include <ctime>
#include <fstream>
#include <iomanip>
#include "../include/StompProtocol.h"
StompProtocol::StompProtocol(): receiptIdMap(),byChannelName(),idtoChannel(),bySubId(),savedEvents(),userName(""),terminate(false),           
          login(false),               
          receiptId(0),               
          subId(0),                   
          host(""),          
          port(0){}
void StompProtocol:: setUserName(std::string name){
    this->userName=name;
}
void StompProtocol::setHost(std::string hostN){
    this->host=hostN;
}
std::string StompProtocol::getHost(){
    return host;
}
int StompProtocol::getPort(){
    return port;
}
void StompProtocol::setPort(std::string portN){
    int p=std::stoi(portN);
    this->port=p;
}
std::vector<std::string> StompProtocol::parseCommands(std::string command){
std::istringstream stream(command);
    std::string word;
    std::vector<std::string> words;
    while (stream >> word) {
        words.push_back(word);
    }
    return words;
}
std::string StompProtocol:: reportparse(std::string input){
    return input.substr(7);
}
std::vector<std::string> StompProtocol:: splitByNewline(const std::string str) {
    std::vector<std::string> result;
    std::istringstream stream(str);
    std::string line;

    while (std::getline(stream, line)) { 
        result.push_back(line);
    }

    return result;
}
std::vector<std::string> StompProtocol:: splitString(const std::string& input, char delimiter) {
    std::stringstream ss(input);
    std::string token;
    std::vector<std::string> result;

    while (std::getline(ss, token, delimiter)) {
        result.push_back(token);
    }

    return result;
}
std::string StompProtocol::convertToConnet(std::string login,std::string passcode){
    std::string output = "CONNECT\n";
    output += "accept-version:1.2\n";
    output += "host:stomp.cs.bgu.ac.il\n";  
    output += "login:" + login + "\n"; 
    output += "passcode:" + passcode + "\n"; 
    return output;
}
std::string StompProtocol::convertToSubscribe(std::string input,int id,int receipt){
    std::string output = "SUBSCRIBE\n";
    output += "destination:" + input + "\n"; 
    output += "id:" + std::to_string(id) + "\n";
    output += "receipt:" + std::to_string(receipt) + "\n"; 
    return output;
}
std::string StompProtocol::convertToUnSubscribe(int id,int receipt){
    std::string output = "UNSUBSCRIBE\n"; 
    output += "id:" + std::to_string(id) + "\n";
    output += "receipt:" + std::to_string(receipt) + "\n";
    return output;
}
std::string StompProtocol::convertToDisconnect(int receipt){
    std::string output = "DISCONNECT\n"; 
    output += "receipt:" + std::to_string(receipt) + "\n"; 
    return output;
}
std::string StompProtocol::convertToSend(std::string channelName,std::string description){
    std::string output="SEND\n";
    output += "destination:"+channelName+"\n";
    output += description;
    return output;
    
}
std::string StompProtocol::sendsCommands(std::string command){
    std::vector<std::string> parse=parseCommands(command);
    std::string output="";
    if(parse.at(0)=="login"){
        std::string host_port=parse.at(1);
        std::vector<std::string>temp=splitString(host_port,':');
        std::string myhost=temp.at(0);
        std::string myport=temp.at(1);
        setHost(myhost);
        setPort(myport);
        std::string username=parse.at(2);
        std::string password=parse.at(3);
        output=convertToConnet(username,password);

    }
    else if(parse.at(0)=="join"){
        output=convertToSubscribe(parse.at(1),subId,receiptId);
        byChannelName.insert({parse.at(1),subId});
        receiptIdMap.insert({receiptId,output});
        idtoChannel.insert({subId,parse.at(1)});
        subId++;
        receiptId++;
    }
    else if(parse.at(0)=="exit"){
        int id=byChannelName[parse.at(1)];
        output=convertToUnSubscribe(id,receiptId);
        receiptIdMap.insert({receiptId,output});
        receiptId++;
    }
    else if(parse.at(0)=="report"){
        output="send";
    }
    else if(parse.at(0)=="summary"){
        std::string channel=parse.at(1);
        std::string user=parse.at(2);
        if(byChannelName.count(channel)==1){
            std::map<std::string,std::vector<Event>> map=savedEvents[channel];
            if(map.empty())
            std::cout<<"there is no available messages";
            else{
                if(map.count(user)==0)
                std::cout<<"there is no messages from this user";
                else{
                   std::vector<Event> vec=map[user];
                   int active=0;
                   int arrive=0;
                   int Total=vec.size();
                   for(Event event:vec){
                    std::map<std::string,std::string> info=event.get_general_information();
                    if(info["active"]=="true")
                    active++;
                    if(info["forces arrival at scene"]=="true")
                    arrive++;
                   }
                   writeEmergencyUpdatesToFile(active,arrive,Total,user,channel,vec,parse.at(3));

                }
            
            }
        
    }else{
        std::cout<<"you are not subsecripe to this channel";
    }
    }else{
    output=convertToDisconnect(receiptId);
    receiptIdMap.insert({receiptId,output});
    receiptId++;}
    return output;
    }
std::vector<std::string> StompProtocol::sendEvents(std::string filePath){
    names_and_events jsonParse=parseEventsFile(filePath);
        std::string channelName=jsonParse.channel_name;
        std::vector<Event> events=jsonParse.events;
        std::vector<std::string> sendFrames;
        for(Event event:events){
            event.setEventOwnerUser(userName);
            std::string body="user:"+event.getEventOwnerUser()+"\n";
            body=body+"city:"+event.get_city()+"\n";
            body=body+"event name:"+event.get_name()+"\n";
            body=body+"date time:"+std::to_string(event.get_date_time())+"\n";
            std::string active=(event.get_general_information()).at("active");
            std::string forcesArrivalAtScene=(event.get_general_information()).at("forces_arrival_at_scene");
            body=body+"general information:\n";
            body=body+"\tactive:"+active+"\n";
            body=body+"\tforces arrival at scene:"+forcesArrivalAtScene+"\n";
            body=body+"description:\n"+event.get_description();
            sendFrames.push_back(convertToSend(channelName,body));
        }
        return sendFrames;
}
std::string StompProtocol::serverResponse(std::string response){
    std::vector<std::string> splitResponse=splitByNewline(response);
    std::string output="";
    if(splitResponse.at(0)=="RECEIPT"){
        std::vector<std::string> receiptS=splitString(splitResponse.at(1),':');
        int id=std::stoi(receiptS.at(1));
        std::string checks=receiptIdMap[id];
        std::vector<std::string> a=splitByNewline(checks);
        if(a.at(0)=="SUBSCRIBE"){
            std::vector<std::string> b=splitString(a.at(1),':');
            output="Joined channel "+b.at(1);
        }
        else if(a.at(0)=="UNSUBSCRIBE"){
            std::vector<std::string> b=splitString(a.at(1),':');
            int idS=std::stoi(b.at(1));
            output="Exited channel "+idtoChannel[idS];
        }
        else{
            output="Logout successful";
            receiptIdMap.clear();
            byChannelName.clear();
            idtoChannel.clear();
            bySubId.clear();
            savedEvents.clear();
            userName="";
            receiptId=0;;
            subId=0;;
            host="";
            port=0;
        }
    }
    else if(splitResponse.at(0)=="MESSAGE"){
        output="saved";
        int index= response.find("user:");
        std::string des=response.substr(index);
        Event event(des);
        std::string destination=splitString(splitResponse.at(3),':').at(1);
        event.setChannelName(destination);
        savedEvents[event.get_channel_name()][event.getEventOwnerUser()].push_back(event);

    }else if(splitResponse.at(0)=="CONNECTED"){
        output="Login successful";
    }
    else{
        std::vector<std::string> errorS=splitString(splitResponse.at(1),':');
        output=errorS.at(1);
    }
    return output;
}
std::string StompProtocol:: summarize(const std::string& description) {
    if (description.length() > 27) {
        return description.substr(0, 27) + "...";
    } else {
        return description;
    }
}
void StompProtocol:: writeEmergencyUpdatesToFile(int active,int arrive,int size,const std::string& user, const std::string& channel_name,std::vector<Event>& reports, const std::string& file_name){
std::ofstream outFile(file_name);
    
    if (!outFile) {
        std::cerr << "Failed to open the file!" << std::endl;
        return;
    }
    selectionSort(reports);
    int totalReports = size;
    int activeCount = active;
    int forcesArrivalCount = arrive;
    outFile << "Channel " << channel_name << "\nStats:\n";
    outFile << "Total: " << totalReports << "\n";
    outFile << "active: " << activeCount << "\n";
    outFile << "forces arrival at scene: " << forcesArrivalCount << "\n";
    outFile << "Event Reports:\n";
    int reportNum = 1;
    for (const auto& report : reports) {
        outFile << "Report_" << reportNum++ << ":\n";
        outFile << "city: " << report.get_city() << "\n";
        outFile << "date time: " << convertDate(std::to_string(report.get_date_time()))<< "\n";
        outFile << "event name: " << report.get_name() << "\n";
        outFile << "summary: " << summarize(report.get_description()) << "\n";
    }

    outFile.close();
}
std::string StompProtocol::convertDate(std::string date){
     // Convert the string to std::time_t (integer)
    std::time_t epochTime;
    std::istringstream(date) >> epochTime;

    // Convert to a `std::tm` structure for local time
    std::tm* localTime = std::localtime(&epochTime);

    // Format the date and time as a string
    std::ostringstream formattedDate;
    formattedDate << std::put_time(localTime, "%Y-%m-%d %H:%M:%S");
    return formattedDate.str();
}
bool StompProtocol::compareEvents(const Event& e1, const Event& e2) {
    if (e1.get_date_time() == e2.get_date_time()) {
        return e1.get_name() < e2.get_name();
    }
    return e1.get_date_time() < e2.get_date_time();
}
// Selection sort implementation
void StompProtocol::selectionSort(std::vector<Event>& events) {
    size_t n = events.size();

    // Traverse the entire list of events
    for (size_t i = 0; i < n - 1; ++i) {
        // Find the minimum element in the unsorted part of the vector
        size_t min_index = i;
        for (size_t j = i + 1; j < n; ++j) {
            if (compareEvents(events[j], events[min_index])) {
                min_index = j;
            }
        }

        // Swap the found minimum element with the first element of the unsorted part
        if (min_index != i) {
            std::swap(events[i], events[min_index]);
        }
    }
}









