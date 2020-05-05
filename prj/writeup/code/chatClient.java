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


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

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
  static String key;
  static String userName;
  static JTextField field;
  static JButton button1;
  static JScrollPane jsp;
  static JPanel P;
  static JPanel bottomP;
  static JFrame JF; 
  static Cipher encryptCipher;
  static Cipher decryptCipher;
  static final String AES = "AES";
  static final int JFWIDTH = 550;
  static final int JFHEIGHT = 300;
  static final int JTFCOLUMNS = 40;
  static final int PORT = 2004;
  
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
      locateKeyFile();      // locate keyfile which includes server ip
      initializeCiphers(); //initialize Ciphers based on key value read 
      connectToServer();
      promptForUserName();
      buildGUI();
      Thread.sleep(Integer.MAX_VALUE);  // stay alive forever during chat exchange;
      // nothing is needed here because actionlisteners will process outgoing messages and listenClient will process incoming
    }
    catch(Exception e) {
      System.out.println(e);
    }
    finally{
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
  } 
  
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
      byte[] msgBytes;
      msgBytes = encrypt(msg);
      out.writeObject(msgBytes);
      out.flush();
    }
    catch(IOException ioException){
      ioException.printStackTrace();
    }
  }
  
   /**
   * locateKeyFile
   *
   * This method builds the panel to prompt the user for 
   * the key file to be used. It then sets the key and ip 
   * files appropriately
   *
   * Parameters:   None.
   *
   * Return value: None. 
   */
  public static void locateKeyFile() throws Exception {
    JPanel panel= new JPanel(); 
    JFileChooser jfc = new JFileChooser();
    panel.add(jfc);
    jfc.setDialogTitle("Please locate SecureChat key file");
    jfc.setFileFilter( new FileNameExtensionFilter("SecureChat key files(*.key)","key"));
    jfc.setSelectedFile(new File("Securechat.key"));
    jfc.showOpenDialog(null);
    try{
      File file = jfc.getSelectedFile();
      BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
      key = br.readLine();
      ip = br.readLine();
    }
    catch(Exception e) {
      System.out.println("Error accessing key file!");
      throw(e);
    }
    
  }
  
   /**
   * buildGUI
   *
   * This method is called at startup to create the GUI for the chat Client
   * It creates a JPanel and attaches the send button to it. It then adds
   * the ActionListener for both enter key and send button requests.   
   * 
   * Parameters:   None. 
   *   
   * Return value: None. 
   */
  public static void buildGUI() throws Exception {
    JF = new JFrame(); 
    P = new JPanel();
    jsp=new JScrollPane(P,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    JF.add(jsp);
    bottomP=new JPanel();
    field= new JTextField(JTFCOLUMNS);
    bottomP.add(field,BorderLayout.SOUTH);
    button1 = new JButton("SEND");
    bottomP.add(button1,BorderLayout.EAST);  
    JF.add(bottomP, BorderLayout.SOUTH); 
    JF.setTitle(userName+"- SecureChat by Elliott Picker");
    JF.setSize(JFWIDTH,JFHEIGHT);
    JF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JF.setVisible(true);
    P.setLayout(new BoxLayout(P, BoxLayout.Y_AXIS));
    AdjustmentListener AL = new AdjustmentListener() { // allows chat to stay scrolled to bottom   
      public void adjustmentValueChanged(AdjustmentEvent e) {  
        e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
      }
    };
    jsp.getVerticalScrollBar().addAdjustmentListener(AL);
    try {
      listenClient l = new listenClient(in,P);
      l.start(); // have one thread updating with any incoming messages while this thread processes outgoing messages
      DateFormat dateFormat = new SimpleDateFormat("h:mm:ss a");
      Date date = new Date();
      sendMessage(userName+" joined chat at "+dateFormat.format(date)); // send login message 
      ActionListener clickSend = new ActionListener() { // action listener used for both send and enter key 
        public void actionPerformed(ActionEvent e) {
          Date date = new Date();
          sendMessage(userName+"("+dateFormat.format(date)+"): "+field.getText());  
          field.setText("");
        }
      }; // end action listener
      field.addActionListener(clickSend);  // have enter key and button have same effect 
      button1.addActionListener(clickSend);
    }
    catch (Exception e) {
      System.out.println("Error building GUI");
      throw(e);
    }
  }
  
   /**
   * encrypt
   *
   * This method encrypts an input String based on the encryptCipher  
   * previously initialized.    
   * 
   * Parameters:  
   *   strClearText: a String representing the text to be encrypted  
   *   
   * Return value: A byte array representing the encrypted value. 
   */
  public static byte[] encrypt(String strClearText){  
    try {
      return encryptCipher.doFinal(strClearText.getBytes());
    } 
    catch (Exception e) {
      System.out.println("CANT ENCRYPT: |"+strClearText+"|");
      System.out.println(e);
    }
  return null; // wasn't able to encrypt but return something
  }

   /**
   * decrypt
   *
   * This method encrypts an input byte array based on the decryptCipher  
   * previously initialized.    
   * 
   * Parameters:  
   *   strBytes: A byte array representing encrypted text 
   *   
   * Return value: A String representing the plain text value of the input 
   */
  public static String decrypt(byte[] strBytes){
    try {
      return new String(decryptCipher.doFinal(strBytes));   
    }
    catch (Exception e) {
      System.out.println("CANT DECRYPT: |"+new String(strBytes)+"|");
      System.out.println(e);
    }
  return null;
  } 
  
   /**
   * initializeCiphers 
   *
   * This method is called at startup to initialize the encrypt and decrypt 
   * Ciphers based on the key value previously assigned.  
   * 
   * Parameters:   None. 
   *   
   * Return value: None. 
   */
  public static void initializeCiphers() throws Exception {
    try {     
      encryptCipher=Cipher.getInstance(AES);
      encryptCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(),AES));
      decryptCipher=Cipher.getInstance(AES);
      decryptCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(),AES));   
    }
    catch(Exception e) {
      System.out.println("error initializing ciphers!");
      throw(e);
    }    
  }      
  
   /**
   * connectToServer
   *
   * This method is called at startup to establish the input and  
   * output streams to communicate with the server.
   * 
   * Parameters:   None. 
   *   
   * Return value: None. 
   */
  public static void connectToServer() throws Exception {
    try{  
      //create a socket to connect to the server
      requestSocket = new Socket(InetAddress.getByName(ip),PORT);
      //get input and output streams from server 
      out = new ObjectOutputStream(requestSocket.getOutputStream());
      out.flush();
      in = new ObjectInputStream(requestSocket.getInputStream());
    }
    catch(UnknownHostException unknownHost){
      System.out.println("You are trying to connect to an unknown host!");
    }
    catch(IOException ioException){
      ioException.printStackTrace();
    }
    catch(Exception e) {
      System.out.println("error connecting to Server!");
      throw(e);
    } 
  }      
  
   /**
   * promptForUserName
   *
   * This method is called at startup to prmpt the user for their name.  
   * 
   * Parameters:   None. 
   *   
   * Return value: None. 
   */
  public static void promptForUserName() throws Exception {
    try{  
      JFrame frame = new JFrame();
      frame.setTitle("SecureChat");
      userName = JOptionPane.showInputDialog(frame, "Please Enter your name:","SecureChat",JOptionPane.INFORMATION_MESSAGE).toString();
    }
    catch(Exception e) {
      System.out.println("error processing user name!");
      throw(e);
    } 
  }      
}