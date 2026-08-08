#pragma once

#include "../include/ConnectionHandler.h"
#include "../include/event.h"
#include <map> 

// TODO: implement the STOMP protocol
class StompProtocol
{
private:
std::map<int,std::string> receiptIdMap;
std::map<std::string,int> byChannelName;
std::map<int,std::string> idtoChannel;
std::map<int,std::string> bySubId;
std::map<std::string, std::map<std::string, std::vector<Event>>> savedEvents;
std::vector<std::string> splitString(const std::string& input, char delimiter);
std::string userName;
bool terminate;
bool login;
int receiptId;
int subId;
std::string host;
int port;
public:
StompProtocol();
void setUserName(std::string name);
void setHost(std::string hostN);
void setPort(std::string portN);
std::string getHost();
int getPort();
std::vector<std::string> parseCommands(std::string command);
std::string convertToConnet(std::string login,std::string passcode);
std::string convertToSubscribe(std::string input,int id,int recipt);
std::string convertToUnSubscribe(int id,int recipt);
std::string convertToDisconnect(int receipt);
std::string connectResponse(std::string response);
std::vector<std::string> splitByNewline(const std::string str);
std::string sendsCommands(std::string command);
std::string convertToSend(std::string channelName,std::string description);
std::vector<std::string> sendEvents(std::string filePath);
std::string serverResponse(std::string response);
std::string convertDate(std::string date);
std::string summarize(const std::string& description);
bool compareEvents(const Event& e1, const Event& e2);
void selectionSort(std::vector<Event>& events);
void writeEmergencyUpdatesToFile(int active,int arrive,int size,const std::string& user, const std::string& channel_name,std::vector<Event>& reports, const std::string& file_name);
std::string reportparse(std::string input);
};
