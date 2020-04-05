/**
 * file: chatServer.java
 * author: Elliott Picker
 * course: MSCS_630L_711_20S
 * assignment: Project
 * due date: May 12, 2020
 * 
 * This file contains the declaration of the 
 * chatServer class which handles the server
 * for the secure chat program.
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.ListIterator;

 /**
  * chatServer 
  * 
  * This class is the main server side program for secure chat.
  * It is responsible for accepting incoming connections from clients
 */
public class chatServer {
  static ServerSocket providerSocket; // the socker that we wait on 
  static Socket connection = null;    // used temporarily for each incoming connection to start listener thread and append out to outlist 
  static ObjectOutputStream out;
  static ArrayList<ObjectOutputStream> outList; 
  static ObjectInputStream in; 
  static String message;
  static BufferedReader br;
  static String userName;

   /**
   * main
   *
   * This method runs the server for the secure chat program.
   * It awaits incoming connections for chat clients and runs a 
   * listenServer thread for each client to await incoming connections  
   *  
   * Parameters: None.
   * 
   * Return value: None. 
   */
  public static void main(String args[]) {
    outList= new ArrayList<ObjectOutputStream>();
    try{
      //Create a Server Socket for clients to connect to 
      providerSocket = new ServerSocket(2004, 10); // this is the socket we listen on 
      //Wait for connections 
      System.out.println("Waiting for connection at "+providerSocket.getLocalSocketAddress());
      System.out.println("Server Started");
      while(true) { // wait for incoming connections indefinitely 
      // there's no need to do anything else because the listeners will call sendMessage as appropriate
        connection = providerSocket.accept();
        System.out.println("Connection received from " + connection.getInetAddress().getHostName());
        //Add the output stream to our outlist 
        out = new ObjectOutputStream(connection.getOutputStream());
        outList.add(out);
        out.flush();
        in = new ObjectInputStream(connection.getInputStream());
        //Add a listenServer to hangle his input stream 
        listenServer l = new listenServer(in,out); // start one input listener for each conection , out is passed so that it can be removed at cleanup
        l.start();
      }
    }
    catch(IOException ioException){
      ioException.printStackTrace();
    }  
    finally{
    //always close all connections
      try{
        in.close();
        out.close();
        providerSocket.close();
      }
      catch(IOException ioException){
        ioException.printStackTrace();
      }
    }  
  } // end run 
  
   /**
   * removeOut
   *
   * This method is called by the listenServer thread for a SocketException
   * We have lost connection to the client, likely due to a disconnect, and 
   * should no longer broadcast messages to it. It will remove the output stream 
   * from the outlist.   
   *  
   * Parameters:
   *   oos: an ObjectOutputStream the stream associated with the terminating client 
   *        to be removed from the outList   
   * 
   * 
   * Return value: None. 
   */
  public static void removeOut(ObjectOutputStream oos){ 
    outList.remove(oos);
  }
  
   /**
   * sendMessage
   *
   * This method is called by the listenServer thread for an incoming messsage
   * The message should be broadcasted to all clients in the outList.  
   *  
   * Parameters:
   *   msg: A String representing the encrypted message to be sent. 
   *        Note: The server is processing an encrypted message and does not 
   *        have the key to decrypt this message text
   * 
   * 
   * Return value: None. 
   */
  public static void sendMessage(String msg) {  //  called by listenServer 
    // optionally show server messages here!!
    System.out.println(msg);
    try{
      ListIterator<ObjectOutputStream> iter=outList.listIterator();
      while(iter.hasNext()) { // send to each recipient
        ObjectOutputStream o=iter.next();
        o.writeObject(msg);  
        o.flush();
      }
    }
    catch(IOException ioException){
      ioException.printStackTrace();
    }
  }
}