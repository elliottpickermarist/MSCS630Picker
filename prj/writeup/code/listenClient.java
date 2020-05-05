/**
 * file: listenClient.java
 * author: Elliott Picker
 * course: MSCS_630L_711_20S
 * assignment: Project
 * due date: May 12, 2020
 * 
 * This file contains the declaration of the 
 * listenClient class used as a thread in the 
 * chatClient class to asynchronously listen
 * for incoming messages from the server.
 */


import java.io.ObjectInputStream;
import javax.swing.JLabel;
import javax.swing.JPanel;

 /**
  * listenClient 
  * 
  * This class contains runnable listenClient thread
  * to allow the chatClient class to asynchornously listen
  * for messages. Each chat client will have one thread running
  * this listenClient class.
 */
public class listenClient extends Thread {
  ObjectInputStream inS;
  JPanel jp;
  String message;
  byte [] messageBytes;
  static final byte[] LOGOFF = {1};  // this will be sent as a message when processing a logoff
  
 /**
 * listenClient
 *
 * This constructor method instantiates a new listenClient object with 
 * a given input stream and jpanel. This allows it to listen for incoming 
 * messages and update the chat window accordingly.
 *  
 * Parameters:
 *   ois: an ObjectInputStream to listen for messages from  
 * 
 *   P: a JPanel representing the chat window where new messages should 
 *      be added to 
 * 
 * Return value: the newly created listenClient object 
 */
  public listenClient(ObjectInputStream ois,JPanel P) {
    inS=ois;
    jp=P;
  }
  
 /**
 * run
 *
 * This method allows listenClient to extend thread and describes the 
 * listenClient threads behavior. It will wait for incoming messages and 
 * upon receiving one it will update the chat window and then repeat,
 * waiting on a new incoming message.   
 *  
 * Parameters: none
 *
 * Return value: none
 */
  public void run() {
    try{      
      while(true) { // listen for messages indefinitely
        messageBytes = (byte[])inS.readObject();
        if (messageBytes.length == 1 && messageBytes[0]==LOGOFF[0]) {
          message = "someone left the chat";
        }
        else {  
          message = chatClient.decrypt(messageBytes);     //DECRYPT MESSAGE HERE
        }            
        jp.add(new JLabel(message+"\n"));
        jp.updateUI();
      }
    }
    catch(Exception e) {
      System.out.println(e);
    } 
  }
}