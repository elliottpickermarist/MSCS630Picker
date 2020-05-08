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


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.InetAddress;

 /**
  * keyCreator 
  * 
  * This class includes the main keyCreator method which will
  * prompt for a passphrase to use in key generation
  * and prompt for the destination server ip address and store
  * both in a newly generated key file 
 */
public class keyCreator {

   public static int NUMBYTESINKEY = 128 / 8; //128 bits, 8 bits per byte = 16 bytes or 16 chars 
   public static char PADCHAR = 'E';
      
   /**
   * main
   *
   * This method runs the key creator program.
   * It will prompt for a passphrase to use in key generation
   * and prompt for the destination server ip address and store
   * both in a newly generated key file 
   *  
   * Parameters: None.
   * 
   * Return value: None. 
   */
  public static void main(String args[]) {
    try{
      System.out.println("Please enter the passphrase to be used for key generation (at least 16 characters)");
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
      String passphrase = br.readLine();
      System.out.println("Please enter the server IP or Enter to use the current local IP address for server");
      String serverip = br.readLine();
      if(serverip.equals("")) {
        serverip = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Using IP: "+serverip);
      }
      System.out.println("Please select destination for the Key file");
      int PADCHARAmount = NUMBYTESINKEY - (passphrase.length() % NUMBYTESINKEY);
      StringBuilder sb = new StringBuilder(passphrase);
      //pad to be an even increment of 8 character strings 
      for(int i = 0;i<PADCHARAmount;i++)
        sb.append("E");
      // break into 16 4-byte  chunks to XOR
      String[] keys = sb.toString().split("(?<=\\G................)");
      byte[] outKey = keys[0].getBytes(); // initialize to the first 16 byte chunk
      for(int i =1;i<keys.length;i++)
      {
        outKey = keyXOR(outKey,keys[i].getBytes());
      }
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
  
   /**
   * keyXOR 
   *
   * This method performs an exclusive or operation on two byte arrays.
   * It assumes each array is of equal size and iterates through using 
   * the ^ operation  
   *  
   * Parameters:
   *   a: the first byte array to be used for XOR  
   *   b: the second byte array to be used for XOR    
   * 
   * 
   * Return value: A byte array representing the result of the XOR operation 
   */
  public static byte[] keyXOR(byte[] a, byte[] b) {
    byte[] c = new byte[NUMBYTESINKEY];
    for(int i=0;i<NUMBYTESINKEY;i++)
      c[i] = (byte) (a[i] ^ b[i]);
    return c;
  }
  
   /**
   * hexVal 
   *
   * This method creates a printable hex  string from an array of bytes 
   *  
   * Parameters:
   *   bytes: the byte array to be converted to String     
   * 
   * 
   * Return value: A String representing the hex value of the input arrray
   */
  public static String hexVal(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (int i=0;i<bytes.length;i++) {
      sb.append(String.format("%02X", bytes[i]));
    }
    return sb.toString();
  }
}