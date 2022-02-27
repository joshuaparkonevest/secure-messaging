// Java implementation of Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import java.io.*;
import java.util.*;
import java.net.*;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;   // Import the FileWriter class

// Server class
public class Server
{

	// Vector to store active clients
	static Vector<ClientHandler> ar = new Vector<>();
	
	// counter for clients
	static int i = 0;

	public static void main(String[] args) throws IOException
	{
        // On startup, check for fingerprint list and creates it if needed
        try {
            // Check for fingerprint file by trying to create one
            File myObj = new File("fingerprintList.txt");
            if (myObj.createNewFile()) {

            }
        } catch (IOException e) {
				
            e.printStackTrace();
        }
		// server is listening on port 5000
		ServerSocket ss = new ServerSocket(5000);
		
		Socket s;
		
		// running infinite loop for getting
		// client request
		while (true)
		{
			// Accept the incoming request
			s = ss.accept();

			System.out.println("New client request received : " + s);
			
			// obtain input and output streams
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			
			System.out.println("Creating a new handler for this client...");

			// Create a new handler object for handling this request.
			ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos);

			// Create a new Thread with this object.
			Thread t = new Thread(mtch);
			
			System.out.println("Adding this client to active client list");

			// add this client to active clients list
			ar.add(mtch);

			// start the thread.
			t.start();

			// increment i for new client.
			// i is used for naming only, and can be replaced
			// by any naming scheme
			i++;

		}
	}
}

// ClientHandler class
class ClientHandler implements Runnable
{
	Scanner scn = new Scanner(System.in);
	private String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s;
	boolean isloggedin;
	
	// constructor
	public ClientHandler(Socket s, String name,
							DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		this.name = name;
		this.s = s;
		this.isloggedin=true;
	}

	@Override
	public void run() {

		String received;
		while (true)
		{
			try
			{
				// receive the string
				received = dis.readUTF();
				
				System.out.println(received);
				
				if(received.equals("logout")){
					this.isloggedin=false;
					this.s.close();
					break;
				}
				
				// break the string into message and recipient part
				StringTokenizer st = new StringTokenizer(received, "#");
				String MsgToSend = st.nextToken();
				String recipient = st.nextToken();

				// search for the recipient in the connected devices list.
				// ar is the vector storing client of active users
				for (ClientHandler mc : Server.ar)
				{
					// if the recipient is found, write on its
					// output stream
					if (mc.name.equals(recipient) && mc.isloggedin==true)
					{
						mc.dos.writeUTF(this.name+" : "+MsgToSend);
						break;
					}
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		try
		{
			// closing resources
			this.dis.close();
			this.dos.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
