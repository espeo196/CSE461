Benjamin Chan & Nicholas Johnson
CSE 461
Autumn 2013

Project 3

The Problem
Our project seeks to create a text-based chat system that runs on local network. 
Clients run the sending and receiving code themselves. The idea is that anybody can run the location-based chatroom client with minimal overhead. 
This chatroom does not has a host that stores all the data and handles all the connections between users, instead, everyone is a peer of each other. 

System Design
The client is run by running ClientRunner which uses two threads: one for passively listening for messages (MulticastClient) and the other for handling user input (ConsoleUI). When the user first runs the program they are joined to a chatroom based on a multicast group (i.e. 225.4.5.14). The user is prompted for a username at the beginning of the session and is provided a list of commands that is easily expandable. Whenever the user types a message and presses enter the message is broadcast to the group they are joined to and others on the same group receive and print out the message.
The Packet class has fields that allow it to serve as a simple ACK, a basic message, or to signify a file transfer. Files and large messages are put into a Message that breaks them up into a collection of Packets. However, the size of file it can handle depends on the performance of the network.

Reflections
We ended up hitting all of our goals in the project and even extended them a bit by adding limited file transfer. The current file transfer function only works for files with smaller file size, we suspect that it is due to the limited bandwidth. This can be improved by adding congestion control and packet retransmit mechanism. With more time we probably would have incorporated Swing/graphical UIs, but for the base project the text ui runs smoothly. Another interesting function to add to this project is to connect the multicast group to multiple local networks. The multicast group is limited to the local network since the router won't broadcast the multicast address outward. Using mechanism like VPN can help to create a virtual multicast group that connects multiple local networks. 

Interesting design decisions
Use of multicast address
Multicast address is a special range of ip address that allows multiple computers to share the same multicast address. So all the client with that address can receive packets that sent to the group. The router bound this address since it doesn't broadcast multicast address outward by default.  
Existing users discovery mechanism
As far as interesting design decisions one of them revolves around how users see what other users are connected to the chatroom. Whenever a new user joins the chatroom it broadcast a brief message letting everybody else know. If the other clients recognize this user as a new user then they respond the same way. These messages are printed out as 'username is in the chatroom' or 'username has left the chatroom' (added and removed from individual list in each client) when the user leaves. The clients can call up the list of active users by using the command /users. Another design decision was the use of the Packet class as an all-encompassing ACK, FIN, and message class with a flag to designate what type it was. We decided on this because a lot of the code would be the same between the three and they are simple enough to be brought into one class.
Packet Splitting
Usually messages can be contain in one UDP packet, however larger files need to be split up into several small files. This program splits messages into packages and combines them. Multiple sources can send packets at the same time since the flags in it uniquely identify the messages. Although it now assumes that packets are arrived in order and there are no packets lost, ordering and retransmit mechanism can be implemented easily.

