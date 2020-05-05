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


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.io.EOFException;




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
  byte[] messageBytes;
  ObjectOutputStream outS;
  static final byte[] LOGOFF = {1}; // this will be sent as a message when processing a logoff
  
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
        messageBytes = (byte[])inS.readObject();
        chatServer.sendMessage(messageBytes);
      }
    }
    catch(SocketException SE ) { // likely a user logging off
      System.out.println("processed a logoff");
      chatServer.removeOut(outS); // so server stops broadcasting there 
      chatServer.sendMessage(LOGOFF); // tell clients that someone left   
    }
    catch(EOFException EE ) { // A user logging off without ever successfully connecting
    // This happens when a key file including server has been selected but closed before enterring username
      System.out.println("Unsuccessful Connection attempt");
      chatServer.removeOut(outS); // so server stops broadcasting there   
    }
    catch(Exception e) {
      System.out.println(e);
      e.printStackTrace();
    }
  }
} 