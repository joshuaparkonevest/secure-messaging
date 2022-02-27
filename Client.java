// Java implementation for multithreaded chat client
// Save file as Client.java

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;   // Import the FileWriter class
import java.util.Random;

public class Client
{
	final static int ServerPort = 5000;

	public static void main(String args[]) throws UnknownHostException, IOException
	{
        // Check for fingerprint and create one if it doesn't exist
        try {
            // Check for fingerprint file by trying to create one
            File myObj = new File("fingerprint.txt");
            if (myObj.createNewFile()) {
              
              // Creates fingerprint file and writes fingerprint to it
              FileWriter myWriter = new FileWriter("fingerprint.txt");
              String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
              StringBuilder sb = new StringBuilder();
              Random random = new Random();
              int length = 6;
              for(int i = 0; i < length; i++) {
                int index = random.nextInt(alphaNumeric.length());
                char randomChar = alphaNumeric.charAt(index);
                sb.append(randomChar);
              }
              String fingerPrint = sb.toString();
              myWriter.write(fingerPrint);
              myWriter.close();
              System.out.println("Fingerprint created.");

            } else {
              // If fingerprint already exists, it's read from file and put into memory
              Scanner myReader = new Scanner(myObj);
              while (myReader.hasNextLine()) {
                  String fingerPrint = myReader.nextLine();
              }
              myReader.close();
              System.out.println("Read fingerprint from file.");
            }
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

		Scanner scn = new Scanner(System.in);
		
		// getting localhost ip
		InetAddress ip = InetAddress.getByName("localhost");
		
		// establish the connection
		Socket s = new Socket(ip, ServerPort);
		
		// obtaining input and out streams
		DataInputStream dis = new DataInputStream(s.getInputStream());
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());

		// sendMessage thread
		Thread sendMessage = new Thread(new Runnable()
		{
			@Override
			public void run() {
				while (true) {

					// read the message to deliver.
					String msg = scn.nextLine();
					
					try {
						// write on the output stream
						dos.writeUTF(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		// readMessage thread
		Thread readMessage = new Thread(new Runnable()
		{
			@Override
			public void run() {

				while (true) {
					try {
						// read the message sent to this client
						String msg = dis.readUTF();
						System.out.println(msg);
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
		});

		sendMessage.start();
		readMessage.start();

	}
}