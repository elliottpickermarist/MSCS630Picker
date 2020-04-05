/**
 * file: chatClient.java
 * author: Elliott Picker
 * course: MSCS_630L_711_20S
 * assignment: Project
 * due date: May 12, 2020
 * 
 * This file contains the declaration of the 
 * chatClient class which builds the main 
 * UI and runs the secure chat program 
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
  * chatClient 
  * 
  * This class is the main client side program for secure chat.
  * It is responsible for connecting to the server and providing
  * the interface for the end user to use.
 */
public class chatClient {
  final static long serialVersionUID=1;
  static Socket requestSocket;
  static ObjectOutputStream out;
  static ObjectInputStream in;
  static String message;
  static String ip;
  static String userName;
  static JTextField field;
  static JButton button1;
  static JScrollPane jsp;
  static JPanel P;
  static JPanel bottomP;
  static JFrame JF; 
  
   /**
   * main
   *
   * This method runs the client side of the secure chat program.
   * It connects to the chat server and creates a listenClient thread 
   * to handle incoming messages from the chat server. It then waits for  
   * outgoing messages from the user and sends them to the server through  
   * a ObjectOutputStream.
   *  
   * Parameters: None.
   * 
   * Return value: None. 
   */
  public static void main(String args[]) {
    if (args.length==1)
      ip=args[0];
    else
      ip=null;
    JF = new JFrame();  
    try{
      //create a socket to connect to the server
      if(ip == null) // used locally for testing 
        requestSocket = new Socket("localhost", 2004);
      else
        requestSocket = new Socket(InetAddress.getByName(ip), 2004);
      System.out.println("Connected port 2004");
      //get input and output streams from server 
      out = new ObjectOutputStream(requestSocket.getOutputStream());
      out.flush();
      in = new ObjectInputStream(requestSocket.getInputStream());
      // get username
      JFrame frame = new JFrame();
      userName = JOptionPane.showInputDialog(frame, "Please Enter your name:").toString();
      //build gui
      buildGUI();
      listenClient l = new listenClient(in,P);
      l.start(); // have one thread updating with any incoming messages while this thread processes outgoing messages
      DateFormat dateFormat = new SimpleDateFormat("h:mm:ss a");
      Date date = new Date();
      sendMessage(userName+" joined chat at "+dateFormat.format(date)); // send login message 
      ActionListener clickSend = new ActionListener() { // action listener used for both send and enter key 
        public void actionPerformed(ActionEvent e) {
          //DateFormat dateFormat = new SimpleDateFormat("h:mm:ss a");
          Date date = new Date();
          sendMessage(userName+"("+dateFormat.format(date)+"): "+field.getText());  // we could do encryption here??
          field.setText("");
        }
      }; // end action listener
      field.addActionListener(clickSend);  
      button1.addActionListener(clickSend);
      while(true); // stay alive forever during chat exchange;
      // nothing is needed here because actionlisteners will process outgoing messages and listenClient will process incoming
    }
    catch(UnknownHostException unknownHost){
      System.err.println("You are trying to connect to an unknown host!");
    }
    catch(IOException ioException){
      ioException.printStackTrace();
    }
    finally{
     /* // attempt to issue logoff message
      try {
        sendMessage("logoff - "+userName);
      }
      catch(Exception e) {
        System.out.println(e); 
      }
      */
      //always close the connections
      try{
        in.close();
        out.close();
        requestSocket.close();
      }
      catch(IOException ioException){
        ioException.printStackTrace();
      }
    }
  } // end run 
  
   /**
   * sendMessage
   *
   * This method sends a specified message to the ObjectOutputStream
   * for the chat server to process. 
   *
   * Parameters:
   *   msg: a String representing the message to be sent 
   *   
   * Return value: None. 
   */
  public static void sendMessage(String msg) {
    try{
      out.writeObject(msg);
      out.flush();
    }
    catch(IOException ioException){
      ioException.printStackTrace();
    }
  }
  
   /**
   * buildGUI
   *
   * This method is called at startup to create the GUI for the chat Client
   * It creates a JPanel and attaches the send button to it. Note the action listener 
   * responsible for the send button and enter will be created in the main method !!!!!!!!!!!!!!!!!!!!!!!!why ? cant we move it down here?  
   * 
   *   
   * Return value: None. 
   */
  public static void buildGUI() {
      P = new JPanel();
      jsp=new JScrollPane(P,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      JF.add(jsp);
      bottomP=new JPanel();
      field= new JTextField(40);
      bottomP.add(field,BorderLayout.SOUTH);
      button1 = new JButton("SEND");
      bottomP.add(button1,BorderLayout.EAST);  
      JF.add(bottomP, BorderLayout.SOUTH); 
      JF.setTitle(userName+"- SecureChat by Elliott Picker");
      JF.setSize(550,300);
      JF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JF.setVisible(true);
      P.setLayout(new BoxLayout(P, BoxLayout.Y_AXIS));
  }
}