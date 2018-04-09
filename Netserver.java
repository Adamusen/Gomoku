package mainP;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JOptionPane;

public class Netserver { //The network server class, containing the voids needed to run a server
	ArrayList clientOutputStreams; //Array list containing the messages
	int Sport; //Standard network port, asked from main class

	public class ClientHandler implements Runnable { //Class that handles the connected clients
		BufferedReader reader; //variable of Buffer reader
	    Socket sock; //variable to a socket used by a client
	     
	    public ClientHandler(Socket clientSocket) { //A newly generated ClientHandler type for the new clients
	    	try {  
	    		sock = clientSocket;
	    		InputStreamReader isReader = new InputStreamReader(sock.getInputStream()); 
	    		reader = new BufferedReader(isReader);
	    	}catch (Exception ex) {ex.printStackTrace();}
	    }
	    
	    public void run() { //Runnable thread that handles the incoming messages
	    	String message;
	    	try {
	    		while ((message = reader.readLine()) != null) {
	    			tellEveryone(message);
	    		}
	    	}catch (Exception ex) {ex.printStackTrace();}
	    }
	}

	public class ClientInn implements Runnable { //Class that handles the connecting clients
		public void run() { //It's runnable thread
			try {
				ServerSocket serverSock = new ServerSocket(Sport);
				JOptionPane.showMessageDialog(null,"Server Started!");
				while (true) {
					Socket clientSocket = serverSock.accept();
					PrintWriter writer = new PrintWriter(clientSocket.getOutputStream()); 
					clientOutputStreams.add(writer);
					Thread t = new Thread (new ClientHandler(clientSocket));
					t.start();
				}
			} catch (Exception ex) {ex.printStackTrace();}
		} 
	}
	
	public void start (int port) { //Void that starts the server
		clientOutputStreams = new ArrayList();
		Sport = port;
		
		Thread ci = new Thread (new ClientInn());
		ci.start();
	}
	    
	    
	public void tellEveryone(String message) { //Void that sends the newly incoming messages to every client
		Iterator it = clientOutputStreams.iterator(); 
		while(it.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter) it.next(); writer.println(message);
				writer.flush();
				//System.out.println(message);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
