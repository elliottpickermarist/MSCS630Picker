/**
 * file: listenServer.java
 * author: Elliott Picker
 * course: MSCS_630L_711_20S
 * assignment: Project
 * due date: May 12, 2020
 * 
 * This file contains the declaration of the 
 * listenServer class used as a thread in the 
 * chatServer class to asynchronously listen
 * for incoming messages from each chatClient.
 */

import java.net.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.FlowLayout;
import javax.swing.*;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


 /**
  * listenServer 
  * 
  * This class contains runnable listenServer thread
  * to allow the chatServer class to asynchornously listen
  * for messages from each of the chat clients. Chat Server will
  * run one listenServer thread for each connected chat client.
 */
public class listenServer extends Thread {
  ObjectInputStream inS;
  String message;
  ObjectOutputStream outS;
  
   /**
   * listenServer
   *
   * This constructor method instantiates a new listenServer object with 
   * a given inputstream and outputstream. This allows it to listen 
   * for incoming messages from each of the chat clients and process  
   * outgoing messages accordingly accordingly. The output stream is only 
   * neccessary so that at logoff time it can be removed from the servers
   * outgoing output stream list.
   *  
   * Parameters:
   *   ois: an ObjectInputStream to listen for messages from  
   * 
   *   P: a JPanel representing the chat window where new messages should 
   *      be added to 
   * 
   * Return value: the newly created listenClient object 
   */
  public listenServer(ObjectInputStream ois,ObjectOutputStream oos) {
    inS=ois;
    outS=oos;
  }
   /**
   * run
   *
   * This method allows listenServer to extend thread and describes the 
   * listenChat threads behavior. It will wait for incoming messages and 
   * upon receiving one it will call chat server sendMessage method to 
   * prcoess the new incoming message. When the Socket is closed a 
   * SocketException will occur and it will call removeOut method of the 
   * chat server as this client can no longer receive messages.
   *  
   * Parameters: none
   *
   * Return value: none
   */
  public void run() {
    try{
      while(true) {
        message = (String)inS.readObject();
        chatServer.sendMessage(message);
      }
    }
    catch(SocketException SE ) { // likely a user logging off
      System.out.println("processed a logoff");
      chatServer.removeOut(outS); // so server stops broadcasting there 
    }
    catch(Exception e) {
      System.out.println(e);
    }
  } // end run 
} // end listen 