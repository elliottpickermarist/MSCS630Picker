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

//import java.net.*;
//import java.awt.BorderLayout;
//import java.awt.event.*;
//import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
//import javax.swing.*;
//import java.io.*;
//import java.util.Date;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.InetAddress;

 /**
  * keyCreator 
  * 
  * This class is the main client side program for secure chat.
  * It is responsible for connecting to the server and providing
  * the interface for the end user to use.
 */
public class keyCreator {

  
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
    try{
      System.out.println("Please enter the passphrase to be used for key generation (at least 8 characters)");
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
      String passphrase = br.readLine();
      System.out.println("Please enter the serer IP or Enter to use the current local IP address for server");
      String serverip = br.readLine();
      if(serverip.equals("")) {
        serverip = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Using IP: "+serverip);
      }
      System.out.println("Please select destination for the Key file");
      int padCharAmount = 16 - (passphrase.length() % 16);
      StringBuilder sb = new StringBuilder(passphrase);
      for(int i = 0;i<padCharAmount;i++)
        sb.append("E");
      String[] keys = sb.toString().split("(?<=\\G................)");
      byte[] outKey = keys[0].getBytes();
      for(int i =1;i<keys.length;i++)
      {
        outKey = keyXOR(outKey,keys[i].getBytes());
      }
      //System.out.println(java.util.Arrays.toString(outKey));

      JPanel panel= new JPanel(); 
      JFileChooser jfc = new JFileChooser();
      panel.add(jfc);
      jfc.setFileFilter( new FileNameExtensionFilter("SecureChat key file(*.key)","key"));
      jfc.setSelectedFile(new File("Securechat.key"));
      jfc.showSaveDialog(null);
      File file = jfc.getSelectedFile();
      String filepath = file.getPath();
      System.out.println("Generating keyfile: "+filepath);
      FileWriter fw = new FileWriter(filepath);
      BufferedWriter bw = new BufferedWriter(fw);
      //bw.write("heyoooo");
      bw.write(hexVal(outKey));
      bw.write("\n");
      bw.write(serverip);
      bw.close();
      fw.close();
      
    }
    catch(Exception e) {
      System.out.println("Error accessing key file!");
      System.out.println(e);
    }
  }
  public static byte[] keyXOR(byte[] a, byte[] b) {
    byte[] c = new byte[16];
    for(int i=0;i<16;i++)
      c[i] = (byte) (a[i] ^ b[i]);
    return c;
  }
  public static String hexVal(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (int i=0;i<bytes.length;i++) {
      sb.append(String.format("%02X", bytes[i]));
    }
    return sb.toString();
  }
}